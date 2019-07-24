import hudson.model.AbstractProject
import hudson.plugins.git.GitSCM
import hudson.plugins.git.extensions.impl.*

Jenkins.instance.getAllItems(AbstractProject.class)
.findAll { job -> job.isBuildable()}
.findAll { job -> job.scm != null && job.scm instanceof GitSCM}
.each { project -> 
  scm = project.scm
  
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