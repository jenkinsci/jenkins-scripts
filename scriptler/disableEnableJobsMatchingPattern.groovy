/*** BEGIN META {
  "name" : "Disable/Enable Jobs Matching Pattern",
  "comment" : "Find all jobs with names matching the given pattern and either disables or enables them, depending on the flag.",
  "parameters" : [ 'jobPattern', 'disableOrEnable' ],
  "core": "1.409",
  "authors" : [
    { name : "Andrew Bayer" }
  ]
} END META**/

import jenkins.model.*

// Pattern to search for. Regular expression.
//def jobPattern = "some-pattern-.*"

// Should we be disabling or enabling jobs? "disable" or "enable", case-insensitive.
//def disableOrEnable = "enable"

def lcFlag = disableOrEnable.toLowerCase()

if (lcFlag.equals("disable") || lcFlag.equals("enable")) { 
    def matchedJobs = Jenkins.instance.items.findAll { job ->
        job.name =~ /$jobPattern/
    }
    
    matchedJobs.each { job ->
        if (lcFlag.equals("disable")) { 
            println "Disabling matching job ${job.name}"
            job.disable()
        } else if (lcFlag.equals("enable")) {
            println "Enabling matching job ${job.name}"
            job.enable()
        }
    }
} else {
    println "disableOrEnable parameter ${disableOrEnable} is not a valid option."
}