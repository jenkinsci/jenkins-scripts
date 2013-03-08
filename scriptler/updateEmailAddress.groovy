/*** BEGIN META {
 "name" : "Update Email Recipients",
 "comment" : "Update Email Recipients for multiple jobs/views",
 "parameters" : [ 'views', 'jobs', 'recipients' ],
 "core": "1.409",
 "authors" : [
 { name : "Eric Dalquist" }
 ]
 } END META**/

import hudson.plugins.emailext.*
import hudson.model.*
import hudson.maven.*
import hudson.maven.reporters.*
import hudson.tasks.*

def viewNames = "${views}";
viewNames = viewNames.split(",");
def jobNames = "${jobs}";
jobNames = jobNames.split(",");
def newRecipients = "${recipients}";

println("Views:      " + viewNames);
println("Jobs:       " + jobNames);
println("Recipients: " + newRecipients);
println();

def items = new LinkedHashSet();

if (viewNames != null && viewNames != "") {
    for (viewName in viewNames) {
        viewName = viewName.trim();
        def view = Hudson.instance.getView(viewName)
        items.addAll(view.getItems());
    }
}

if (jobNames != null && jobNames != "") {
    for (jobName in jobNames) {
        jobName = jobName.trim();
        def job = Hudson.instance.getJob(jobName)
        items.add(job);
    }
}

// For each project
for(item in items) {
    println(item.name + ": Checking for email notifiers");
    // Find current recipients defined in project
    if(!(item instanceof ExternalJob)) {
        if(item instanceof MavenModuleSet) {
            // Search for Maven Mailer Reporter
            for(reporter in item.reporters) {
                if(reporter instanceof MavenMailer) {
                    println(item.name + " - Updating reporter: " + reporter + " changing recipients from '" + reporter.recipients + "' to '" + newRecipients + "'");
                    reporter.recipients = newRecipients;
                }
            }
        }

        for(publisher in item.publishersList) {
            // Search for default Mailer Publisher (doesn't exist for Maven projects)
            if(publisher instanceof Mailer) {
                println(item.name + " - Updating publisher: " + publisher + " changing recipients from '" + publisher.recipients + "' to '" + newRecipients + "'");
                publisher.recipients = newRecipients;
            } 
            // Or for Extended Email Publisher
            else if(publisher instanceof ExtendedEmailPublisher) {
                println(item.name + " - Updating publisher: " + publisher + " changing recipients from '" + publisher.recipientList + "' to '" + newRecipients + "'");
                publisher.recipientList = newRecipients;
            }
        }
    }
}
