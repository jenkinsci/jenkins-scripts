/*** BEGIN META {
  "name" : "Hashify All SCM Triggers",
  "comment" : "Hashify all SCM triggers. See JENKINS-17311.",
  "parameters" : [ "dry" ],
  "core": "1.511",
  "authors" : [
    { "name" : "Wisen Tanasa" },
    { "name" : "Benjamin Francisoud" }
  ]
} END META**/
import hudson.scheduler.*
import hudson.model.*
import hudson.triggers.*
import hudson.scm.*

dry = dry.toBoolean()
println "Dry mode: $dry. \n"
  
TriggerDescriptor SCM_TRIGGER_DESCRIPTOR = Hudson.instance.getDescriptorOrDie(SCMTrigger.class)
assert SCM_TRIGGER_DESCRIPTOR != null;

items = Hudson.instance.items.findAll{job -> (!(job instanceof hudson.model.ExternalJob) && (job.scm instanceof SubversionSCM) && job.disabled == false) }

for(item in items) {
  def trigger = item.getTriggers().get(SCM_TRIGGER_DESCRIPTOR)
  if(trigger != null && trigger instanceof SCMTrigger) {
    def newSpec = CronTab.hashify(trigger.spec)
    if (newSpec) {
      def newTrigger = new SCMTrigger(newSpec)
      print "$item.name".padRight(80)
      print "Old spec: $trigger.spec".padRight(30)
      print "New spec: $newTrigger.spec".padRight(30)
        
      if (!dry) {
        newTrigger.job = item
        item.removeTrigger(SCM_TRIGGER_DESCRIPTOR)
        item.addTrigger(newTrigger)
        item.save()
      }
    } else {
      print "$item.name".padRight(80)
      print "Already hashified: $trigger.spec"
    }
    println()
  }
}
