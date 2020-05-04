/*** BEGIN META {
 "name" : "List EC2 Cloud instances",
 "comment" : "Iterate all EC2 Clouds and every template on them, to list the instances running",
 "parameters" : [],
 "core": "2.32",
 "authors" : [{ name : "kuisathaverat" }]
 } END META**/

import com.amazonaws.services.ec2.model.InstanceStateName

Jenkins.instance.clouds
    .findAll{ it -> it instanceof hudson.plugins.ec2.AmazonEC2Cloud}
    .each{ c -> 
        println c.getCloudName() + ' - ' + c.getRegion() + ' - CAP:' + c.instanceCap 
        c.getTemplates()
            .each{ t-> 
            println '\t' + t.description + ' - CAP:' + t.instanceCap
            String description = t?.description;
            int running = 0
            int terminated = 0
            int shuttingdown = 0
            c.connect()?.describeInstances()?.getReservations().each{ r ->
                r?.getInstances().each{ i ->
                    if (t.getAmi().equals(i.getImageId())) {
                        InstanceStateName stateName = InstanceStateName.fromValue(i.getState().getName());
                        if (stateName != InstanceStateName.Terminated && stateName != InstanceStateName.ShuttingDown) {
                             running++
                        } else if (stateName == InstanceStateName.Terminated) {
                           terminated++
                        } else if (stateName == InstanceStateName.ShuttingDown) {
                           shuttingdown++
                        }
                        println "\t\tExisting instance found: " + i.getInstanceId() + " AMI: " + i.getImageId() + ' - State:' + stateName    
                     }
                }
            }
        println "\tTotal Intances Running:" + running
        println "\tTotal Intances Terminated:" + terminated
        println "\tTotal Intances ShuttingDown:" + shuttingdown
    }
}
return
