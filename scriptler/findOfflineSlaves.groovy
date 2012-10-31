/*** BEGIN META {
 "name" : "Find Offline Slaves",
 "comment" : "Find offline slaves with a given name prefix.",
 "parameters" : [ 'namePrefix' ],
 "core": "1.409",
 "authors" : [
 { name : "Gareth Bowles" }
 ]
 } END META**/


// NOTE: uncomment parameter below if not using Scriptler >= 2.0, or if you're just pasting
// the script in manually.

// The name prefix for the slaves.
//def namePrefix = "my-slave"


import hudson.model.*
import hudson.node_monitors.*
import hudson.slaves.*
import java.util.concurrent.*

hudson = Hudson.instance

def getEnviron(computer) {
    def env
    def thread = Thread.start("Getting env from ${computer.name}", { env = computer.environment })
    thread.join(2000)
    if (thread.isAlive()) thread.interrupt()
    env
}

def slaveAccessible(computer) {
    getEnviron(computer)?.get('JAVA_HOME') != null
}

for (slave in hudson.slaves) {
    if (slave.name.startsWith(${namePrefix})) {
        def computer = slave.computer
        print "Checking computer ${computer.name}:"
        def isOK = slaveAccessible(computer)
        if (isOK) {
            println "\t\tOK"
        } else {
            println "  can't get JAVA_HOME from slave: must be wedged."
        }
    }
}