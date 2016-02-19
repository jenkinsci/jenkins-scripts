/*** BEGIN META {
  "name" : "Show Java Version of job",
  "comment" : "Show Java Version used on job",
  "parameters" : [],
  "core": "1.609",
  "authors" : [
    { name : "kuisathaverat" }
  ]
} END META**/

import jenkins.model.Jenkins
import hudson.model.AbstractProject
import com.cloudbees.hudson.plugins.folder.Folder
import hudson.triggers.TimerTrigger

Jenkins jenkins = Jenkins.instance

findAllItems(jenkins.items)

def findAllItems(items){
  for(item in items)
  {
    if (!(item instanceof Folder)) {
      if(item instanceof AbstractProject && !item.disabled && item.getJDK()) {
        println 'Job : ' + item.getName() + ' - Name : ' + item.getJDK().getName() + ' - Home : ' + item.getJDK().getHome()
        println item.getJDK().getProperties()
      }
    } else {
      findAllItems(((Folder) item).getItems())
    }
  }
}
