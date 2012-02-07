/*** BEGIN META {
  "name" : "Trigger via Snapshot",
  "comment" : "This script will make sure ALL of your maven jobs are triggered by builds on any snapshot dependencies.  There is a basic example of how to exclude jobs by name in the top of this script.",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "EJ Ciramella" }
  ]
} END META**/

import hudson.model.*

for(item in Hudson.instance.items) 
{
	// Don't run if the projet is a sonar call AND if the job isn't a maven 2/3 job
	if(!item.getDisplayName().contains("sonar") && item.getDescriptor().getDisplayName().contains("Build a maven2"))
    {    
		println("-----------------------------------------------");
		println(item.getDisplayName());
		println(item.getDescriptor().getDisplayName());
		println(item.getApi().getDisplayName());
		println(item.ignoreUpstremChanges());
		println(item.getBuildTriggerUpstreamProjects().size());
		item.setIgnoreUpstremChanges(false);
		println("-----------------------------------------------");
	}
}