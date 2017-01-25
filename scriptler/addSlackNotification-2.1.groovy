/*** BEGIN META {
  "name" : "Add Slack notification",
  "comment" : "This script will add slack notifications with slack plugin version 2.1",
  "parameters" : [],
  "authors" : [
    { name : "Arnaud Héritier - aheritier@apache.org" }    
  ]
} END META**/

import jenkins.plugins.slack.*;

String teamDomain = null;
String authToken = null;
String authTokenCredentialId = null;
String room = null;
String sendAs = null;
boolean startNotification = false;
boolean notifySuccess = false;
boolean notifyAborted = false;
boolean notifyNotBuilt = false;
boolean notifyUnstable = false;
boolean notifyFailure = false;
boolean notifyBackToNormal = false;
boolean notifyRepeatedFailure = false;
boolean includeTestSummary = false;
CommitInfoChoice commitInfoChoice = CommitInfoChoice.NONE;
boolean includeCustomMessage = false;
String customMessage = null;

jenkins.model.Jenkins.instance.getAllItems(hudson.model.AbstractProject).each {
job -> 
  println("Add or update the Slack Notifier in ${job.fullName}")
  job.publishersList.replace(
    new SlackNotifier(
      teamDomain,
      authToken,
      room,
      authTokenCredentialId, 
      sendAs, 
      startNotification, 
      notifyAborted, 
      notifyFailure, 
      notifyNotBuilt, 
      notifySuccess, 
      notifyUnstable, 
      notifyBackToNormal, 
      notifyRepeatedFailure, 
      includeTestSummary, 
      commitInfoChoice, 
      includeCustomMessage, 
      customMessage));
  println(">>> ${job.fullName} Updated")
}