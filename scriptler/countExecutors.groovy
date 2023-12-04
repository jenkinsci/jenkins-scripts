/*** BEGIN META {
  "name" : "Count executors",
  "comment" : "Shows the total number of nodes and executors on Jenkins",
  "parameters" : [ ],
  "core": "1.350",
  "authors" : [
    { name : "Andy Pemberton" }
  ]
} END META**/
import jenkins.model.Jenkins

println("====== Regular Slave Executors ======")
println()

// Jenkins Master and slaves
// TODO perhaps filter other known cloud slaves; shame there isn't a cleaner way to know them
def regularSlaves = Jenkins.instance.computers.grep{ 
  it.class.superclass?.simpleName != 'AbstractCloudComputer' &&
  it.class.superclass?.simpleName != 'AbstractCloudSlave' &&
  it.class.simpleName != 'EC2AbstractSlave'
}
int regularSlaveExecutorCount = regularSlaves.inject(0, {a, c -> a + c.numExecutors})

println("| Node Name | Type | Executors |")
regularSlaves.each {
  println "| ${it.displayName} | ${it.class.simpleName} | ${it.numExecutors} |" 
}
println()

println("Total: " + regularSlaveExecutorCount + " master & regular slave executors")
println()

println("====== Shared Slave Executors ======")
println()

// CJOC Shared Slaves
def sharedSlaves = Jenkins.instance.allItems.grep{
  it.class.name == 'com.cloudbees.opscenter.server.model.SharedSlave' 
}
int sharedSlaveExecutorCount = sharedSlaves.inject(0, {a, c -> a + c.numExecutors})

println("| Node Name | Type | Executors |")
sharedSlaves.each {
  println "| ${it.displayName} | ${it.class.simpleName} | ${it.numExecutors} |"
}
println()

println("Total: " + sharedSlaveExecutorCount + " shared slave executors")
println()

println("====== Cloud Slave Executors ======")
println()

println("| Cloud Name | Type | Max. Executors |")
int totalInstanceCaps
Jenkins.instance.clouds.each { cloud ->
  Integer instanceCaps
  try{
    instanceCaps = cloud.templates?.inject(0, {a, c -> a + (c.numExecutors * c.instanceCap)})
    totalInstanceCaps += instanceCaps
  }
  catch(e){}
  finally{}

  println "| ${cloud.displayName} | ${cloud.descriptor.displayName} | ${instanceCaps ? instanceCaps : 'Unknown'} |"
}

println()
println("Total: up to " + totalInstanceCaps + " cloud executors")

return