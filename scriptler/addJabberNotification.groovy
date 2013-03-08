/*** BEGIN META {
 "name" : "Add Jabber Notification",
 "comment" : "Add jabber notifications to the specified jobs",
 "parameters" : [ 'views', 'jobs', 'targets', 'strategy', 'notifyOnBuildStart', 'notifySCMCommitters', 'notifySCMCulprits', 'notifyUpstreamCommitters', 'notifySCMFixers'],
 "core": "1.409",
 "authors" : [
 { name : "Eric Dalquist" }
 ]
 } END META**/

import hudson.maven.*
import hudson.maven.reporters.*
import hudson.model.*
import hudson.plugins.im.*
import hudson.plugins.im.build_notify.*
import hudson.plugins.jabber.im.transport.*
import hudson.tasks.*

def viewNames = "${views}";
viewNames = viewNames.split(",");
def jobNames = "${jobs}";
jobNames = jobNames.split(",");

println("Views:      " + viewNames);
println("Jobs:       " + jobNames);
println();

def targetsAsString = "${targets}"; //chat@conferences.example.com
def notificationStrategy = "${strategy}"; //"failure and fixed"
def notifyGroupChatsOnBuildStart = "${notifyOnBuildStart}".toBoolean(); // true/false
def notifySuspects = "${notifySCMCommitters}".toBoolean(); // true/false
def notifyCulprits = "${notifySCMCulprits}".toBoolean(); // true/false
def notifyUpstreamCommitter = "${notifyUpstreamCommitters}".toBoolean(); // true/false
def notifyFixers = "${notifySCMFixers}".toBoolean(); // true/false

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

def conv = new JabberIMMessageTargetConverter();
def targetsSplit = targetsAsString.split("\\s");
println(targetsSplit);

List<IMMessageTarget> targets = new ArrayList<IMMessageTarget>(targetsSplit.length);
for (String target : targetsSplit) {
    def msgTarget = conv.fromString(target);
    if (msgTarget != null)  {
        targets.add(msgTarget);
    }
}
def printPublisher = { publisher ->
    println(publisher);
    println("\t" + publisher.getTargets());
    println("\t" + publisher.getNotificationStrategy());
    println("\t" + publisher.getStrategy());
    println("\t" + publisher.getNotifyOnStart());
    println("\t" + publisher.getNotifySuspects());
    println("\t" + publisher.getNotifyCulprits());
    println("\t" + publisher.getNotifyFixers());
    println("\t" + publisher.getNotifyUpstreamCommitters());
};


// For each project
for(item in items) {
    println(item.name + ": Checking for jabber notifiers");
    // Find current recipients defined in project
    if(!(item instanceof ExternalJob)) {
        for (Iterator publishersItr = item.publishersList.iterator(); publishersItr.hasNext();) {
            def publisher = publishersItr.next();
            if (publisher instanceof JabberPublisher) {
                addedToPublisher = true;
                println("Deleting:");
                printPublisher(publisher);
                
                publishersItr.remove();
            }
        }

        def newPublisher = new JabberPublisher(
            targets,
            notificationStrategy,
            notifyGroupChatsOnBuildStart,
            notifySuspects,
            notifyCulprits,
            notifyFixers,
            notifyUpstreamCommitter,
            new DefaultBuildToChatNotifier(),
            MatrixJobMultiplier.ONLY_CONFIGURATIONS);
        
        println("Adding:");
        printPublisher(newPublisher);
        item.publishersList.add(newPublisher);
    }
}