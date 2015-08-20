/*** BEGIN META {
  "name" : "zip a folder",
  "comment" : "zip a folder using AntBuilder",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Thomas Froehlich - mail@thomas-froehlich.net" }
  ]
} END META**/

import hudson.model.*

// get current thread / Executor
def thr = Thread.currentThread()
// get current build
def build = thr?.executable
  
def resolver = build.buildVariableResolver
def destinationFile = resolver.resolve("DESTINATION_FILE")
def sourceFolder = resolver.resolve("SOURCE_FOLDER")

File f = new File(destinationFile);
if(f.exists() && !f.isDirectory()) {
  f.delete();
}

(new AntBuilder()).zip(destfile: destinationFile, basedir: sourceFolder)
