/*** BEGIN META {
 "name" : "Configure Maven Auto Installer",
 "comment" : "Configure maven auto-installer in Jenkins global configuration",
 "parameters" : [],
 "core": "1.625",
 "authors" : [
 { name : "Amit Modak" }
 ]
 } END META**/

import hudson.tasks.Maven.MavenInstallation;
import hudson.tools.InstallSourceProperty;
import hudson.tools.ToolProperty;
import hudson.tools.ToolPropertyDescriptor;
import hudson.util.DescribableList;

def mavenDesc = jenkins.model.Jenkins.instance.getExtensionList(hudson.tasks.Maven.DescriptorImpl.class)[0]

def isp = new InstallSourceProperty()
def autoInstaller = new hudson.tasks.Maven.MavenInstaller("3.3.3")
isp.installers.add(autoInstaller)

def proplist = new DescribableList<ToolProperty<?>, ToolPropertyDescriptor>()
proplist.add(isp)

def installation = new MavenInstallation("M3", "", proplist)

mavenDesc.setInstallations(installation)
mavenDesc.save()
