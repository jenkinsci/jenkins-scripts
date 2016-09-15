/*** BEGIN META {
 "name" : "add credentials to folder",
 "comment" : "Sample groovy script to add credentials to Jenkins's folder into global domain",
 "core": "1.609",
 "authors" : [
 { name : "Kuisathaverat" }
 ]
 } END META**/

import com.cloudbees.hudson.plugins.folder.properties.FolderCredentialsProvider.FolderCredentialsProperty
import com.cloudbees.hudson.plugins.folder.AbstractFolder
import com.cloudbees.hudson.plugins.folder.Folder
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*

String id = UUID.randomUUID().toString()
Credentials c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, id, "description:"+id, "user", "password")

Jenkins.instance.getAllItems(Folder.class)
    .findAll{it.name.equals('FolderName')}
    .each{
        AbstractFolder<?> folderAbs = AbstractFolder.class.cast(it)
        FolderCredentialsProperty property = folderAbs.getProperties().get(FolderCredentialsProperty.class)
        if(property != null){
            property.getStore().addCredentials(Domain.global(), c)
            println property.getCredentials().toString()
        }
    }
