/*** BEGIN META {
 "name" : "Discard old builds",
 "comment" : "Changes the config of the builds to discard old builds (only if no log rotation is configured).",
 "parameters" : [ 'dryRun', 'daysToKeep', 'numToKeep', 'artifactDaysToKeep', 'artifactNumToKeep'],
 "core": "2.46.2",
 "authors" : [
 { name : "Mestachs" }, { name : "Dominik Bartholdi" }, { name: "Denys Digtiar" }
 ]
 } END META**/

// NOTES:
// dryRun: to only list the jobs which would be changed
// daysToKeep:  If not -1, history is only kept up to this days.
// numToKeep: If not -1, only this number of build logs are kept.
// artifactDaysToKeep: If not -1 nor null, artifacts are only kept up to this days.
// artifactNumToKeep: If not -1 nor null, only this number of builds have their artifacts kept.

import jenkins.model.Jenkins
import hudson.model.Job
import jenkins.model.BuildDiscarderProperty
import hudson.tasks.LogRotator

Jenkins.instance.allItems(Job).each { job ->
    if (job.isBuildable() && job.supportsLogRotator() && job.getProperty(BuildDiscarderProperty) == null) {
        println "Processing \"${job.fullDisplayName}\""
        if (!"true".equals(dryRun)) {
            // adding a property implicitly saves so no explicit one
            job.addProperty(new BuildDiscarderProperty(new LogRotator ( daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep)))
            println "${job.displayName} is updated"
        }
    }
}
return;
