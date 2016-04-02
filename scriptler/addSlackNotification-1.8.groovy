/*** BEGIN META {
  "name" : "Add Slack notification",
  "comment" : "This script will add slack notifications with slack plugin version 1.8 ",
  "parameters" : [],
  "authors" : [
    { name : "Zamir Ivry - zamir.ivry@gmail.com" }
  ]
} END META**/

def addSlack(item)
{
      println("adding slack to: "+ item.name);
    for (Iterator publishersItr = item.publishersList.iterator(); publishersItr.hasNext();) {
        def publisher = publishersItr.next();
        if(publisher instanceof jenkins.plugins.slack.SlackNotifier)
	      {
  				publishersItr.remove()
        	break;
    	  }
      }

    item.publishersList.add(new jenkins.plugins.slack.SlackNotifier(teamDomain, token, room,buildServerUrl,null) );

   
    
    ps = item.getAllProperties()
    def f;
    for(p in ps) {
	    if(p instanceof jenkins.plugins.slack.SlackNotifier.SlackJobProperty)
      {
        
      		f= p;
        break;
      }
     
    }
    if(f!=null)   
    	item.removeProperty(f)
    hudson.model.JobProperty pr  = new jenkins.plugins.slack.SlackNotifier.SlackJobProperty(null,null,null,false,false,true,false,false,true,true,false,true,true,false,null);
    item.addProperty(pr)
   

}
def getAllDependencies(item)
{
	println(item.name +" dependencies:" + item.getDownstreamProjects())
  addSlack(item)
  for(i in  item.getDownstreamProjects())
  {
    getAllDependencies(i)
  }
}


teamDomain = null //your slack team name
token = null // your slack token for jenkins
buildServerUrl = null //jenkins build server address
room = null //slack channel
 
for (item in Hudson.instance.items) {

  
  
  if(item.name.equals("common")){
    getAllDependencies(item)
  }
}
