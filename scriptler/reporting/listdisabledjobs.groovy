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
