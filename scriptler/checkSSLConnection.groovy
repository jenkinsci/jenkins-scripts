/*** BEGIN META {
 "name" : "Check SSL handshake with Server",
 "comment" : "Check Jenkins can reach out to a Server via SSL. The script also reports all trusted certificates",
 "parameters" : ["serverUrl"],
 "core": "1.609",
 "authors" : [
 { name : "Allan Burdajewicz" }
 ]
 } END META**/

import hudson.ProxyConfiguration
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import java.net.http.HttpResponse
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate

try {
    println("## DUMP JVM TRUST MANAGERS ##")
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    tmf.init((KeyStore) null)
    for (TrustManager trustManager : tmf.getTrustManagers()) {
        println(trustManager)

        if (trustManager instanceof X509TrustManager) {
            X509TrustManager x509TrustManager = (X509TrustManager) trustManager
            for (X509Certificate certificate: x509TrustManager.getAcceptedIssuers()) {
                println("\t" + certificate.getSubjectX500Principal())
            }
            println("\tAccepted issuers count : " + x509TrustManager.getAcceptedIssuers().length)
            println("###################")
        } else {
            println("Skip " + trustManager + " - " + trustManager.getClass())
        }
    }
} catch (Exception e) {
    e.printStackTrace()
    println "See stacktrace outputted in system.out for " + e
}
try {
    String url = "${serverUrl}"
    def response = ProxyConfiguration.newHttpClient().send(
            ProxyConfiguration.newHttpRequestBuilder(new URI(url)).build(),
            HttpResponse.BodyHandlers.discarding()
    )
    println("$url -> ${response.statusCode()}")
    for (Certificate certificate : response.sslSession().get().getPeerCertificates()) {
        if (certificate instanceof X509Certificate) {
            X509Certificate x509Certificate = (X509Certificate) certificate
            def subjectDN = x509Certificate.getSubjectX500Principal()
            println("\t" + subjectDN.getClass() + " - " + subjectDN)
        } else {
            println(certificate)
        }
    }
} catch (Exception e) {
    println "See stacktrace outputted in system.out for " + e
    e.printStackTrace()
}