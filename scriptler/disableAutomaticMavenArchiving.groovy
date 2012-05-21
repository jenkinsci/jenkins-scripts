/*** BEGIN META {
  "name" : "Disable Maven Artifact Archiving",
  "comment" : "This script disables artifact archiving for maven projects, if you use an enterprise repository this rarely usefull.",
  "parameters" : [ 'dryRun' ],
  "core": "1.350",
  "authors" : [
    { name : "Mestachs" }, { name : "Dominik Bartholdi" }
  ]
} END META**/

// NOTES:
// dryRun: to list current configuration only

String format ='%-45s | %-20s | %-10s | %-10s | %-30s'
activeJobs = hudson.model.Hudson.instance.items.findAll
    {job -> job.isBuildable() && job instanceof hudson.maven.MavenModuleSet}
def oneline= { str ->   if (str==null)     return "";  str.replaceAll("[\n\r]", " - ")}
println String.format(format , "job", "scm trigger","last status"," logrot","archiving disabled?")
println "-------------------------------------------------------------------------------------------------------------------------------"
activeJobs.each{run -> 
    if (!run.isArchivingDisabled() && !"true".equals(dryRun)) {        
        run.setIsArchivingDisabled(true);
        run.save()
    }
    println String.format(format ,run.name,oneline(run.getTrigger(hudson.triggers.Trigger.class)?.spec), run?.lastBuild?.result, run.logRotator.getDaysToKeep()+" "+run.logRotator.getNumToKeepStr(), ""+run.isArchivingDisabled()) ;
}