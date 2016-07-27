/*** BEGIN META {
  "name" : "Delete Logs from Build History",
  "comment" : "Deletes Log-Files for a Job from the Build History.",
  "parameters" : [ 'jobName', 'period' ],
  "core": "1.409",
  "authors" : [
    { name : "Steffen Legler" }
  ]
} END META**/


// NOTE: uncomment parameters below if not using Scriptler >= 2.0, or if you're just pasting
// the script in manually.

// The name of the job.
//def jobName = "some-job"

// The range of build numbers to delete.
//def buildRange = "1-5"

import jenkins.model.*;

def deleteLogHistory(job) { 
	long timestampPeriod = period.toLong() * 24l * 60l * 60l * 1000l
	long refDate = new Date().getTime() - timestampPeriod
	def j = jenkins.model.Jenkins.instance.getItem(job);   
  	long lastBuildTime = new File(j.getLastBuild().getRootDir().getAbsolutePath() + "/log").lastModified()
  	int lastBuildNumber = j.getLastBuild().getNumber()
  	if(j == null) {
      println "Job was not found. Script exits."
      return
  	}
	j.getBuilds().byTimestamp(0,refDate).each {
      	if (it.getNumber() == lastBuildNumber){
          	println "Files from last Build will not be deleted."          	
        }else{
  			File file = new File(it.getRootDir().getAbsolutePath() + "/log");  			
	  		if(file.exists()){
  				println "Delete " + file.getAbsolutePath() 
              	file.delete()
  			}
        }
	}
}



if(jobName != null && jobName.length() > 0){
	deleteLogHistory(jobName)
}else{
	jenkins.model.Jenkins.instance.getItems().each{
      	deleteLogHistory(it.getName())
    }
}


