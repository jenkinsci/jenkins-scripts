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

println("All Nodes - executor count:")
println()
Jenkins.instance.computers.eachWithIndex() { c, i -> println " [${i+1}] ${c.displayName}: ${c.numExecutors}" };
println()
println("Total nodes: [" + Jenkins.instance.computers.size() + "]")
println("Total executors: [" + Jenkins.instance.computers.inject(0, {a, c -> a + c.numExecutors}) + "]")