/*** BEGIN META {
  "name" : "Update a VaultTokenCredential of hashicorp-vault-plugin",
  "comment" : "Update the Token for a specific credential ID for a VaultTokenCredential of https://plugins.jenkins.io/hashicorp-vault-plugin",
  "parameters" : ["vaultCredentialID", "newTokenValue"],
  "core": "2.107.3",
  "authors" : [
    { name : "Ray Kivisto" }
  ]
} END META**/

// https://github.com/jenkinsci/hashicorp-vault-plugin/blob/master/src/main/java/com/datapipe/jenkins/vault/credentials/VaultTokenCredential.java

import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.datapipe.jenkins.vault.credentials.*
import hudson.util.Secret

def updateVaultTokenCredential = { credentialID, newPassword ->
  def credentialsStore = jenkins.model.Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
  def credentials = credentialsStore.getCredentials(Domain.global())
  credentials.each{
    if (it.id==credentialID){
      if ( credentialsStore.updateCredentials(
             com.cloudbees.plugins.credentials.domains.Domain.global(),
             it,
             new VaultTokenCredential(it.scope, it.id, it.description, new Secret(newPassword) ) ) ) {
        println "${credentialID} updated" 
      } else {
        println "ERROR: unable to update ${credentialID}"
      }
    }
  }
}

updateVaultTokenCredential(vaultCredentialID, newTokenValue)
