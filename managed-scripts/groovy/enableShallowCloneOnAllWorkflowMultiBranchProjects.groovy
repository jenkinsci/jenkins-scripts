import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import jenkins.plugins.git.traits.CloneOptionTrait
import hudson.plugins.git.extensions.impl.CloneOption

Jenkins.instance.getAllItems(AbstractItem.class)
.findAll {it instanceof WorkflowMultiBranchProject}
.each { project ->
  sources = project.sources.find {it.source instanceof jenkins.plugins.git.GitSCMSource}
  traits = sources.source.traits
  cloneOptionTrait = traits.findAll {it instanceof CloneOptionTrait}
  
  if (!cloneOptionTrait) {
    traits.add(new CloneOptionTrait(new CloneOption(true, false, "", 10)))
    
  } else {
    cloneOption = cloneOptionTrait.extension.find {it instanceof CloneOption}
    traits.removeIf {it instanceof CloneOptionTrait}
    traits.add(new CloneOptionTrait(new CloneOption(true, cloneOption.noTags, cloneOption.reference, cloneOption.timeout)))
  }
  project.save()
}
  
null