/*** BEGIN META {
 "name" : "Add a pre-build shell script step",
 "comment" : "Add a pre-build shell script step to all Maven and Frestyle Projects",
 "core": "1.609",
 "authors" : [
 { name : "Kuisathaverat" }
 ]
 } END META**/

import hudson.util.*
import hudson.tasks.*
import hudson.maven.*


Jenkins.instance.getAllItems(Job.class)
	.findAll{ it instanceof FreeStyleProject || it instanceof MavenModuleSet  }
	.each{
      println it.name + " - " + it.class

      DescribableList<Builder,Descriptor<Builder>> builders = new DescribableList<Builder,Descriptor<Builder>>()
      builders.add(new hudson.tasks.Shell("echo 'Hello world'"))

      if(it instanceof FreeStyleProject){
        builders.addAll(it.getBuildersList())
        it.getBuildersList().clear()
        it.getBuildersList()addAll(builders)
      } else if (it instanceof MavenModuleSet ){
        builders.addAll(it.getPrebuilders())
        it.getPrebuilders().clear()
      	it.getPrebuilders().addAll(builders)
      }

      it.save()
    }
