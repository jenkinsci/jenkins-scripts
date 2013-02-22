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
for(item in jenkins.model.Jenkins.instance.items) {
  // check that job is not building
  if(!item.isBuilding()) {
    println("Wiping out workspace of job "+item.name)
    if (!"true".equals(dryRun)) {
      item.doDoWipeOutWorkspace()
    }
  }
  else {
    println("Skipping job "+item.name+", currently building")
  }
}

