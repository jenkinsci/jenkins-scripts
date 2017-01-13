/*** BEGIN META {
 "name" : "Check Node Online",
 "comment" : "Check if nodes are online/offline and display a simple report",
 "parameters" : [ ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import jenkins.model.Jenkins

Jenkins.instance.nodes.each {
    if (it.toComputer().isOffline()) {
        println "${it.getNodeName()} is offline"
    } else {
        println "${it.getNodeName()} is online"
        println " is online [" +
                "executors=${it.toComputer().countExecutors()}" +
                ", busy=${it.toComputer().countBusy()}" +
                ", idle=${it.toComputer().countIdle()}" +
                "]"
    }
}
return