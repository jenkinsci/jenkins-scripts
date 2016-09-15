/*** BEGIN META {
  "name" : "Show Java Version of job",
  "comment" : "Show Java Version used on job",
  "parameters" : [],
  "core": "1.609",
  "authors" : [
    { name : "kuisathaverat" }
  ]
} END META**/

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.triggers.TimerTrigger

findAllItems(Jenkins.instance.items)

def findAllItems(items){
    items.each{
        if (!(it instanceof Folder)) {
            if(it instanceof AbstractProject && !it.disabled && it.getJDK()) {
                println 'Job : ' + it.getName() + ' - Name : ' + it.getJDK().getName() + ' - Home : ' + it.getJDK().getHome()
                println it.getJDK().getProperties()
            }
        } else {
            findAllItems(((Folder) it).getItems())
        }
    }
}
