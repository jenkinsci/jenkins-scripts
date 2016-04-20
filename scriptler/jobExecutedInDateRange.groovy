/*** BEGIN META {
 "name" : "List executed Jobs",
 "comment" : "List Jobs executed in a range of time",
 "core": "1.609",
 "authors" : [
 { name : "Kuisathaverat" }
 ]
 } END META**/

 jenkins = Jenkins.instance

 Calendar after = Calendar.getInstance()
 Calendar before = Calendar.getInstance()
 //set(int year, int month, int date, int hourOfDay, int minute,int second)
 after.set(2016,1,5,0,0,0)
 before.set(2016,1,23,0,0,0)
 println "Jobs executed between " + after.getTime() + " - " + before.getTime()

 for (job in jenkins.getAllItems(Job.class)) {
   for(Run run in job.getBuilds()){
         if (run.getTimestamp()?.before(before) && run.getTimestamp()?.after(after)) {
                 println "" + run.getResult() + " " + job.name + " " + job.getLastBuild().getTime()
         }
   }
 }
