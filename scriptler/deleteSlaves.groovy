/**
Delete slave matching specific string pattern. 
This script will delete all slaves with "docker" string

**/
for (aSlave in hudson.model.Hudson.instance.slaves) {
  if (aSlave.name.contains("Docker")) {
    println('\t SLAVE NAME: ' + aSlave.name);
    aSlave.getComputer().doDoDelete();
  }
}
