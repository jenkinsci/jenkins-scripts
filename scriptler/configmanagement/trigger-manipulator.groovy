/*** BEGIN META {
  "name" : "Trigger Manipulator",
  "comment" : "This script came about because there were many jobs that were both scheduled to run @midnight AND were polling the scm truth server.  Doing one or the other makes sense, but having polling AND @midnight doesn't.  This script will iterate over all the projects and pull out the timer based trigger as well as set the minute interval for how often it should poll.  Initially, I used this to move from a really slopply written polling interval string to */5 (run every 5 minutes).",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "EJ Ciramella" }
  ]
} END META**/
import hudson.model.*
import hudson.triggers.*

TriggerDescriptor SCM_TRIGGER_DESCRIPTOR = Hudson.instance.getDescriptorOrDie(SCMTrigger.class)
TriggerDescriptor TIMER_TRIGGER_DESCRIPTOR = Hudson.instance.getDescriptorOrDie(TimerTrigger.class)
  
assert SCM_TRIGGER_DESCRIPTOR != null;
assert TIMER_TRIGGER_DESCRIPTOR != null;

for(item in Hudson.instance.items)
{
    println("Looking at "+  item.name);
		
	def trigger = item.getTriggers().get(SCM_TRIGGER_DESCRIPTOR)
	def timertrigger = item.getTriggers().get(TIMER_TRIGGER_DESCRIPTOR)
	String triggertoreplace = "some string here";
	String newtriggervalue = "new trigger value";
	
  	if(timertrigger != null && trigger != null)
	{
          println(item.name + " has a both triggers DUH!");
          item.removeTrigger(TIMER_TRIGGER_DESCRIPTOR)
	}
	
    if(trigger != null && trigger instanceof SCMTrigger)
	{
      	println("> $trigger.spec");
  	    String[] triggerbits = trigger.spec.split(" ");
        if(triggerbits[0] == triggertoreplace )
  		{
			triggerbits[0] = newtriggervalue;
  		}

        println("about to build up the new string builder trigger spec");
        StringBuilder newtriggerbits = new StringBuilder();
  		for(bits in triggerbits)
  		{
  			newtriggerbits.append(bits+" ");
  		}
  
  		println(" here is the new schedule " + newtriggerbits.toString());
  
  		def newTrigger = new SCMTrigger(newtriggerbits.toString())

  		item.removeTrigger(SCM_TRIGGER_DESCRIPTOR)
        item.addTrigger(newTrigger)
  }
  else
  {
    println("no modifications needed");
  }
}
