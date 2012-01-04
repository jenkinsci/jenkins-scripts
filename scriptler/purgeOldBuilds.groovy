/*** BEGIN META {
  "name" : "Purge Old Builds",
  "comment" : "Runs the log rotator for each job, purging old builds if needed.",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Andrew Bayer" }
  ]
} END META**/


jenkins.model.Jenkins.items.each { it.logRotate() }
