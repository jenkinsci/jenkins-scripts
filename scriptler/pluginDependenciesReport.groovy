/*** BEGIN META {
 "name" : "Plugin Dependencies Report",
 "comment" : "Get information about direct and recursive (JSON object) dependencies of/to a specific plugin.",
 "parameters" : [ "pluginShortName" ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import hudson.PluginWrapper
import jenkins.model.Jenkins

/***********************************************************************************************
 * Following methods analyze dependencies of a particular plugin, for example the `git-plugin`.*
 ***********************************************************************************************/

def pluginByName = Jenkins.instance.getPluginManager().getPlugin(pluginShortName);

/**
 * Get the dependencies (direct) of a particular plugin.
 */
println "\nDEPENDENCIES (DIRECT) of ${pluginByName.getShortName()} (${pluginByName.getVersion()}):"
/**
 * Get the direct dependencies of a particular plugin.
 */
pluginByName.getDependencies().each {
    println "${it.shortName} (${it.version})"
};

println "\nDEPENDENCIES (Recursive JSON) of ${pluginByName.getShortName()} (${pluginByName.getVersion()}):"
/**
 * Get a complete JSON object of the dependencies (recursively) of a particular plugin.
 * @param plugin The Plugin
 */
def void getDependenciesJSON(PluginWrapper plugin) {
    print "{\"plugin\":\"${plugin.getShortName()}\", \"version\":\"${plugin.getVersion()}\"";
    def deps = plugin.getDependencies();
    if (!deps.isEmpty()) {
        def i;
        print ", \"dependencies\":["
        for (i = 0; i < deps.size() - 1; i++) {
            getDependenciesJSON(Jenkins.instance.getPluginManager().getPlugin(deps.get(i).shortName));
            print ","
        }
        getDependenciesJSON(Jenkins.instance.getPluginManager().getPlugin(deps.get(i).shortName));
        print "]"
    }
    print "}"
}
getDependenciesJSON(pluginByName);

println "\n\nDEPENDANTS (DIRECT) of ${pluginByName.getShortName()} (${pluginByName.getVersion()}):"
/**
 * Get the plugins that depend (directly) on a particular plugin.
 */
Jenkins.instance.getPluginManager().getPlugins()
        .findAll { plugin ->
    plugin.getDependencies().find {
        dependency -> pluginShortName.equals(dependency.shortName)
    }
}.each {
    println "${it.getShortName()} (${it.getVersion()})"
};

println "\nDEPENDANTS (Recursive JSON) of ${pluginByName.getShortName()} (${pluginByName.getVersion()}):"
/**
 * Get a complete JSON object of the dependants (recursively) of a particular plugin.
 * @param plugin The Plugin
 */
def void getDependantsJSON(PluginWrapper plugin) {
    print "{\"plugin\":\"${plugin.getShortName()}\", \"version\":\"${plugin.getVersion()}\"";
    def deps = Jenkins.instance.getPluginManager().getPlugins().findAll { depCandidate ->
        depCandidate.getDependencies().find {
            dependency -> plugin.shortName.equals(dependency.shortName)
        }
    }
    if (!deps.isEmpty()) {
        def i;
        print ", \"dependants\":["
        for (i = 0; i < deps.size() - 1; i++) {
            getDependantsJSON(Jenkins.instance.getPluginManager().getPlugin(deps.get(i).shortName));
            print ","
        }
        getDependantsJSON(Jenkins.instance.getPluginManager().getPlugin(deps.get(i).shortName));
        print "]"
    }
    print "}"
}
println(getDependantsJSON(pluginByName));
