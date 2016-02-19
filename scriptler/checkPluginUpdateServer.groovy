/*** BEGIN META {
  "name" : "Check Update Server",
  "comment" : "check Plugin Update Server - workaround for JENKINS-27694",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Radek Antoniuk" }
  ]
} END META**/

import jenkins.model.*

Jenkins j = Jenkins.getInstance()
j.pluginManager.doCheckUpdatesServer()
