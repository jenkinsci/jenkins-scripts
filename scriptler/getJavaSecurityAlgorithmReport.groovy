/*** BEGIN META {
 "name" : "Get java security algorithms",
 "comment" : "Report disabled and supported java security algorithms",
 "parameters" : [ ],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import java.security.Provider
import java.security.Security

//Check disabled algorithms
println "Disabled Algorithms:\n  jdk.tls.disabledAlgorithms=${Security.getProperty("jdk.tls.disabledAlgorithms")}"

//Check all supported algorithms
println "\nSupported Algorithms"
for (Provider provider : Security.getProviders()) {
    println("  Provider: " + provider.getName())
    for (Provider.Service service : provider.getServices()) {
        println("    Type: ${service.getType()} - Algorithm: ${service.getAlgorithm()}")
    }
}