/*** BEGIN META {
  "name" : "Bulk Delete Jobs",
  "comment" : "Delete jobs disabled and where last build is older than specified param",
  "parameters" : [ 'dryRun', 'numberOfDays', 'excludeRegexp' ],
  "core": "2.0",
  "authors" : [
    { name : "Benjamin Francisoud" }
  ]
} END META**/



import jenkins.model.*
import java.util.regex.Pattern
import java.util.Date

jenkins = Jenkins.instance

dryRun = dryRun.toBoolean()
println "Dry mode: $dryRun"
numberOfDays = numberOfDays.toInteger() ?: 365
println "numberOfDays: $numberOfDays"
excludeRegexp = excludeRegexp ?: '(Template).*'
println "excludeRegexp: ${excludeRegexp}"
pattern = Pattern.compile(excludeRegexp)

int count = 0
Date now = new Date()
Date xDaysAgo = new Date(((long)now.time-(1000L*60*60*24*numberOfDays)))
println "\nNow: ${now}"
println "X days ago: ${xDaysAgo}\n"

jobs = jenkins.items.findAll{job -> (job instanceof hudson.model.AbstractProject && job.disabled == true && (job.lastSuccessfulBuild?.time?.before(xDaysAgo) || job.lastSuccessfulBuild == null) && !pattern.matcher(job.name).matches()) }

jobs.each { job ->
    if (job.firstBuild?.time?.after(xDaysAgo)) {
        println "No successful builds for ${job.name}, but we won't disable it yet as it's less than ${numberOfDays} days old; first build was at ${job.firstBuild?.time}"
    } else {
        println "Deleting ${job.name} at ${now}. lastSuccessfulBuild ${job.lastSuccessfulBuild?.time}"
        if (!dryRun) {
            job.delete()
        }
        count++
    }
}

println "\nDeleted ${count} jobs.\n"