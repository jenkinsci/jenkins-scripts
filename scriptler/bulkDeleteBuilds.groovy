/*** BEGIN META {
  "name" : "Bulk Delete Builds",
  "comment" : "For a given job and a given range of possible build numbers, delete those builds.",
  "parameters" : [ 'jobName', 'buildRange' ],
  "core": "1.409",
  "authors" : [
    { name : "Andrew Bayer" }
  ]
} END META**/


// NOTE: uncomment parameters below if not using Scriptler >= 2.0, or if you're just pasting
// the script in manually.

// The name of the job.
//def jobName = "some-job"

// The range of build numbers to delete.
//def buildRange = "1-5"

import jenkins.model.*;
import hudson.model.Fingerprint.RangeSet;
def j = jenkins.model.Jenkins.instance.getItem(jobName);

def r = RangeSet.fromString(buildRange, true);

j.getBuilds(r).each { it.delete() }


