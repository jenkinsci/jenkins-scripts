/*** BEGIN META {
  "name" : "Show labels overview",
  "comment" : "Show an overview of all labels defined and which slaves have which labels",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Stefan Heintz" },
    { name : "Nico Mommaerts" },
    { name : "Rob Fagen" }
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

maxLen=0
uniqueLabels.each() {
  if (it.length() > maxLen) {
    maxLen=it.length()
  } 
}

def vertLabels = []

for (int idx=0;idx<maxLen;idx++) {
  def vertLabel="|"
  uniqueLabels.each() {
    if (idx < it.length()) { 
      vertLabel+="${it[idx]}|" 
    } else {
      vertLabel+=" |"
    }
  }
  vertLabels.add(vertLabel)
}
    

def FIXED_FIRST_COLUMN = 40
 
vertLabels.each() { 
  printSign(FIXED_FIRST_COLUMN-1, " ")
  print "${it}\n" 
}
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
