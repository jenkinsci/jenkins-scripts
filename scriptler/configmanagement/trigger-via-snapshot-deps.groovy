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