/*** BEGIN META {
 "name" : "Check Labels Nodes Online",
 "comment" : "Check if the nodes of each Labels are online/offline and display a simple report.",
 "parameters" : [ ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/
import jenkins.model.Jenkins

Jenkins.instance.labels.findAll{ !it.selfLabel }.each { label ->
    println "Label '$label':"
    label.nodes.each { node ->
        print "- '${node.getNodeName()}'"
        if (node.toComputer().isOffline()) {
            println " is offline"
        } else {
            println " is online [" +
                    "executors=${node.toComputer().countExecutors()}" +
                    ", busy=${node.toComputer().countBusy()}" +
                    ", idle=${node.toComputer().countIdle()}" +
                    "]"
        }
    }
}
return