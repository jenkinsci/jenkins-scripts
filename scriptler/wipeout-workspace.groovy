/*** BEGIN META {
  "name" : "Wipeout Workspace",
  "comment" : "This script will go through all workspaces for all jobs and wipe them.",
  "parameters" : [ 'dryRun' ],
  "core": "1.499",
  "authors" : [
    { name : "Vincent Dupain" }
  ]
} END META**/
// For each project
jenkins.model.Jenkins.instance.getAllItems(hudson.model.AbstractProject).each { job ->
  if (job.building) {
    println "Skipping job $job.name, currently building"
  } else {
    println "Wiping out workspace of $job.name"
    if (dryRun != 'true') {
      job.doDoWipeOutWorkspace()
    }
  }
}
