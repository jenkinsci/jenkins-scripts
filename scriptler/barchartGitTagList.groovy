/*** BEGIN META {
  "name" : "Barchart : Git Tag List",
  "comment" : "returns reverse sorted git tag list, assuming X.Y.Z version format in the tag; for use with https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+Dynamic+Parameter+Plug-in",
  "parameters" : [ "paramTag" ],
  "core": "1.450",
  "authors" : [
    { "name" : "Andrei Pozolotin", "email" : "Andrei.Pozolotin@gmail.com" }
  ]
} END META**/

import hudson.model.*

import java.util.regex.Pattern

/**
 * provided script parameter or default value
 * 
 * @return paramTag - tag filter pattern
 */
def paramTag() {
	try{
		// provided script parameter, if present
		paramTag 
	} catch( e ) {
		// "all tags" : http://www.kernel.org/pub/software/scm/git/docs/git-tag.html
		"*" 
	}
}

/**
 * @return current jenkins job 
 */ 
def jenkinsJob() {
	def threadName = Thread.currentThread().getName()
	def pattern = Pattern.compile("job/(.*)/build")
	def matcher = pattern.matcher(threadName); matcher.find()
	def jobName = matcher.group(1)
	def jenkinsJob = Hudson.instance.getJob(jobName)
}

/**
 * @return repository tag list using default git on os path 
 */ 
def tagList(dir, tag) {
	def command = [ "/bin/bash", "-c", "cd '${dir}' ; git fetch --tags &> /dev/null ; git tag -l '${tag}'" ]
	def process = command.execute(); process.waitFor()
	def result = process.in.text.tokenize("\n")
}

/**
 * version number model: prefix-X.Y.Z
 */ 
class Version {
	
	static def pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)")
	
	/** parse version from text */
	static def from(text){
		def matcher = pattern.matcher(text);
		if(matcher.find()){
			new Version( major:matcher.group(1), minor:matcher.group(2), patch:matcher.group(3) )
		} else{
			new Version( major:"0", minor:"0", patch:"0" )
		}
	}
	
	String prefix, major, minor, patch
	
	/** padded form for alpha sort */
	def String toString() { 
		String.format('%010d-%010d-%010d', major.toInteger(), minor.toInteger(), patch.toInteger())  
	}
	
}

/**
 * @return sorted tag list as script result 
 */ 
try {
  
	def tagList = tagList( jenkinsJob().workspace, paramTag() )
	 
	if (tagList) {
		tagList.sort{ tag -> Version.from(tag).toString() }.reverse()
	} else {
		[ 'master' ] // no tags in git repo
	}

} catch( e )  {

	[ e.toString() ]
  
}
