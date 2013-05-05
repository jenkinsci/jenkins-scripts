/*** BEGIN META {
  "name" : "Jira issue update publisher",
  "comment" : 'Activate the <a href="https://wiki.jenkins-ci.org/display/JENKINS/JIRA+Plugin">Jira plugin</a> publisher',
  "parameters" : ['dryRun','jobs','jobsPattern'],
  "core": "1.424",
  "authors" : [
    { name : "Julien Carsique" }
  ]
} END META**/
import hudson.model.*

if (dryRun == "true") {
    println("Dry run")
}
def jobsList = []
if (!jobs.isEmpty()) {
    println("Working with jobs list: $jobs")
    for (jobName in jobs.split()) {
        jobItem = Hudson.getInstance().getItem(jobName)
        if (jobItem == null) {
            println("WARN $jobName not found!")
            continue
        }
        jobsList.add(jobItem)
    }
}
if (!jobsPattern.isEmpty()) {
    println("Working with jobs pattern: $jobsPattern")
    for (jobItem in Hudson.getInstance().getItems()) {
        for (jobPattern in jobsPattern.split()) {
            if (jobItem.getName() =~ jobPattern) {
                jobsList.add(jobItem)
                break
            }
        }
    }
}
if (jobs.isEmpty() && jobsPattern.isEmpty()) {
    jobsList = Hudson.getInstance().getItems()
}

for(item in jobsList) {
    println("\n[$item.name]")
    hasJira = false;
    for(p in item.getPublishersList()) {
        if(p instanceof hudson.plugins.jira.JiraIssueUpdater) {
            println("Jira publisher already active on $item.name")
            hasJira = true
        }
    }
    if(!hasJira) {
        println("Adding Jira publisher to $item.name")
        if (dryRun != "true") {
            item.getPublishersList().add(new hudson.plugins.jira.JiraIssueUpdater())
            item.save()
        }
    }
}