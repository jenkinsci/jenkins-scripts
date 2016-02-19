/*** BEGIN META {
  "name" : "List and remove invalid credential not present in a whitelist regexp",
  "comment" : "Prevent people from use their own login/password (which expire every X months therefore breaking build/jobs) and forcing usage of service accounts with no expiration limit",
  "parameters" : [ 'dryRun', 'whitelistRegexp', 'moreInfo' ],
  "core": "1.642",
  "authors" : [
    { name : "Benjamin Francisoud" }
  ]
} END META**/

import hudson.scm.*
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials
import java.util.regex.Pattern

Date now = new Date()

// parameters
dryRun = dryRun ? dryRun.toBoolean(): true;
println "dryRun: ${dryRun}"
whitelistRegexp = whitelistRegexp ?: '(user|root|svcaccount).*'
println "whitelistRegexp: ${whitelistRegexp}"
pattern = Pattern.compile(whitelistRegexp)
description = "disable by removeInvalidCredentials.groovy on [[${now}]] (use valid service account regexp: [[${whitelistRegexp}]]) (${moreInfo})"
println "description: ${description}"

hudsonInstance = hudson.model.Hudson.instance
credentials = CredentialsProvider.lookupCredentials(StandardUsernameCredentials.class, hudsonInstance, null, null);

println '\n---- authorizedCredentials ---'
authorizedCredentials = credentials.findAll { credential -> pattern.matcher(credential.username).matches() }
authorizedCredentials.each { credential -> println "[${credential.id}] ${credential.username} (${credential.description})" }

println '\n---- invalidCredentials ---'
invalidCredentials = credentials.minus(authorizedCredentials)
invalidCredentials.each { credential -> println "[${credential.id}] ${credential.username} (${credential.description})" }

// No SCM-Configuration possible for External Jobs!
jobs = hudsonInstance.items.findAll{job -> (job.disabled == false && !(job instanceof hudson.model.ExternalJob) && (job.scm instanceof SubversionSCM)) }

println '\n---- matching jobs ---'
jobs.each { job ->
  job.scm.locations.each { location -> 
    isAuthorized = authorizedCredentials.any { authorizedCredential -> authorizedCredential.id == location.credentialsId }
    if (!isAuthorized) {
      println "${job.name} (isDisabled:${job.disabled} - isAuthorized:${isAuthorized} - credentialsId:${location.credentialsId})"
      if(!dryRun) {
        job.disabled = true
        if (job.description) {
          job.setDescription("${job.description} - ${description}")
        } else {
          job.setDescription(" - ${description}")
        }
      }
    }
  }
}

println '---- done ---\n'
