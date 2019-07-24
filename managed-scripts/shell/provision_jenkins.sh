#!/bin/bash
#Created by Sam Gleske (https://github.com/samrocketman/home)
#Wed May 20 11:09:18 EDT 2015
#Mac OS X 10.9.5
#Darwin 13.4.0 x86_64
#GNU bash, version 3.2.53(1)-release (x86_64-apple-darwin13)
#curl 7.30.0 (x86_64-apple-darwin13.0) libcurl/7.30.0 SecureTransport zlib/1.2.5
#awk version 20070501
#java version "1.7.0_55"
#Java(TM) SE Runtime Environment (build 1.7.0_55-b13)
#Java HotSpot(TM) 64-Bit Server VM (build 24.55-b03, mixed mode)

#DESCRIPTION
#  Provisions a fresh Jenkins on a local laptop, updates the plugins, and runs
#  it.
#    1. Creates a JENKINS_HOME.
#    2. Downloads Jenkins.
#    3. Updates the Jenkins plugins to the latest version.

#USAGE
#  Automatically provision and start Jenkins on your laptop.
#    mkdir ~/jenkins_testing
#    cd ~/jenkins_testing
#    provision_jenkins.sh bootstrap
#  Kill and completely delete your provisioned Jenkins.
#    cd ~/jenkins_testing
#    provision_jenkins.sh purge
#  Update all plugins to the latest version using jenkins-cli
#    cd ~/jenkins_testing
#    provision_jenkins.sh update-plugins
#  Start or restart Jenkins.
#    cd ~/jenkins_testing
#    provision_jenkins.sh start
#    provision_jenkins.sh restart
#  Stop Jenkins.
#    provision_jenkins.sh stop

#
# USER CUSTOMIZED VARIABLES
#

#Latest Release
jenkins_url="${jenkins_url:-http://mirrors.jenkins-ci.org/war/latest/jenkins.war}"
#LTS Jenkins URL
#jenkins_url="${jenkins_url:-http://mirrors.jenkins-ci.org/war-stable/latest/jenkins.war}"
JENKINS_HOME="${JENKINS_HOME:-my_jenkins_home}"
JENKINS_WEB="${JENKINS_WEB:-http://localhost:8080/}"
JENKINS_CLI="${JENKINS_CLI:-java -jar ./jenkins-cli.jar -s ${JENKINS_WEB} -noKeyAuth}"
JENKINS_START="${JENKINS_START:-java -jar jenkins.war}"
#remove trailing slash
JENKINS_WEB="${JENKINS_WEB%/}"
CURL="${CURL:-curl}"

#Get JAVA_HOME for java on Mac OS X
#will only run if OS X is detected
if uname -rms | grep Darwin &> /dev/null; then
  JAVA_HOME="$(/usr/libexec/java_home)"
  PATH="${JAVA_HOME}/bin:${PATH}"
  echo "JAVA_HOME=${JAVA_HOME}"
  java -version
fi

export jenkins_url JENKINS_HOME JAVA_HOME PATH JENKINS_CLI CURL

#
# SCRIPT CONSOLE SCRIPTS
#

function script_skip_wizard() {
  cat <<'EOF'
import hudson.util.PluginServletFilter
def j=Jenkins.instance
if('getSetupWizard' in j.metaClass.methods*.name.sort().unique()) {
    def w=j.getSetupWizard()
    if(w != null) {
        try {
          w.completeSetup()
        }
        catch(Exception e) {
          //pre Jenkins 2.6
          w.completeSetup(j)
          PluginServletFilter.removeFilter(w.FORCE_SETUP_WIZARD_FILTER)
        }
        j.save()
        println 'Wizard skipped.'
    }
}
EOF
}

function script_disable_usage_stats() {
  echo 'Jenkins.instance.setNoUsageStatistics(true)'
}

function script_upgrade_plugins() {
  cat <<'EOF'
import hudson.model.UpdateSite

def j = Jenkins.instance

/*
   Install Jenkins plugins
 */
def install(Collection c, Boolean dynamicLoad, UpdateSite updateSite) {
    c.each {
        println "Installing ${it} plugin."
        UpdateSite.Plugin plugin = updateSite.getPlugin(it)
        Throwable error = plugin.deploy(dynamicLoad).get().getError()
        if(error != null) {
            println "ERROR installing ${it}, ${error}"
        }
    }
    null
}

//upgrade plugins
UpdateSite s = (UpdateSite) j.getUpdateCenter().getSite(UpdateCenter.ID_DEFAULT)

//download latest JSON update data
s.updateDirectlyNow(true)

install(s.getUpdates()*.getInstalled()*.getShortName(), false, s)
EOF
}

function script_install_plugins() {
  cat <<EOF
def plugins = "$@".split('[, ]') as ArrayList

/*
   Install Jenkins plugins
 */
def install(Collection c, Boolean dynamicLoad, UpdateSite updateSite) {
    c.each {
        println "Installing \${it} plugin."
        UpdateSite.Plugin plugin = updateSite.getPlugin(it)
        Throwable error = plugin.deploy(dynamicLoad).get().getError()
        if(error != null) {
            println "ERROR installing \${it}, \${error}"
        }
    }
    null
}

def j = Jenkins.instance

//upgrade plugins
UpdateSite s = (UpdateSite) j.getUpdateCenter().getSite(UpdateCenter.ID_DEFAULT)
//only install plugins if they're missing
install(plugins - j.pluginManager.getPlugins()*.getShortName(), true, s)
EOF
}

#
# FUNCTIONS
#

function jenkins_script_console() {
  echo "Calling jenkins_script_console $1"
  ${CURL} --data-urlencode "script=$(eval "$@")" ${JENKINS_WEB}/scriptText
}

#CSRF protection support
function is_crumbs_enabled() {
  use_crumbs="$( $CURL -s ${JENKINS_WEB}/api/json?pretty=true 2> /dev/null | python -c 'import sys,json;exec "try:\n  j=json.load(sys.stdin)\n  print str(j[\"useCrumbs\"]).lower()\nexcept:\n  pass"' )"
  if [ "${use_crumbs}" = "true" ]; then
    return 0
  fi
  return 1
}

#CSRF protection support
function get_crumb() {
  ${CURL} -s ${JENKINS_WEB}/crumbIssuer/api/json | python -c 'import sys,json;j=json.load(sys.stdin);print j["crumbRequestField"] + "=" + j["crumb"]'
}

#CSRF protection support
function csrf_set_curl() {
  if is_crumbs_enabled; then
    if [ ! "${CSRF_CRUMB}" = "$(get_crumb)" ]; then
      if [ -n "${CSRF_CRUMB}" ]; then
        #remove existing crumb value from curl command
        CURL="$(echo "${CURL}" | sed "s/ -d ${CSRF_CRUMB}//")"
      fi
      export CSRF_CRUMB="$(get_crumb)"
      export CURL="${CURL} -d ${CSRF_CRUMB}"
      echo "Using crumbs for CSRF support."
    elif ! echo "${CURL}" | grep -F "${CSRF_CRUMB}" &> /dev/null; then
      export CURL="${CURL} -d ${CSRF_CRUMB}"
      echo "Using crumbs for CSRF support."
    fi
  fi
}

function is_auth_enabled() {
  no_authentication="$( $CURL -s ${JENKINS_WEB}/api/json?pretty=true 2> /dev/null | python -c 'import sys,json;exec "try:\n  j=json.load(sys.stdin)\n  print str(j[\"useSecurity\"]).lower()\nexcept:\n  pass"' )"
  #check if authentication is required.;
  #if the value of no_authentication is anything but false; then assume authentication
  if [ ! "${no_authentication}" = "false" ]; then
    echo -n "Authentication required..."
    if [ -e "${JENKINS_HOME}/secrets/initialAdminPassword" ]; then
      echo "DONE"
      return 0
    else
      echo "FAILED"
      echo "Could not set authentication."
      echo "Missing file: ${JENKINS_HOME}/secrets/initialAdminPassword"
      exit 1
    fi
  fi
  return 1
}

function url_ready() {
  url="$1"
  echo -n "Waiting for ${url} to become available."
  while [ ! "200" = "$(curl -sLiI -w "%{http_code}\\n" -o /dev/null ${url})" ]; do
    echo -n '.'
    sleep 1
  done
  echo 'ready.'
}

function download_file() {
  #see bash man page and search for Parameter Expansion
  if [ "$#" = 1 ]; then
    url="$1"
    file="${1##*/}"
  else
    url="$1"
    file="$2"
  fi
  url_ready "${url}"
  if [ ! -e "${file}" ]; then
    curl -SLo "${file}" "${url}"
  fi
}

function jenkins_status() {
    #check to see if jenkins is running
    #will return exit status 0 if running or 1 if not running
    STATUS=1
    if [ -e "jenkins.pid" ]; then
      ps aux | grep -v 'grep' | grep 'jenkins\.war' | grep "$(cat jenkins.pid)" &> /dev/null
      STATUS=$?
    fi
    return ${STATUS}
}

function start_or_restart_jenkins() {
  #start Jenkins, if it's already running then stop it and start it again
  if [ -e "jenkins.pid" ]; then
    echo -n 'Jenkins might be running so attempting to stop it.'
    kill $(cat jenkins.pid)
    #wait for jenkins to stop
    while jenkins_status; do
      echo -n '.'
      sleep 1
    done
    rm jenkins.pid
    echo 'stopped.'
  fi
  echo 'Starting Jenkins.'
  ${JENKINS_START} >> console.log 2>&1 &
  echo "$!" > jenkins.pid
}

function stop_jenkins() {
  if [ -e "jenkins.pid" ]; then
    echo -n 'Jenkins might be running so attempting to stop it.'
    kill $(cat jenkins.pid)
    #wait for jenkins to stop
    while ps aux | grep -v 'grep' | grep "$(cat jenkins.pid)" &> /dev/null; do
      echo -n '.'
      sleep 1
    done
    rm jenkins.pid
    echo 'stopped.'
  fi
}

function update_jenkins_plugins() {
  #download the jenkins-cli.jar client
  download_file "${JENKINS_WEB}/jnlpJars/jenkins-cli.jar"
  echo 'Updating Jenkins Plugins using jenkins-cli.'
  UPDATE_LIST="$( ${JENKINS_CLI} list-plugins | awk '$0 ~ /\)$/ { print $1 }' )"
  if [ ! -z "${UPDATE_LIST}" ]; then
    ${JENKINS_CLI} install-plugin ${UPDATE_LIST}
  fi
}

function jenkins_cli() {
  #download the jenkins-cli.jar client
  download_file "${JENKINS_WEB}/jnlpJars/jenkins-cli.jar"
  echo "Executing: ${JENKINS_CLI} $@"
  ${JENKINS_CLI} $@
}

function force-stop() {
  if [ -e 'jenkins.pid' ]; then
    kill -9 $(cat jenkins.pid) 2> /dev/null
    rm -f jenkins.pid
  fi
}

#
# main execution
#

case "$1" in
  bootstrap)
    shift
    skip_restart='false'
    while [ "$#" -gt '0' ]; do
      case $1 in
        --skip-restart)
          skip_restart='true'
          shift
          ;;
        *)
          echo "Error invalid arument provided to bootstrap command: $1"
          exit 1
          ;;
      esac
    done

    #provision Jenkins by default
    #download jenkins.war
    download_file ${jenkins_url} jenkins.war

    echo "JENKINS_HOME=${JENKINS_HOME}"

    start_or_restart_jenkins

    url_ready "${JENKINS_WEB}/jnlpJars/jenkins-cli.jar"

    #try enabling authentication
    if is_auth_enabled; then
      export CURL="${CURL} -u admin:$(<${JENKINS_HOME}/secrets/initialAdminPassword)"
    fi

    #try enabling CSRF protection support
    csrf_set_curl

    jenkins_script_console script_skip_wizard
    jenkins_script_console script_disable_usage_stats
    jenkins_script_console script_upgrade_plugins
    jenkins_script_console script_install_plugins "credentials-binding,git,github,github-oauth,job-dsl,matrix-auth,matrix-project,pipeline-stage-view,ssh-slaves,workflow-aggregator"

    if ! ${skip_restart}; then
      start_or_restart_jenkins
      url_ready "${JENKINS_WEB}/jnlpJars/jenkins-cli.jar"
    fi

    echo "Jenkins is ready.  Visit ${JENKINS_WEB}/"
    if is_auth_enabled &> /dev/null; then
      echo "User: admin"
      echo "Password: $(<${JENKINS_HOME}/secrets/initialAdminPassword)"
    fi
    ;;
  download-file)
    shift
    download_file "$1"
    ;;
  clean)
    force-stop
    rm -f console.log jenkins.pid
    rm -rf "${JENKINS_HOME}"
    ;;
  cli)
    shift
    jenkins_cli $@
    ;;
  install-plugins)
    shift
    #try enabling authentication
    if is_auth_enabled; then
      export CURL="${CURL} -u admin:$(<${JENKINS_HOME}/secrets/initialAdminPassword)"
    fi
    #try enabling CSRF protection support
    csrf_set_curl
    jenkins_script_console script_install_plugins "$@"
    ;;
  update-plugins)
    update_jenkins_plugins
    echo 'Jenkins may need to be restarted.'
    ;;
  purge)
    force-stop
    rm -f console.log jenkins-cli.jar jenkins.pid jenkins.war
    rm -rf "${JENKINS_HOME}"
    ;;
  start|restart)
    start_or_restart_jenkins
    ;;
  status)
    if jenkins_status; then
      echo 'Jenkins is running.'
      exit 0
    else
      echo 'Jenkins is not running.'
      exit 1
    fi
    ;;
  stop)
    stop_jenkins
    ;;
  url-ready)
    shift
    url_ready "$1"
    ;;
  *)
    cat <<- "EOF"
SYNOPSIS
  provision_jenkins.sh [command] [additional arguments]

DESCRIPTION
  Additional arguments are only available for commands that support it.
  Otherwise, additional arguments will be ignored.

  Provisions a fresh Jenkins on a local laptop, updates the plugins, and runs
  it.  Creates a JAVA_HOME.  Downloads Jenkins.  Updates the Jenkins plugins to
  the latest version.

COMMANDS
  bootstrap                  The bootstrap behavior is to provision Jenkins.
                             This command creates a JAVA_HOME, downloads
                             Jenkins, and updates the plugins to the latest
                             version.  Additionally, it will install the git,
                             github, and github-oauth plugins.

  cli [args]                 This command takes additional arguments.  All
                             arguments are passed through to jenkins-cli.jar.

  clean                      WARNING: destructive command.  Kills the current
                             instance of Jenkins, deletes JENKINS_HOME, removes
                             the jenkins.pid file, and removes the console.log.
                             Use this when you want start from scratch but don't
                             want to download the latest Jenkins.

  download-file URL          Wait for a file to become available and then
                             download it.  This command is useful for
                             automation.

  install-plugins [args]     This command takes additional arguments.  The
                             additional arguments are one or more Jenkins plugin
                             IDs.

  purge                      WARNING: destructive command.  Deletes all files
                             related to the provisioned Jenkins including the
                             war file and JENKINS_HOME.  If Jenkins is running
                             it will be sent SIGKILL.

  start or                   start and restart do the same thing.  If Jenkins is
  restart                    not running then it will start it.  If Jenkins is
                             already running then it will stop Jenkins and start
                             it again.

  stop                       Will gracefully shutdown Jenkins and leave the
                             JENKINS_HOME intact.

  update-plugins             Updates all unpinned plugins in Jenkins to their
                             latest versions.

  url-ready URL              Wait for a URL to become available.  This command
                             is useful for automation.

EXAMPLE USAGE
  Automatically provision and start Jenkins on your laptop.
    mkdir ~/jenkins_testing
    cd ~/jenkins_testing
    provision_jenkins.sh bootstrap

  Kill and completely delete your provisioned Jenkins.
    cd ~/jenkins_testing
    provision_jenkins.sh purge

  Update all plugins to the latest version using jenkins-cli
    cd ~/jenkins_testing
    provision_jenkins.sh update-plugins

  Install additional plugins e.g. the slack plugin.
    cd ~/jenkins_testing
    provision_jenkins.sh install-plugins slack

  Start or restart Jenkins.
    cd ~/jenkins_testing
    provision_jenkins.sh start
    provision_jenkins.sh restart

  Stop Jenkins.
    provision_jenkins.sh stop

  See Jenkins CLI help documentation.
    provision_jenkins.sh cli help

  Create a Job using Jenkins CLI.
    provision_jenkins.sh cli create-job my_job < config.xml

EOF
esac

