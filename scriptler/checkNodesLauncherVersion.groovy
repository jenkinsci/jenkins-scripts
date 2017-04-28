/*** BEGIN META {
 "name" : "Check Nodes Version",
 "comment" : "Check the .jar version and the java version of the Nodes against the Master versions",
 "parameters" : [ ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import hudson.remoting.Launcher
import hudson.slaves.SlaveComputer
import jenkins.model.Jenkins

def expectedAgentVersion = Launcher.VERSION
def expectedJavaVersion = System.getProperty("java.version")
println "Master"
println " Expected Agent Version = '${expectedAgentVersion}'"
println " Expected Java Version = '${expectedJavaVersion}'"
Jenkins.instance.getComputers()
        .findAll { it instanceof SlaveComputer }
        .each { computer ->
    println "Node '${computer.name}'"
    if (!computer.getChannel()) {
        println " is disconnected."
    } else {
        def isOk = true
        def agentVersion = computer.getSlaveVersion()
        if (!expectedAgentVersion.equals(agentVersion)) {
            println " expected agent version '${expectedAgentVersion}' but got '${agentVersion}'"
            isOk = false
        }
        def javaVersion = computer.getSystemProperties().get("java.version")
        if (!expectedJavaVersion.equals(javaVersion)) {
            println " expected java version '${expectedJavaVersion}' but got '${javaVersion}'"
            isOk = false
        }

        if(isOk) {
            println " OK"
        }
    }
}
return;