/*** BEGIN META {
  "name" : "Workspace Cleaner",
  "comment" : "This script will go through all workspaces for any/all jobs and remove them.",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "EJ Ciramella" }
  ]
} END META**/

import hudson.FilePath;

for (slave in hudson.model.Hudson.instance.slaves)
{ 
  FilePath fp = slave.createPath(slave.getRootPath().toString() + File.separator + "workspace"); 
  fp.deleteRecursive(); 
}