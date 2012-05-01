#!/bin/bash

usage() {
    cat <<_eof_

Why this? Because Oracle doesn't want you to download the JDK from Jenkins.
As many times as the Jenkins team updates the FetcherCrawler, it breaks again
the next week.

Keep in mind that this will be downloaded and run for every Java step in every
project, so make it run quick if it's a no-op. Might want to cache it locally,
but I don't mind the small load this makes on my web server.

This script needs a pretty specific file layout to work the way I wrote it. I
intend to convert it to Groovy at some point and make it more automagical.

What might break? Permissions, permissions, permissions. Creating, updating
and deleting directories might not work if your system is all wonky. If you
get permissions problems, just delete the ${jdkName} and ${thisJdkDir}
directories and make sure that ${toolsDir} is writable.

1) Download whichever .bin or .sh files you want for whichever platforms you
need. We are a Linux/Solaris shop, so I get all of those and put them in a
directory.

e.g.
jdk-6u32-linux-i586.bin
jdk-6u32-linux-x64.bin
jdk-6u32-solaris-i586.sh
jdk-6u32-solaris-sparc.sh
jdk-6u32-solaris-sparcv9.sh
jdk-6u32-solaris-x64.sh

2) Now create the .list files for each platform. It's pretty easy. The script
uses jdk-6-\$(uname -s)_\$(uname -m) to find the correct file. Note that current
Solaris sparc comes in sun4u and sun4v flavors on the machine side, so
symlink the sparc one. Don't use binary (-b) mode with md5sum here because we
parse the output of that file later.

e.g.
md5sum jdk-6u32-linux-i586.bin > jdk-6-Linux_i686.list
md5sum jdk-6u32-linux-x64.bin > jdk-6-Linux_x86_64.list
md5sum jdk-6u32-solaris-sparc*.sh > jdk-6-SunOS_sparc.list
ln -s ./jdk-6-SunOS_sparc.list jdk-6-SunOS_sun4u.list
ln -s ./jdk-6-SunOS_sparc.list jdk-6-SunOS_sun4v.list
md5sum jdk-6u32-solaris-*6*.sh > jdk-6-SunOS_i86pc.list

3) Update this script with the version you are using. Should probably make
that updatable externally, but there's a lot of other manual stuff already.
You'll also need to set your webroot and jdkName once.

4) Make all of this available from a web server.

5) Configure Jenkins JDK Installations with this:
Name: ${jdkName}
Install automatically: checked
Add Installer -> Run Command
Label: Either blank or something that includes only your Unix boxes
Command: env -i PATH=/usr/bin:/usr/sfw/bin wget -q $webroot/$(basename $0) -O - | bash
Tool Home: .

_eof_
    exit 1
}


cd /

version=1.6.0_32
webroot=http://your.web.url/tools
jdkName=sun-java6-jdk

unset http_proxy https_proxy ftp_proxy
PATH=/usr/xpg4/bin:/usr/bin:/usr/sfw/bin:$PATH

if ! type wget >/dev/null 2>&1; then
    echo "ERROR: Unable to run wget"
    exit 1
fi

: ${JENKINS_HOME:=~jenkins}
toolsDir=$JENKINS_HOME/tools
jdkDir=$toolsDir/$jdkName
thisJdkDir=$toolsDir/jdk$version

while getopts h Option
do
    case "$Option" in
        h) usage ;;
    esac
done
shift $((OPTIND - 1))



if [ -x $jdkDir/bin/java ]; then
    JAVA_HOME=$jdkDir
    if $JAVA_HOME/bin/java -version 2>&1 | grep -q "$version"; then
        exit 0
    fi
fi

rm -rf /tmp/jdkInstaller.$$ $thisJdkDir
mkdir -p /tmp/jdkInstaller.$$ $thisJdkDir

pushd /tmp/jdkInstaller.$$

# Download and check md5sum
md5Done=0
jdkList=jdk-6-$(uname -s)_$(uname -m).list
wget --no-verbose $webroot/$jdkList -O $jdkList
while read md5sum file
do
    wget --no-verbose $webroot/$file -O $file

    if type md5sum >/dev/null 2>&1; then
        if [[ $md5Done -eq 0 ]]; then
            if ! md5sum --check --status $jdkList; then
                echo "ERROR: md5sum --check $jdkList doesn't match"
                exit 1
            else
                md5Done=1
            fi
        fi

    elif type digest >/dev/null 2>&1; then
        digest=$(digest -a md5 $file 2>/dev/null)
        if [[ ${#digest} -eq 32 ]]; then
            if ! grep -q "$digest" $jdkList; then
                echo "ERROR: digest -a md5 of $file doesn't match"
                exit 1
            fi
        fi

    else
        echo "ERROR: No md5sum or digest -a md5 found."
        exit 1

    fi

    chmod 0755 $file

    pushd $toolsDir
    /tmp/jdkInstaller.$$/$file -noregister
    popd

done < $jdkList

popd

rm -rf /tmp/jdkInstaller.$$

rm -rf $jdkDir
ln -s ./$(basename $thisJdkDir) $jdkDir

exit 0
