/*** BEGIN META {
  "name" : "Change credential password",
  "comment" : "Modify the password for an existing credential identified by username",
  "parameters" : ['username', 'password'],
  "core": "1.609",
  "authors" : [
    { name : "Thomas LÉVEIL" }
  ]
} END META**/

import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl

def changePassword = { username, new_password ->
    def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.common.StandardUsernameCredentials.class,
        jenkins.model.Jenkins.instance
    )

    def c = creds.findResult { it.username == username ? it : null }

    if ( c ) {
        println "found credential ${c.id} for username ${c.username}"

        def credentials_store = jenkins.model.Jenkins.instance.getExtensionList(
            'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
            )[0].getStore()

        def result = credentials_store.updateCredentials(
            com.cloudbees.plugins.credentials.domains.Domain.global(), 
            c, 
            new UsernamePasswordCredentialsImpl(c.scope, c.id, c.description, c.username, new_password)
            )

        if (result) {
            println "password changed for ${username}" 
        } else {
            println "failed to change password for ${username}"
        }
    } else {
      println "could not find credential for ${username}"
    }
}

changePassword("$username", "$password")