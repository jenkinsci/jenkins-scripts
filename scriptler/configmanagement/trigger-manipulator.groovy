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
