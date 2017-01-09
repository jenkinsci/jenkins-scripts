/*** BEGIN META {
    "name" : "Change secret text",
    "comment" : "Modify the secret text for an existing credential identified by the ID",
    "parameters" : ['id', 'secret'],
    "core": "1.609",
    "authors" : [
        { name : "Thomas LÉVEIL" },
        { name : "Hans Schulz" }
    ]
} END META**/


import org.jenkinsci.plugins.plaincredentials.StringCredentials
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import hudson.util.Secret
import jenkins.model.Jenkins

def changeSecret = { id, newSecret ->
    def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
            StringCredentials.class,
            Jenkins.instance)

    def c = creds.findResult { it.id == id ? it : null }
    if (c) {
        def credentials_store = Jenkins.instance.getExtensionList(
                'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
        )[0].getStore()

        def result = credentials_store.updateCredentials(
                com.cloudbees.plugins.credentials.domains.Domain.global(),
                c,
                new StringCredentialsImpl(c.scope, c.id, c.description, Secret.fromString(newSecret))
        )

        if (result) {
            println "Secret changed for credential ${c.id}"
        } else {
            throw new RuntimeException("Failed to change secret for ${c.id}")
        }
    } else {
        throw new RuntimeException("No existing credential with ID ${c.id}")
    }
}

changeSecret("${id}", "${secret}")
