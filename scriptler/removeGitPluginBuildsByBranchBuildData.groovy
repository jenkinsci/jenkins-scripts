/*** BEGIN META {
  "name" : "Remove Git Plugin BuildsByBranch BuildData",
  "comment" : "This script is used to remove the static list of BuildsByBranch that is uselessly stored for each build by the Git Plugin. This is a workaround for the problem described here: https://issues.jenkins-ci.org/browse/JENKINS-19022 Updated to handle Matrix Project types. Updated to better support SCM Polling",
  "parameters" : [],
  "core": "1.509.4",
  "authors" : [
    { name : "Scott Hebert" }
  ]
} END META**/

import hudson.matrix.*
import hudson.model.*

hudsonInstance = hudson.model.Hudson.instance
allItems = hudsonInstance.items

// Iterate over all jobs and find the ones that have a hudson.plugins.git.util.BuildData
// as an action.
//
// We then clean it by removing the useless array action.buildsByBranchName
//

for (job in allItems) {
  println("job: " + job.name);
  def counter = 0;
  for (build in job.getBuilds()) {
    // It is possible for a build to have multiple BuildData actions
    // since we can use the Mulitple SCM plugin.
    def gitActions = build.getActions(hudson.plugins.git.util.BuildData.class)
    if (gitActions != null) {
      for (action in gitActions) {
        action.buildsByBranchName = new HashMap<String, Build>();
        hudson.plugins.git.Revision r = action.getLastBuiltRevision();
        if (r != null) {
          for (branch in r.getBranches()) {
            action.buildsByBranchName.put(branch.getName(), action.lastBuild)
          }
        }
        build.actions.remove(action)
        build.actions.add(action)
        build.save();
        counter++;
      }
    }
    if (job instanceof MatrixProject) {
      def runcounter = 0;
      for (run in build.getRuns()) {
        gitActions = run.getActions(hudson.plugins.git.util.BuildData.class)
        if (gitActions != null) {
          for (action in gitActions) {
            action.buildsByBranchName = new HashMap<String, Build>();
            hudson.plugins.git.Revision r = action.getLastBuiltRevision();
            if (r != null) {
              for (branch in r.getBranches()) {
                action.buildsByBranchName.put(branch.getName(), action.lastBuild)
              }
            }
            run.actions.remove(action)
            run.actions.add(action)
            run.save();
            runcounter++;
          }
        }
      }
      if (runcounter > 0) {
        println(" -->> cleaned: " + runcounter + " runs");
      }
    }
  }
  if (counter > 0) {
    println("-- cleaned: " + counter + " builds");
  }
}
