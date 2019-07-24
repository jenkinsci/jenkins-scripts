/*** BEGIN META {
  "name" : "add git clean before checkout",
  "comment" : "This script will add git clean after checkout to all Jenkins items ",
  "parameters" : [],
  "authors" : [
    { name : "Zamir Ivry - zamir.ivry@gmail.com" }
  ]
} END META**/

for(item in Hudson.instance.items) {
    println item.getSCMs();
    for(scm in item.getSCMs())
    {
        if(scm instanceof hudson.plugins.git.GitSCM)
        {
            es = scm.getExtensions()
            if(hasCheckOutPlugin(es, b))
            {
                println "already exists in " + item.getName()
                continue;
            }
            println "adding clean to " + item.getName()
            es.add(new hudson.plugins.git.extensions.impl.CleanCheckout())
            item.save()
        }
    }
}

private java.lang.Boolean hasCheckOutPlugin(es, java.lang.Boolean b) {
    for(s in es) {
        if(s instanceof hudson.plugins.git.extensions.impl.CleanCheckout) {
            return true;
        }
    }
    return false;
}
