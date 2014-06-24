/*** BEGIN META {
  "name" : "svnChangeBranch",
  "comment" : "Batch switch svn jobs to a different branch. JobFilter and URL filter are regular expression to select Job/SVN urls to replace. newBranch is the new value to set, oldBranch is the expression to replace (defaults to \'/branches/[^/]* /\' which replaces the url part after branches. No actual changes will be made unless applyChanges is set to \'true\'",
  "parameters" : [ 'jobFilter', 'urlFilter', 'newBranch', 'oldBranch', 'applyChanges' ],
  "core": "1.499",
  "authors" : [
    { name : "thosor" }, { name : "AVee" }
  ]
} END META**/

/*
 * Based on http://scriptlerweb.appspot.com/script/show/48001
 * 
 * Currently largely untested, although it seems to work, check your results!
 */

import hudson.scm.*

// Default to replacing the part after .../branches/ with the value of newBranch.
if(null == oldBranch || "".equals(oldBranch)) {
  oldBranch = "/branches/([^/])*/"
  newBranch = "/branches/$newBranch/"
}

/** Display Configuration */
println "### Overwrite SVN-Version ###"
println "oldVersion:     " + oldBranch
println "New branch:     " + newBranch
println "Job Filter:     " + jobFilter
println "Url Filter:     " + urlFilter
println "Apply Changes:  " + applyChanges


// Access to the Hudson Singleton
hudsonInstance = hudson.model.Hudson.instance

// Retrieve matching jobs
allItems = hudsonInstance.items
chosenJobs = allItems.findAll{job -> job.name =~ /$jobFilter/}

// Do work and create the result table
chosenJobs.each { job ->
  if(!(job instanceof hudson.model.ExternalJob)) {
    // No SCM-Configuration possible for External Jobs!
    if (job.scm instanceof SubversionSCM) { 
      println ""
      println job.name
      
      // Job has a SubversionSCM-Configuration
      def newSvnPath = [][]

      job.scm.locations.each{
        //For every Subversion-Location
        println "-   $it.remote"

        if (it.remote =~ /$urlFilter/) {
          //SVN-Path contains the given Path

          newRemote = it

          match =  it.remote =~ /$oldBranch/
          
          if(match) {
            newRemote = match.replaceFirst(newBranch)
            println " -> $newRemote"
            newSvnPath.add(it.withRemote(newRemote))
          } else {
            println "    Doesn't match oldBranch"
            newSvnPath.add(it)
          }
        } else {
          println "    Doesn't match urlFilter"
        }
      }
      
      // Every Location was checked. Building new SVN-Configuration with the new SVN-Locations
      newscm = new hudson.scm.SubversionSCM(newSvnPath, job.scm.workspaceUpdater, job.scm.browser, 
                                   job.scm.excludedRegions, job.scm.excludedUsers, job.scm.excludedRevprop, 
                                   job.scm.excludedCommitMessages, job.scm.includedRegions,
                                   job.scm.ignoreDirPropChanges, job.scm.filterChangelog, job.scm.additionalCredentials)
      if(newscm.getLocations().size() == newSvnPath.size())
      {
        if ("true".equals(applyChanges)){
          // Only write values, when applyChanges is true
          println "Saving $job.name"
          job.scm = newscm
        }
      } else {
        println "ERROR: SVN SubversionSCM didn't pick up the new path."
      }
      //Job is done
    }
  } 
}
//done
