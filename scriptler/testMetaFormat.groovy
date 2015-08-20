import net.sf.json.*

@Grapes([
    @Grab('org.kohsuke.stapler:json-lib:2.4-jenkins-1')
])

/**
 * This script is used to check if the format of the contributed scripts META information is correct.
 * 
 * Please execute it befor sending your pull request.
 * 
 * $> groovy testMetaFormat.groovy
 */

def json = [];

def scriptlerDir = new File(".")

scriptlerDir.eachFileMatch(~/.+\.groovy/) { File f ->
    if(f.name.equals('testMetaFormat.groovy')) {
        return
    }
    println "parsing $f"
    def m = (f.text =~ /(?ms)BEGIN META(.+?)END META/)
    if (m) {
        try {
            def metadata = JSONObject.fromObject(m[0][1]);
            assert metadata.name != null : "name version must be set for: ${f.name}"
            assert metadata.comment != null : "comment must be set for: ${f.name}"            
            metadata['script'] = f.name
            json << metadata
        } catch (Exception e) {
        	println "metadata for [${f.name}] not correct json" 
            e.printStackTrace(System.err);
        	throw e 
        }
    } else { 
      throw new RuntimeException("no metadata in [${f.name}] found")
	}
}

//lib.DataWriter.write("org.jenkinsci.plugins.scriptler.CentralScriptJsonCatalog",JSONObject.fromObject([list:json]));
