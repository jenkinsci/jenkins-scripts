/*** BEGIN META {
 "name" : "Disable Broken Jobs",
 "comment" : "Disable jobs that haven't had a successful build for at least 90 days.",
 "core": "1.409",
 "authors" : [
 { name : "Gareth Bowles" }
 ]
 } END META**/


import hudson.model.*
import java.util.Date

hudson = Hudson.instance

int count = 0
Date now = new Date()
Date ninetyDaysAgo = new Date(((long)now.time-(1000L*60*60*24*90)))
println "\nNow: ${now}"
println "Ninety days ago: ${ninetyDaysAgo}\n"

for (job in hudson.projects) {
    if (!job.isDisabled()) {
        if (job.lastSuccessfulBuild?.time?.before(ninetyDaysAgo)) {
            if (job.firstBuild?.time?.after(ninetyDaysAgo)) {
                println "No successful builds for ${job.name}, but we won't disable it yet as it's less than 90 days old; first build was at ${job.firstBuild?.time}"
            } else {
                println "Disabling ${job.name} at ${now}. lastSuccessfulBuild ${job.lastSuccessfulBuild?.time}"
                def description = "Disabled by TL-JEN-jobs_disable-old_broken on [[${now}]]"
                if (job.description) {
                    job.setDescription("${job.description} - ${description}")
                } else {
                    job.setDescription(" - ${description}")
                }
                job.doDisable()
                count++
            }
        }
    }
}
println "\nDisabled ${count} jobs.\n"