/*** BEGIN META {
  "name" : "Purge Old Builds",
  "comment" : "Runs the log rotator for each job, purging old builds if needed.",
  "parameters" : [],
  "core": "1.409",
  "authors" : [
    { name : "Andrew Bayer" }
  ]
} END META**/


jenkins.model.Jenkins.instance.items.findAll
{job -> job.isBuildable() && job.hasProperty('logRotator') && job.logRotator!=null}.each { it.logRotate() }
