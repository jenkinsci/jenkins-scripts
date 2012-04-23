/*** BEGIN META {
  "name" : "Maven Multi-Module Project Batch Builds Remover",
  "comment" : "Removes all the builds (modules included) of a given Maven Multi-module Project job ('jobName') and resets the number of the next build to 'newNextBuildNumber' (default 1). It's possible to conduct a dry run test using a parameter (default true) you could never guess ;)",
  "parameters" : [ 'jobName', 'newNextBuildNumber', 'dryRun' ],
  "authors" : [
    { name : "Giacomo Boccardo" }
  ]
} END META**/

import hudson.model.*

// If you are not using Scriptler plugin, uncomment and change properly the following parameters.
// def jobName = "XYZ"
// def newNextBuildNumber = 1
// def dryRun = true
  

def dryRunBool = dryRun.toBoolean()
def newNBNInt = newNextBuildNumber.toInteger()

!dryRunBool ?:  println("!!! DRY RUN !!!") 

// Remove job's build
def job = Hudson.instance.getItem(jobName)
def builds = job.getBuilds()
builds.each { 
   dryRunBool ?: it.delete()
   println("Build removed: [" + it + "]") 
}
 
// Reset job's 'nextBuildNumber'
dryRunBool ?: job.updateNextBuildNumber(newNBNInt) 
def jNBN = dryRunBool ? newNBNInt : job.getNextBuildNumber()
println(job.name + ". NextBuildNumber set to [" + jNBN + "]")
 
// Reset job's modules' 'nextBuildNumber'
job.getModules().each{module -> 
   def prevNBN = module.getNextBuildNumber()
   dryRunBool ?: module.updateNextBuildNumber(newNBNInt)
   def mNBN = dryRunBool ? newNBNInt : module.getNextBuildNumber()
   println("Module: " + module.name + ". NextBuildNumber changed from " + prevNBN + " to " + mNBN) 
}
