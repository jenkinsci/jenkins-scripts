import hudson.model.AbstractProject
import hudson.plugins.git.GitSCM
import hudson.plugins.git.extensions.impl.*

Jenkins.instance.getAllItems(AbstractProject.class)
.findAll { job -> job.isBuildable()}
.findAll { job -> job.scm != null && job.scm instanceof GitSCM}
.each { project -> 
  scm = project.scm
  
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