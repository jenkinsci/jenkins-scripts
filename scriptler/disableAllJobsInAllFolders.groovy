/*** BEGIN META {
      "name" : "Disable all jobs in all folders",
      "comment" : "Disables <b>all jobs</b> in <b>all folders</b> in your Jenkins server",
      "parameters" : [],
      "core": "1.300",
      "authors" : [
        { name : "Christian Haeussler - https://github.com/cniweb" }
      ]
    } END META**/
import hudson.model.*

disableChildren(Hudson.instance.items)

def disableChildren(items) {
  for (item in items) {
    if (item.class.canonicalName != 'com.cloudbees.hudson.plugins.folder.Folder') {
      item.disabled=true
      item.save()
      println(item.name)
    } else {      
      disableChildren(((com.cloudbees.hudson.plugins.folder.Folder) item).getItems())
    }
  }
}
