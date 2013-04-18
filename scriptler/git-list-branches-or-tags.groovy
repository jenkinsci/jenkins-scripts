/*** BEGIN META {
  "name" : "Git - List Branches or Tags",
  "comment" : "With https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+Dynamic+Parameter+Plug-in this scriptlet will use ls-remote to list your Git branches. The script exposes a 'scope' parameter that can be switched between '--heads' or '--tags' to toggle the list content.",
  "parameters" : [ "scope", "context" ],
  "core": "1.450",
  "authors" : [
    { "name" : "Jason Stiefel", "email" : "jason@stiefel.io" }
  ]
} END META**/

import hudson.model.*
import jenkins.model.*

def param(name, defaultValue) {
    try {
        this."${name}"
    } catch (Throwable t) {
        defaultValue
    }
}

def gitScm = 'hudson.plugins.git.GitSCM'
def gitTimeout = 30000
def gitScope = param('scope','--heads')             // Change to '--tags' or specify param value to list tags

def project
try {

    def jenkinsContext = new URI(Jenkins.instance.rootUrlFromRequest).path.replaceAll(/\//,'\\/')
    def projectUrl = (Thread.currentThread().name =~ "${jenkinsContext}([\\S]*\\/)[a-z]++[\\/]?")[0][1]
    project = Jenkins.instance.getAllItems(AbstractProject).find { it.url == projectUrl }
    if (!project)
        throw new ArrayIndexOutOfBoundsException()
    if (project.scm.class.name != gitScm)
        return ["Invalid SCM - Not using ${gitScm}"]

} catch (Throwable t) {
    return ["Could not parse project URL from thread name: ${Thread.currentThread().name}"]
}

try {

    def url = project.scm.userRemoteConfigs.first().url
    def proc = ['git', 'ls-remote', gitScope, url].execute(); proc.waitForOrKill(gitTimeout);
    if (proc.exitValue())
        throw new IllegalStateException(proc.errorStream.text)
    proc.in.text.tokenize('\n').collectAll { (it =~ /refs\/[^\/]+\/(.*)/)[0][1] }.sort()

} catch (Throwable t) {
    return ["Could not list branches for ${project.name}: ${t}"]
}
