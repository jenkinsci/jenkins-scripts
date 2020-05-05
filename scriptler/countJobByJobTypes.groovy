/*** BEGIN META {
 "name" : "Count Job by Job Types",
 "comment" : "A groovy script that counts the number of jobs by job types",
 "core": "1.609",
 "authors" : [
 { name : "support" }
 ]
 } END META**/

Set<String> jobTypes = new HashSet<String>();

Jenkins.instance.allItems(AbstractItem.class).each {
    jobTypes.add(it.class)
}

jobTypes.each { clazz ->
  println clazz.getCanonicalName() + ": " + Jenkins.instance.getAllItems(clazz).size()
}

println "--------------------------------------------------------------------------"
println "Total items : " + Jenkins.instance.getAllItems(AbstractItem.class).size()

return
