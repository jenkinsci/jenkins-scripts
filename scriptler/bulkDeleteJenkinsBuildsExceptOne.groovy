/*** BEGIN META {
  "name" : "Bulk Delete Builds except the given build number",
  "comment" : "For a given job and a given build numnber, delete all build except the user provided one.",
  "parameters" : [ 'jobName', 'buildNumber' ],
  "core": "1.410",
  "authors" : [
     { name : "Arun Sangal" }
  ]
} END META **/


// NOTE: Uncomment parameters below if not using Scriptler >= 2.0, or if you're just pasting the script in manually.
// ----- Logic in this script takes 5000 as the infinite number, decrease / increase this value from your own experience.
// The name of the job.
//def jobName = "some-job"

// The range of build numbers to delete.
//def buildNumber = "5"

def lastBuildNumber = buildNumber.toInteger() - 1;
def nextBuildNumber = buildNumber.toInteger() + 1;


import jenkins.model.*;
import hudson.model.Fingerprint.RangeSet;

def jij = jenkins.model.Jenkins.instance.getItem(jobName);

println("Keeping Job_Name: ${jobName} and build Number: ${buildNumber}");
println ""

def setBuildRange = "1-${lastBuildNumber}"

//println setBuildRange

def range = RangeSet.fromString(setBuildRange, true);

jij.getBuilds(range).each { it.delete() }

println("Builds have been deleted - Range: " + setBuildRange)


setBuildRange = "${nextBuildNumber}-5000"

//println setBuildRange

range = RangeSet.fromString(setBuildRange, true);

jij.getBuilds(range).each { it.delete() }

println("Builds have been deleted - Range: " + setBuildRange)

