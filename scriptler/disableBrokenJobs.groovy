/*** BEGIN META {
 "name" : "Disable Broken Jobs",
 "comment" : "Disable jobs that haven't had a successful build for at least X days (default 90).",
 "parameters" : [ 'dryRun', 'jobName', 'numberOfDays' ],
 "core": "1.409",
 "authors" : [
 { name : "Gareth Bowles"},{ name: "Benjamin Francisoud" }
 ]
 } END META**/


import hudson.model.*
import java.util.Date

hudson = Hudson.instance

dryRun = dryRun.toBoolean()
println "Dry mode: $dryRun"
maintenanceJobName = maintenanceJobName ?: "disableBrokenJobs.groovy"
println "maintenanceJobName: $maintenanceJobName"
numberOfDays = numberOfDays.toInteger() ?: 90
println "numberOfDays: $numberOfDays"

int count = 0
Date now = new Date()
Date xDaysAgo = new Date(((long)now.time-(1000L*60*60*24*numberOfDays)))
println "\nNow: ${now}"
println "X days ago: ${xDaysAgo}\n"

for (job in hudson.projects) {
    if (!job.isDisabled()) {
        if (job.lastSuccessfulBuild?.time?.before(xDaysAgo)) {
            if (job.firstBuild?.time?.after(xDaysAgo)) {
              println "No successful builds for ${job.name}, but we won't disable it yet as it's less than ${numberOfDays} days old; first build was at ${job.firstBuild?.time}"
            } else {
                println "Disabling ${job.name} at ${now}. lastSuccessfulBuild ${job.lastSuccessfulBuild?.time}"
              def description = "Disabled by [[${maintenanceJobName}]] on [[${now}]]"
                if (job.description) {
                    job.setDescription("${job.description} - ${description}")
                } else {
                    job.setDescription(" - ${description}")
                }
                if (!dryRun) {
                  job.doDisable()
                }
                count++
            }
        }
    }
}
println "\nDisabled ${count} jobs.\n"
