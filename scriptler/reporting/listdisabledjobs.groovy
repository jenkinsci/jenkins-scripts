/*** BEGIN META {
  "name" : "List Disabled Jobs",
  "comment" : "This script came about because there were many jobs that were disabled and interlaced between active jobs in the hundreds.  If you don't want to set up a new view that lists out just the disabled (or never run) jobs, this is a quick fix that will provide counts at the end of the run.",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "EJ Ciramella" }
  ]
} END META**/

import hudson.model.*
 
int nonbuildable = 0;
int buildable = 0;
for (item in Hudson.instance.items)
{
  buildable++
  if(!item.buildable)
  {
    //println(item.name + " " + "\"" + item.lastBuild + "\"");
    if(item.lastBuild != null && item.lastBuild.time != "")
    {
                    println(item.name + " " + item.lastBuild.time);
    }
    else
    {
     println(item.name + " " + "Never Built")
    }
    nonbuildable++
  }
}
println(nonbuildable)
println(buildable)
  int merge = 0;
for (item in Hudson.instance.items)
{
  if(item.name.contains("erge"))
  {
    println(item.name + " active=" + item.buildable);
    merge++
  }
}
println(merge);
