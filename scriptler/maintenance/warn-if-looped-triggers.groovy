import hudson.model.*

for(item in Hudson.instance.items) 
{
	println("-----------------------------------------------");
	println(item.getDisplayName());
	ArrayList dsprojects = item.getDownstreamProjects();
	println(dsprojects.size());
	for(int x = 0; x < dsprojects.size(); x++)
	{
		println("Here is the downstream project name - " + dsprojects.get(x).getDisplayName());    
	}
	ArrayList usprojects = item.getUpstreamProjects();
	println(usprojects.size());
	for(int x = 0; x < usprojects.size(); x++)
	{
		println("Here is the upstream project name - " + usprojects.get(x).getDisplayName());    
	}

	for (int x = 0; x < dsprojects.size(); x++)
	{
		for (int y = 0; y < usprojects.size(); y++)
		{
			String upstreamname = usprojects.get(y).getDisplayName();
			String downstreamname = dsprojects.get(x).getDisplayName();
			if (upstreamname.equalsIgnoreCase(downstreamname))
			{
				println("Yerfooked");
			}
		}

	}
	println("-----------------------------------------------");
}