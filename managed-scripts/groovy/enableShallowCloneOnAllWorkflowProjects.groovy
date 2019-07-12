import org.jenkinsci.plugins.workflow.job.WorkflowJob
import hudson.plugins.git.GitSCM
import hudson.plugins.git.extensions.impl.*

Jenkins.instance.getAllItems(WorkflowJob.class)
.each { project ->   
  scm = project.definition.scm 
  
  scm.extensions
  .findAll {it instanceof CloneOption}
  .findAll {! it.shallow}
  .each {
    boolean shallowClone = true
    CloneOption cloneOption = new CloneOption(shallowClone, it.noTags, it.reference, it.timeout)
    scm.extensions.remove(it)
    scm.extensions.add(cloneOption) 
  }
  
  project.save()
}
null