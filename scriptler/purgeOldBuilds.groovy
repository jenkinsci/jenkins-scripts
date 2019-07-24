/*** BEGIN META {
  "name" : "Purge Old Builds",
  "comment" : "Runs the log rotator for each job, purging old builds if needed.",
  "parameters" : [],
  "core": "1.409",
  "authors" : [
    { name : "Andrew Bayer" }, { name : "Sam Gleske" }
  ]
} END META**/

import hudson.model.Job
import jenkins.model.Jenkins

Jenkins.instance.getAllItems(Job.class).findAll { it.hasProperty('logRotator') && it.logRotator != null }.each { job ->
    job.logRotate()
}
