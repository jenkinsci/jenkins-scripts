/*** BEGIN META {
  "name" : "Build Jobs Matching Pattern",
  "comment" : "Find all jobs with names matching the given pattern and builds them.",
  "parameters" : [ "jobPattern" ],
  "core": "1.409",
  "authors" : [
    { name : "Kristian Kraljic" }
  ]
} END META**/

import jenkins.model.*
import hudson.model.*

// Pattern to search for. Regular expression.
//def jobPattern = "some-pattern-.*"

def matchedJobs = Jenkins.instance.items.findAll { job ->
    job.name =~ /$jobPattern/
}

matchedJobs.each { job ->
    println "Scheduling matching job ${job.name}"
    job.scheduleBuild(new Cause.UserIdCause())
}