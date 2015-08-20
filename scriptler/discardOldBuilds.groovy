/*** BEGIN META {
  "name" : "Discard old builds",
  "comment" : "Changes the config of the builds to discard old builds (only if no log rotation is configured).",
  "parameters" : [ 'dryRun', 'daysToKeep', 'numToKeep', 'artifactDaysToKeep', 'artifactNumToKeep'],
  "core": "1.350",
  "authors" : [
    { name : "Mestachs" }, { name : "Dominik Bartholdi" }
  ]
} END META**/

// NOTES:
// dryRun: to only list the jobs which would be changed
// daysToKeep:  If not -1, history is only kept up to this days.
// numToKeep: If not -1, only this number of build logs are kept.
// artifactDaysToKeep: If not -1 nor null, artifacts are only kept up to this days.
// artifactNumToKeep: If not -1 nor null, only this number of builds have their artifacts kept.

noLogRotation = hudson.model.Hudson.instance.items.findAll
{job -> job.isBuildable() && job.logRotator==null}
noLogRotation.each() { job ->
    println job.name
    if(!"true".equals(dryRun)){
        job.logRotator = new hudson.tasks.LogRotator ( daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep)
        println "$job.name fixed "
    }
}
