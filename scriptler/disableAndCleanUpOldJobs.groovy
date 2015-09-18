/*** BEGIN META {
  "name" : "disable and clean up workspace for old jobs",
  "comment" : "Disable and clean up workspace for jobs that were built more than 90 day ago",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Dmitriy Slupytskyi" }
  ]
} END META**/

import hudson.model.*
import hudson.node_monitors.*
import hudson.slaves.*
import hudson.FilePath
import java.util.concurrent.*

jenkins = Hudson.instance

// Define day to compare (will find builds that were built more than n day ago)
day=90;
hour=24;
minute=60;
second=60;
daysInSecond=day*hour*minute*second;
now=Calendar.instance;
list=[];
listFreezeBuild=[];
listProceeded=[];

println("The build is run at ${now.time}");

println("\n### GET LIST OF ALL JOBS ###\n")

for (item in jenkins.items) {
  println("\tProcessing ${item.name}...")
  // ignore project that contains freeze or patch case insensitive
  if (item.name ==~ /(?i)(freeze|patch).*/) {
    // add item to list
    listFreezeBuild << item;
  } else if (!item.disabled&&item.getLastBuild()!=null) {
    // caculate build time
    build_time=item.getLastBuild().getTimestamp();
    // compare build time with current time
    if (now.time.time/1000-build_time.time.time/1000>daysInSecond) {
      // add item to list
      list << item;
    }
  }
}

println("\n### DISABLE OLD JOBS ###\n")

def disableJob(item) {
  if (item.class.canonicalName != 'com.cloudbees.hudson.plugins.folder.Folder') {
    // disable item
    item.disabled=true;
    // save
    item.save();
  }
}

def pushLogRotate(item) {
  // perform log rotation
  item.logRotate();
}

def wipeOutWorkspace(item) {
  // check if build is not in building stage
  if(!item.isBuilding()) {
    try {
      // wipe out the workspace
      item.doDoWipeOutWorkspace();
    }
    catch (ex) {
      println("Error: " + ex);
    }
  }
}

def wipeOutWorkspaceFromSlaves(item) {
  for (slave in hudson.model.Hudson.instance.slaves) {
    // get slave name
    slaveNodeName = slave.getNodeName();
    println("\t\tCheck ${slaveNodeName} slave...");
    // get workspace root for specify slave
    workspaceRoot = slave.getWorkspaceRoot();
    // create path slave with workspace
    FilePath fp = slave.createPath(workspaceRoot.toString() + File.separator + item.name);
    // check that workspace root exists and path to workspace exists
    if ((workspaceRoot != null) && (fp.exists())) {
      // delete workspace recursively
      println("\t\tWipe out ${fp}...")
      try {
        fp.deleteRecursive();
      }
      catch (ex) {
        println("Error: " + ex);
      }
    }
  }
}

if (list.size() > 0) {
  for (item in list) {
    println("\tDisabling ${item.name}...");
    disableJob(item);
    println("\tPush log rotate for ${item.name}...");
    pushLogRotate(item);
    println("\tWipe out workspace for ${item.name}...");
    wipeOutWorkspace(item);
    println("\tWipe out workspace for ${item.name} from slaves...")
    wipeOutWorkspaceFromSlaves(item);
    // add item to list
    listProceeded << item;
  }
}

println("\n### SUMMARY INFORMATION ###\n");

if (listFreezeBuild.size() > 0) {
  println("Next jobs were ignored as it is a freeze or patch build:")
  for (item in listFreezeBuild) {
     println("\t${item.name}");
  }
}

if (listProceeded.size() > 0) {
  println("Next jobs were procced, disabled and cleaned workspace:")
  for (item in listProceeded) {
     println("\t${item.name}");
  }
}

return 0;
