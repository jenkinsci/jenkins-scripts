/*** BEGIN META {
  "name" : "plugins lister",
  "comment" : "print list of plugins (optionally set build.displayName)",
  "parameters" : [],
  "core": "0.601",
  "authors" : [
    { name : "Mark Hudson" }
  ]
} END META**/

def pcount = 0 ; def pstr = ''
def plist = jenkins.model.Jenkins.instance.pluginManager.plugins

plist.sort{it}  // plugins list `it` defaults to shortName

plist.each {
  pcount = pcount + 1
  pname = (pcount + ' ' + it).replaceAll("Plugin:", '')
  pstr = pstr + ' ' + pname + ' ' + it.getVersion() + "\n"  // + "<br>"
}

print pstr

if ( "executable" in Thread.currentThread().getProperties() ) {
  print Thread.currentThread().getProperties()
  def manager_build = Thread.currentThread().executable ; assert manager_build  // non-Postbuild context
  manager_build.displayName =  "#" + manager_build.number + " had " + pcount + " plugins"
} else
{
  print "(not setting displayName in non-executable)"
}

return

