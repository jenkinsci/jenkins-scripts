/*** BEGIN META {
  "name" : "reload job config",
  "comment" : "Reload the job config of a specific job.",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Thomas Froehlich - mail@thomas-froehlich.net" }
  ]
} END META**/


import java.io.InputStream;
import java.io.FileInputStream
import java.io.File;
import javax.xml.transform.stream.StreamSource

def hudson = hudson.model.Hudson.instance;
//def env = System.getenv();
//def toBeCopiedJobName = env['JOB_NAME_TO_REPORT_FOR'];
String toBeCopiedJobName = "copiedJob";

//to get a single job
//def job = hudson.model.Hudson.instance.getItem('my-job');

for(job in hudson.model.Hudson.instance.items) {   

    if (job.name == toBeCopiedJobName) {

        def configXMLFile = job.getConfigFile();
        def file = configXMLFile.getFile();

        InputStream is = new FileInputStream(file);

        job.updateByXml(new StreamSource(is));
        job.save();         
    }      
}
