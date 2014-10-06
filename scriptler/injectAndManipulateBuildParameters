/*** BEGIN META {
  "name" : "inject and manipulate build parameters",
  "comment" : "This allows you to manipulate the build parameters and also add new ones depending on existing ones.",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Thomas Froehlich - mail@thomas-froehlich.net" }
  ]
} END META**/

/* 
 * Parameters to inject
 */
def custom_parameters(Map all_parameters) {
  def new_parameters = new ArrayList<StringParameterValue>();
  
  //** INSERT YOUR CODE HERE ! **//
  
  for (e in all_parameters) {
    if(e.key.toString().equals("SPRINT_NUMBER")) {
      new_parameters.add(new StringParameterValue("DATE_PARAM", (new Date()).toString() + "_SPRINT_" + e.value.toString()));
    } else if (e.key.toString().equals("BROWSER")) {
      if(e.value.toString().equals("IE9")) {
          new_parameters.add(new StringParameterValue("IEWORKAROUND", "true"));
      } else {
          new_parameters.add(new StringParameterValue("IEWORKAROUND", "false"));
      }
    }                         
  }
  
  //** END **//
  return new_parameters;
}

/*
 * PLEASE DON'T CHANGE THE FOLLOWING CODE
 */
import hudson.model.*
import java.util.Map;

// init
def thr = Thread.currentThread()
def build = thr?.executable
def all_parameters = build.getBuildVariables();
def new_parameters = custom_parameters(all_parameters);

// Inject the new parameters into the existing list
def modified_parameters = null
def old_parameters = build.getAction(ParametersAction.class)
if (old_parameters != null) {
  build.actions.remove(old_parameters)
  modified_parameters = old_parameters.createUpdated(new_parameters)
} else {
  modified_parameters = new ParametersAction(new_parameters)
}

// Reattach the parameters with the additions
build.actions.add(modified_parameters)

