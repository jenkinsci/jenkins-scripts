/*** BEGIN META {
 "name" : "Print next build numbers for all jobs",
 "comment" : "This script consumes a JSON-formatted input file the lists the next build numbers for a collection of jobs and sets the next build numbers of matching jobs as per the input file. The input JSON can be generated using the getNextBuildNumbers.groovy script. The idea is to ensure that build numbers do not get reset to 1 when migrating jobs from one Jenkins server to another.",
 "parameters" : ['inputFilePath'],
 "core": "1.625",
 "authors" : [
 { name : "Amit Modak" }
 ]
 } END META**/

import jenkins.model.*

def setBuildNumber(def key, def value, def path = []) {

  path.push(key)
  if(value instanceof java.util.Map) {
    value.each { k, v ->
      setBuildNumber(k, v, path)
    }

  } else {
    jobName = path.join('/')
    job = Jenkins.instance.getItemByFullName(jobName)

    if(job && !(job instanceof com.cloudbees.hudson.plugins.folder.Folder)) {
      
      //println "Setting build number for " + jobName + " to " + value
      job.nextBuildNumber = value
      job.saveNextBuildNumber()

    } else {
      println "Warning: Failed to set next build number for '" + jobName + "'"
    }
  }
  path.pop()
}

//
// main
//

if(!inputFilePath) {

  println "Error: Please specify inputFilePath and retry"
  return

}

try {

  String input = new File(inputFilePath).getText('UTF-8')

  def jsonSlurper = new groovy.json.JsonSlurper()
  def root = jsonSlurper.parseText(input)

  root.each { k, v ->
    setBuildNumber(k, v)
  }

} catch (java.io.FileNotFoundException e) {
  println "Error: Could not open " + inputFilePath + " for reading"

} catch (groovy.json.JsonException e) {
  println "Error: Failed to parse " + inputFilePath

}
