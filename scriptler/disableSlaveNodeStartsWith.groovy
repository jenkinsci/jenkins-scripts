/*** BEGIN META {
  "name" : "Disable Jenkins Hudson slaves nodes gracefully for all slaves starting with a given value",
  "comment" : "Disables Jenkins Hudson slave nodes gracefully - waits until running jobs are complete.",
  "parameters" : [ 'slaveStartsWith'],
  "core": "1.350",
  "authors" : [
    { name : "GigaAKS" }, { name : "Arun Sangal" }
  ]
} END META**/

// This scriptler script will mark Jenkins slave nodes offline for all slaves which starts with a given value.
// It will wait for any slave nodes which are running any job(s) and then delete them.
// It requires only one parameter named: slaveStartsWith and value can be passed as: "swarm-".

import java.util.*
import jenkins.model.*
import hudson.model.*
import hudson.slaves.*

def atleastOneSlaveRunnning = true;
def time = new Date().format("HH:mm MM/dd/yy z",TimeZone.getTimeZone("EST"))

while (atleastOneSlaveRunnning) {
  
 //First thing - set the flag to false.
 atleastOneSlaveRunnning = false; 
 time = new Date().format("HH:mm MM/dd/yy z",TimeZone.getTimeZone("EST"))
  
 for (aSlave in hudson.model.Hudson.instance.slaves) {
   
   println "-- Time: " + time;
   println ""
   //Dont do anything if the slave name is "ansible01"
   if ( aSlave.name == "ansible01" ) {
        continue;
   }  
   if ( aSlave.name.indexOf(slaveStartsWith) == 0) {
       println "Active slave: " + aSlave.name; 
       
       println('\tcomputer.isOnline: ' + aSlave.getComputer().isOnline());
       println('\tcomputer.countBusy: ' + aSlave.getComputer().countBusy());
       println ""
       if ( aSlave.getComputer().isOnline()) {
            aSlave.getComputer().setTemporarilyOffline(true,null);
            println('\tcomputer.isOnline: ' + aSlave.getComputer().isOnline());    
            println ""
       }
       if ( aSlave.getComputer().countBusy() == 0 ) {
            time = new Date().format("HH:mm MM/dd/yy z",TimeZone.getTimeZone("EST"))
            println("-- Shutting down node: " + aSlave.name + " at " + time);
            aSlave.getComputer().doDoDelete(); 
       } else {
            atleastOneSlaveRunnning = true;  
       }
  }
 }
 //Sleep 60 seconds  
 if(atleastOneSlaveRunnning) { 
   println ""
   println "------------------ sleeping 60 seconds -----------------"
   sleep(60*1000); 
   println ""   
 } 
}

