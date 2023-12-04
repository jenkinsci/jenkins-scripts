/*** BEGIN META {
  "name" : "Disable Jenkins agent nodes gracefully for all slaves starting with a given value",
  "comment" : "Disables Jenkins agent nodes gracefully - waits until running jobs are complete.",
  "parameters" : [ 'slaveStartsWith'],
  "core": "1.350",
  "authors" : [
    { name : "GigaAKS" }, { name : "Arun Sangal" }
  ]
} END META**/

// This scriptler script will mark Jenkins agent nodes offline for all slaves which starts with a given value.
// It will wait for any agent nodes which are running any job(s) and then delete them.
// It requires only one parameter named: slaveStartsWith and value can be passed as: "swarm-".

import java.util.*
import jenkins.model.*
import hudson.model.*
import hudson.slaves.*

def atleastOneAgentRunnning = true;
def time = new Date().format("HH:mm MM/dd/yy z",TimeZone.getTimeZone("EST"))

while (atleastOneAgentRunnning) {
  
 //First thing - set the flag to false.
 atleastOneAgentRunnning = false; 
 time = new Date().format("HH:mm MM/dd/yy z",TimeZone.getTimeZone("EST"))
  
 for (agent in hudson.model.Hudson.instance.slaves) {
   
   println "-- Time: " + time;
   println ""
   //Dont do anything if the agent name is "ansible01"
   if ( aAgent.name == "ansible01" ) {
        continue;
   }  
   if ( agent.name.indexOf(slaveStartsWith) == 0) {
       println "Active agent: " + agent.name; 
       
       println('\tcomputer.isOnline: ' + agent.getComputer().isOnline());
       println('\tcomputer.countBusy: ' + agent.getComputer().countBusy());
       println ""
       if ( agent.getComputer().isOnline()) {
            agent.getComputer().setTemporarilyOffline(true,null);
            println('\tcomputer.isOnline: ' + agent.getComputer().isOnline());    
            println ""
       }
       if ( agent.getComputer().countBusy() == 0 ) {
            time = new Date().format("HH:mm MM/dd/yy z",TimeZone.getTimeZone("EST"))
            println("-- Shutting down node: " + agent.name + " at " + time);
            agent.getComputer().doDoDelete(); 
       } else {
            atleastOneAgentRunnning = true;  
       }
  }
 }
 //Sleep 60 seconds  
 if(atleastOneAgentRunnning) { 
   println ""
   println "------------------ sleeping 60 seconds -----------------"
   sleep(60*1000); 
   println ""   
 } 
}

