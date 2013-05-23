/*** BEGIN META {
  "name" : "Run Jobs Matching Pattern",
  "comment" : "Find all jobs with names matching the given pattern and run them.",
  "parameters" : [ 'jobPattern'],
  "core": "1.509",
  "authors" : [
    { name : "Vincent Dupain" }
  ]
} END META**/

import jenkins.model.*


def matchedJobs = Jenkins.instance.items.findAll { job ->
  job.name =~ /$jobPattern/
}

matchedJobs.each { job ->
  if (!job.isDisabled() && job.isBuildable()) { 
    println "matching job ${job.name} is enabled, new build..."
    Jenkins.instance.queue.schedule(job) 
  } else println "matching job ${job.name} is disable"
}

