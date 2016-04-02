/*** BEGIN META {
  "name" : "Show Agent Java Version",
  "comment" : "It lists name and java version installed on each Agents",
  "parameters" : [],
  "core": "1.609",
  "authors" : [
    { name : "Ben Walding (kuisathaverat)" }
  ]
} END META**/

import hudson.FilePath
import hudson.model.Node
import hudson.model.Slave
import jenkins.model.Jenkins

Jenkins jenkins = Jenkins.instance
for (Node node in jenkins.nodes) {
  // Make sure slave is online
  if (!node.toComputer().online) {
    println "Node '$node.nodeName' is currently offline - skipping check"
    continue;
  } else {
    props = node.toComputer().getSystemProperties();
    println "Node '$node.nodeName' is running " + props.get('java.runtime.version');
  }
}
