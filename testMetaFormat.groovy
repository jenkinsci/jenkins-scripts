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

def dir = new File(".");

def scriptlerDir = new File(dir, "scriptler")

scriptlerDir.eachFileMatch(~/.+\.groovy/) { File f ->
    println "parsing $f"
    def m = (f.text =~ /(?ms)BEGIN META(.+?)END META/)
    if (m) {
        try {
            def metadata = JSONObject.fromObject(m[0][1]);
            metadata['script'] = f.name
            json << metadata
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}

//lib.DataWriter.write("org.jenkinsci.plugins.scriptler.CentralScriptJsonCatalog",JSONObject.fromObject([list:json]));
