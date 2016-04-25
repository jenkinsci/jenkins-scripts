/*** BEGIN META {
 "name" : "Disable All Jobs In Folder",
 "comment" : "Disable all the buildable projects inside a Folder (full name must be provided)",
 "parameters" : [ 'folderFullName' ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import com.cloudbees.hudson.plugins.folder.AbstractFolder
import hudson.model.AbstractProject
import jenkins.model.Jenkins

// I want to disable jobs
def disableJobs = { println "Disabling '${it.fullName}'"; it.disable() }

//Function to retrieve all buildable Project in a specific Folder
def doAllItemsInFolder(folderName, closure) {
    AbstractFolder folder = Jenkins.instance.getAllItems(AbstractFolder.class)
            .find {folder -> folderName == folder.fullName };

    folder.getAllJobs()
            .findAll {job -> job instanceof AbstractProject}
            .findAll {job -> job.isBuildable()}
            .each {closure(it)};
}

doAllItemsInFolder("${folderFullName}", disableJobs);