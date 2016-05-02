/*** BEGIN META {
 "name" : "Get all Polling Repos",
 "comment" : "Print all the Git branches and repositories of jobs that have polling configured",
 "parameters" : [ ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import hudson.model.AbstractProject
import hudson.plugins.git.GitSCM
import hudson.triggers.SCMTrigger
import jenkins.model.Jenkins

def activeJobs = Jenkins.instance.getAllItems(AbstractProject.class)
        .findAll { job -> job.isBuildable()}
        .findAll { job -> job.getTrigger(SCMTrigger)}
        .findAll { job -> job.scm != null && job.scm instanceof GitSCM}
        .collect();

for (project in activeJobs) {
    scm = project.scm;
    println("${project.name}: " +
            "repositories: ${scm.repositories.collectMany{ it.getURIs() }}" +
            ", branches: ${scm.branches.collect{ it.name }}" +
            ", cronTab: ${project.getTrigger(SCMTrigger.class).getSpec()}")
}