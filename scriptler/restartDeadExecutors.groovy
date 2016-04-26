/*** BEGIN META {
 "name" : "Restart Dead Executors",
 "comment" : "Search for dead executors and throws away them and get a new ones.",
 "core": "1.609",
 "authors" : [
 { name : "Kuisathaverat" }
 ]
 } END META**/
 
import hudson.model.Node
import jenkins.model.Jenkins

Jenkins jenkins = Jenkins.instance
for (Node node in jenkins.nodes) {
  // Make sure slave is online
  if (!node.toComputer().online) {
    println "Node '$node.nodeName' is currently offline - skipping check"
  } else {
    props = node.toComputer().getSystemProperties();
    println "Node '$node.nodeName' is running ";
    //check if has executors dead
    for (Executor ex : node.toComputer().getExecutors()){
      Throwable cause = ex.getCauseOfDeath()
      if(cause instanceof Throwable){
        println '[Dead]' + cause
        ex.doYank()
      }
    }
  }
}
