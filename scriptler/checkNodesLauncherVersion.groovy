/*** BEGIN META {
 "name" : "Check Nodes Version",
 "comment" : "Check the .jar version of the Nodes against the Master version",
 "parameters" : [ ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import hudson.remoting.Launcher
import hudson.slaves.SlaveComputer
import jenkins.model.Jenkins

def expectedVersion = Launcher.VERSION
println "Expected Version = ${expectedVersion}"
Jenkins.instance.getComputers()
        .findAll { it instanceof SlaveComputer }
        .each { computer ->
    if (!computer.getChannel()) {
        println "Node ${computer.name} disconnected.."
    } else {
        def version = computer.getSlaveVersion()
        if (!expectedVersion.equals(version)) {
            println "${computer.name} - expected ${expectedVersion} but got ${version}"
        } else {
            println "${computer.name} - OK"
        }
    }
}
return;