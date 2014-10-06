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