/*** BEGIN META {
  "name" : "Find running pipeline jobs.",
  "comment" : "Find all pipeline jobs running for at least a given amount of time (delay).",
  "parameters" : [],
  "core": "1.612.1",
  "authors" : [
    { name : "Arnaud Héritier" }
  ]
} END META**/

import groovy.time.*

def abortJobs = false

use(TimeCategory)  {
  
  def delay = 1.day
  
  println "List of running jobs : "
  Jenkins.instance.getAllItems(org.jenkinsci.plugins.workflow.job.WorkflowJob).each{
    job -> job.builds.findAll{it.isBuilding() && new Date(it.startTimeInMillis) < ( new Date() - delay ) }.each{
      build -> 
      TimeDuration duration = TimeCategory.minus(new Date(), new Date(build.startTimeInMillis))
      println "* $job.fullName#$build.number started since $duration"
      if ( abortJobs ) {
        build.finish(hudson.model.Result.ABORTED, new java.io.IOException("Aborting build"))
        println "* $job.fullName#$build.number aborted"        
      }      
    }
  }
}
return;
