import org.jenkinsci.plugins.workflow.job.WorkflowJob
import hudson.plugins.git.GitSCM
import hudson.plugins.git.extensions.impl.*

Jenkins.instance.getAllItems(WorkflowJob.class)
.each { project ->   
  scm = project.definition.scm 
  
  cloneOption = scm.extensions.find {it instanceof CloneOption}
  if (!cloneOption) {
      scm.extensions.add(new CloneOption(true, false, "", 10))
  } else {
      scm.extensions.remove(cloneOption)
      scm.extensions.add(new CloneOption(true, cloneOption.noTags, cloneOption.reference, cloneOption.timeout))
  }
  
  project.save()
}
null