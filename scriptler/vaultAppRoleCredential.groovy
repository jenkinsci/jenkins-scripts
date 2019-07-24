/*** BEGIN META {
  "name" : "Update VaultAppRoleCredential of hashicorp-vault-plugin",
  "comment" : "Update the Secret ID for a specific Role ID for a VaultAppRoleCredential of https://plugins.jenkins.io/hashicorp-vault-plugin",
  "parameters" : ["vaultRoleID", "newSecret"],
  "core": "2.107.3",
  "authors" : [
    { name : "Ray Kivisto" }
  ]
} END META**/

// https://github.com/jenkinsci/hashicorp-vault-plugin/blob/master/src/main/java/com/datapipe/jenkins/vault/credentials/VaultAppRoleCredential.java

import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.datapipe.jenkins.vault.credentials.*
import hudson.util.Secret

def updateVaultAppRoleCredential = { roleID, newPassword ->
  def credentialsStore = jenkins.model.Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
  def credentials = credentialsStore.getCredentials(Domain.global())
  credentials.each{
    if (it.getRoleId()==roleID){
      if ( credentialsStore.updateCredentials(
             com.cloudbees.plugins.credentials.domains.Domain.global(),
             it,
             new VaultAppRoleCredential(it.scope, it.id, it.description, it.roleId, new Secret(newPassword) ) ) ) {
        println "${roleID} updated" 
      } else {
        println "ERROR: unable to update ${roleID}"
      }
    }
  }
}

updateVaultAppRoleCredential(vaultRoleID, newSecret)
