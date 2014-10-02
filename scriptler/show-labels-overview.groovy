/*** BEGIN META {
  "name" : "Show labels overview",
  "comment" : "Show an overview of all labels defined and which slaves have which labels",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Stefan Heintz" },
    { name : "Nico Mommaerts" }
  ]
} END META**/
import jenkins.model.Jenkins;
 
def uniqueLabels = []
def slave_label_map = [:]
for (slave in Jenkins.instance.slaves) {
 words = slave.labelString.split()
 def labelListForSlave = []
 words.each() {
          labelListForSlave.add(it);
          uniqueLabels.add(it)
 }
 slave_label_map.put(slave.name, labelListForSlave)
}
uniqueLabels.unique()
   
def FIXED_FIRST_COLUMN = 40
 
printSign(FIXED_FIRST_COLUMN-1, " ")
print "|"
uniqueLabels.each() { print "${it}|" }
printLine()
 
for ( entry in slave_label_map ) {
 print "${entry.key}"
 printSign(FIXED_FIRST_COLUMN - entry.key.size()-1, " ")
 print "|"
 uniqueLabels.each() { lab ->
  boolean found = false
  entry.value.each() { valueList ->
   if(lab.equals(valueList)) {
    found = true
   }
  }
  if(found) {
   print "X"
  } else {
   print " "
  }
  printSign(lab.size()-1, " ")
  print "|"
 }
 printLine()
}
 
 
def printSign(int count, String sign) {
 for (int i = 0; i < count; i++) {
  print sign
 }
}
 
def printLine() {
 print "\n";
 printSign(120, "-")
 print "\n";
}