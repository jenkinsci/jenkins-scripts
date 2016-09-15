/*** BEGIN META {
 "name" : "Interrupt Polling Threads",
 "comment" : "Interrupt Polling Threads running for a certain amount of time. Script based on a comment in JENKINS-5413.",
 "parameters" : [ "duration" ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import jenkins.model.Jenkins

/**
 * Interrupt any running Polling Threads that are currently running for more than `duration` seconds. This can be tuned.
 */
Jenkins.instance.getTrigger("SCMTrigger").getRunners().each() {
    item ->
        println(item.getTarget().name)
        println(item.getDuration())
        println(item.getStartTime())
        long millis = Calendar.instance.time.time - item.getStartTime()

        if (millis > (1000 * Integer.parseInt(duration))) {
            Thread.getAllStackTraces().keySet().each() {
                tItem ->
                    if (tItem.getName().contains("SCM polling") && tItem.getName().contains(item.getTarget().name)) {
                        println "Interrupting thread " + tItem.getId() + " " + tItem.getName();
                        tItem.interrupt()
                    }
            }
        }
}
