/*** BEGIN META {
  "name" : "Maven Multi-module Jobs Disaster Recovery",
  "comment" : "If for any reason (e.g., an incorrect global configuration) many builds of many jobs fail, you'd have to spend a lot of time cleaning them. This plugin removes, for each job, all the builds to the last successful and resets the number of the next build properly (lastSuccessful+1). It seems to work also for multi-configuration multi-module maven projects. It's possible to conduct a dry run test using a parameter (default true) you could never guess;)",
  "parameters" : [ 'dryRun' ],
  "authors" : [
    { name : "Giacomo Boccardo" }
  ]
} END META**/


import hudson.matrix.MatrixConfiguration
import hudson.matrix.MatrixProject
import hudson.maven.MavenModuleSet
import hudson.model.*
import hudson.model.Fingerprint.RangeSet;


// If you are not using Scriptler plugin, uncomment and change properly the following parameter
// def dryRun = true
    
def dryRunBool = dryRun.toBoolean()
  
  
!dryRunBool ?:  println("\n\n\n!!! DRY RUN !!!\n\n\n") 
  
  
def jobs = Hudson.instance.items


// Disable all jobs to avoid dirty states,
// but remember the jobs already disabled to avoid re-enabling them
def disabledJobs = [:]
println("[Disabling all the enabled jobs]")
jobs.each{job ->
   disabledJobs[job.name] = job.disabled
   println(" * " + job.name + (job.disabled? " already": "") + " disabled") 
   dryRunBool ?: (job.disabled=true)
}

  
println("\n[Cleaning all the jobs]")  
jobs.each{job ->
   // The builds' range to remove  is [lastStableBuild + 1, lastBuild]
   def lSBNumber = job.getLastStableBuild()?.getNumber() ?: 1 
   def lBNumber = job.getLastBuild()?.getNumber() ?: 1 
   def jobName = job.name
     
   if ( job instanceof MavenModuleSet || job instanceof MatrixProject ) {  
      if ( lSBNumber == lBNumber ) {
         println(" * " + jobName + " is already clean")  
      } else { 
         cleanJobRange(job, lSBNumber+1, lBNumber, dryRunBool, 2)
      }
   } else {
      println("Unknown job type " + job)
   } 
   println "\n\n"
     
}

println("\n[Re-enabling only the jobs previously enabled]")
jobs.each{job ->
   dryRunBool ?: (job.disabled=disabledJobs[job.name])
   println(" > " + job.name + (job.disabled ? " still disabled" : " enabled"))  
}

  
def cleanJobRange(job, fromBuildNum, toBuildNum, dryRunBool, tab) {
   def delRange =  fromBuildNum + "-" + toBuildNum
   def delRangeSet = RangeSet.fromString(delRange, true);
  
   def jobName = job.name
   println(" "*(tab-2)  + " * " + jobName + " - Removing builds " + delRangeSet.min() + " -> " + (delRangeSet.max()-1))  
     
   // Remove job's build  
   def builds = job.getBuilds(delRangeSet)
   builds.each { 
      dryRunBool ?: it.delete()
      println(" "*tab + " > " + it + " removed.") 
   }
 
   def newNBNInt = fromBuildNum
   // Reset job's 'nextBuildNumber'
   dryRunBool ?: job.updateNextBuildNumber(newNBNInt) 
   def jPNBN = job.getNextBuildNumber()
   def jNBN = dryRunBool ? newNBNInt : jPNBN
   println(" "*(tab+1) + jobName + "'s nextBuildNumber changed from " + jPNBN + " to " + jNBN + ".")
 
     
   if ( job instanceof MavenModuleSet ) {  
       // Reset job's modules' 'nextBuildNumber'
      job.getModules().each{module -> 
         def prevNBN = module.getNextBuildNumber()
         dryRunBool ?: module.updateNextBuildNumber(newNBNInt)
         def mNBN = dryRunBool ? newNBNInt : module.getNextBuildNumber()
         println(" "*(tab+1) + "  > " + module.name + "'s nextBuildNumber changed from " + prevNBN + " to " + mNBN) 
      }
   } else if ( job instanceof MatrixProject ) {
       def mConfs = job.getActiveConfigurations()  
       mConfs.each{ mConf ->  
          cleanJobRange(mConf, fromBuildNum, toBuildNum, dryRunBool, tab+2)
       }
   } else if ( job instanceof MatrixConfiguration ) {
     // NOP
   } else {
      println("Something very strange happened if you read this message. Job: " + job)
   } 
}  