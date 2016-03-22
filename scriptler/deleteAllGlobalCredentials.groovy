/*** BEGIN META {
 "name" : "Delete all global credentials",
 "comment" : "Delete all global credentials in Jenkins",
 "parameters" : [],
 "core": "1.625",
 "authors" : [
 { name : "Amit Modak" }
 ]
 } END META**/

import com.cloudbees.plugins.credentials.domains.Domain

def credentialsStore = jenkins.model.Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

allCreds = credentialsStore.getCredentials(Domain.global())
allCreds.each{
   credentialsStore.removeCredentials(Domain.global(), it)
}
