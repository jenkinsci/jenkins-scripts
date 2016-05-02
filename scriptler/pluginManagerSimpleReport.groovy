/*** BEGIN META {
 "name" : "Plugins Manager Simple Report",
 "comment" : "Simple report of Installed/Disabled/Bundled/Failed/Forced by Pinning/Inactive plugins of a Jenkins Instance",
 "parameters" : [],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import jenkins.model.Jenkins

println "\nINSTALLED:\n---"
/**
 * Get the list of installed plugins.
 */
Jenkins.instance.getPluginManager().getPlugins().each {
    println "${it.getShortName()} (${it.getVersion()})"
}

println "\nFAILED:\n---"
/**
 * Get failed plugins: getFailedPlugins()
 */
Jenkins.instance.getPluginManager().getFailedPlugins()
        .each {
    println "${it.getShortName()} (${it.getVersion()})}"
}

println "\nPINNED:\n---"
/**
 * Get pinned plugins: isPinned()
 */
Jenkins.instance.getPluginManager().getPlugins()
        .findAll { plugin -> plugin.isPinned() }
        .each {
    println "${it.getShortName()} (${it.getVersion()})}"
}

println "\nBUNDLED:\n---"
/**
 * Get bundled plugins: isBundled()
 */
Jenkins.instance.getPluginManager().getPlugins()
        .findAll { plugin -> plugin.isBundled() }
        .each {
    println "${it.getShortName()} (${it.getVersion()})}"
};

println "\nFORCED BY PINNING:\n---"
/**
 * Get plugins forced to an older version because of Pinning
 */
Jenkins.instance.getPluginManager().getPlugins()
        .findAll { plugin -> plugin.isPinningForcingOldVersion() }
        .each {
    println "${it.getShortName()} (${it.getVersion()})}"
}

println "\nDISABLED:\n---"
/**
 * Get Disabled plugins.
 */
Jenkins.instance.getPluginManager().getPlugins()
        .findAll { plugin -> !plugin.isEnabled() }
        .each {
    println "${it.getShortName()} (${it.getVersion()})}"
}

println "\nINACTIVE:\n---"
/**
 * Get Inactive plugins.
 */
Jenkins.instance.getPluginManager().getPlugins()
        .findAll { plugin -> !plugin.isActive() }
        .each {
    println "${it.getShortName()} (${it.getVersion()})}"
}
return;