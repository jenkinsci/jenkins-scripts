/*** BEGIN META {
      "name" : "disable all jobs",
      "comment" : "disables all jobs",
      "parameters" : [],
      "core": "1.300",
      "authors" : [
        { name : "Nicolas Mommaerts" }
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