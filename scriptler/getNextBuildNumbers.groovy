/*** BEGIN META {
 "name" : "Print next build numbers for all jobs",
 "comment" : "This script ouputs a JSON-formatted listing of the next build numbers for all jobs recursively. If you are interested in only a subset of the jobs, please specify the root folder explicitly. The output JSON can be captured in a file, copied over to another Jenkins server and used with the setNextBuildNumbers.groovy script. The idea is to ensure that build numbers do not get reset to 1 when migrating jobs from one Jenkins server to another.",
 "parameters" : ['rootItem'],
 "core": "1.625",
 "authors" : [
 { name : "Amit Modak" }
 ]
 } END META**/

import jenkins.model.*

def getBuildNumber(def item, def node) {

  if(item instanceof com.cloudbees.hudson.plugins.folder.Folder) {
    node[item.getName()] = [:] 
    item.getItems().each {
      getBuildNumber(it, node[item.getName()])
    }   
  } else {
    node[item.getName()] = item.nextBuildNumber
  }
}

//
// main
//

def root = [:] 
def node = root

if(rootItem) {

  if(Jenkins.instance.getItemByFullName(rootItem) && (Jenkins.instance.getItemByFullName(rootItem) instanceof com.cloudbees.hudson.plugins.folder.Folder)) {
  
    rootItem.split('/').each {
      node[it] = [:] 
      node = node[it]
    }   
  
    Jenkins.instance.getItemByFullName(rootItem).getItems().each {
      getBuildNumber(it, node)
    }   
  
  } else {
    println "Error: '" + rootItem + "' does not exist or is not a folder"
    return
  }

} else {

  Jenkins.instance.getItems().each {
    getBuildNumber(it, node)
  }
}

def output = groovy.json.JsonOutput.toJson(root)
println groovy.json.JsonOutput.prettyPrint(output)
