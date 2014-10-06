/*** BEGIN META {
  "name" : "sync two jenkins servers",
  "comment" : "This allows you to download a remote workspace folder and extract it to your present Jenkins.",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Thomas Froehlich - mail@thomas-froehlich.net" }
  ]
} END META**/

import hudson.model.*
  
import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/*********************************************************************
 * Downloader
 */

public static String getMethod(String urlString, String username, String password, String targetFolder) throws ClientProtocolException, IOException {
        URI uri = URI.create(urlString);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(username, password));
        
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        HttpGet httpGet = new HttpGet(uri);
        
        // Add AuthCache to the execution context
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        HttpResponse response = httpClient.execute(host, httpGet, localContext);
        HttpEntity entity = response.getEntity();		
        
        File returnedFile = null;
        if(entity != null) {
                InputStream instream = entity.getContent();
                
                returnedFile = saveTmpFile(instream, new File(targetFolder));
        }
        
        if(returnedFile != null)
                return returnedFile.getAbsolutePath();
        else
                return "Failure.";
        
        //return EntityUtils.toString(response.getEntity());
}

public static File saveTmpFile(InputStream instream, File targetDir) throws IOException {
      if (!targetDir.exists()) {
          targetDir.mkdirs();
      }
      
      // make sure we get the actual file
      File zip = File.createTempFile("remote_workspace", ".zip", targetDir);
      OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
      copyInputStream(instream, out);
      out.close();

      return zip;
}

public static void copyInputStream(InputStream instream, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int len = instream.read(buffer);
    while (len >= 0) {
        out.write(buffer, 0, len);
        len = instream.read(buffer);
    }
    instream.close();
    out.close();
}
/**************************************************************************************************************
 * Extractor and Timestamp updater
 */
public void extractFolder(String zipFile, String targetFolder)
{
    System.out.println(zipFile);
    int BUFFER = 2048;
    File file = new File(zipFile);

    ZipFile zip = new ZipFile(file);

    new File(targetFolder).mkdir();
    Enumeration zipFileEntries = zip.entries();

    // Process each entry
    while (zipFileEntries.hasMoreElements())
    {
        // grab a zip file entry
        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
        String currentEntry = entry.getName();
        File destFile = new File(targetFolder, currentEntry);
        //destFile = new File(targetFolder, destFile.getName());
        File destinationParent = destFile.getParentFile();

        // create the parent directory structure if needed
        destinationParent.mkdirs();

        if (!entry.isDirectory())
        {
            BufferedInputStream is = new BufferedInputStream(zip
            .getInputStream(entry));
            int currentByte;
            // establish buffer for writing file
            byte[] data = new byte[BUFFER];

            // write the current file to disk
            FileOutputStream fos = new FileOutputStream(destFile);
            BufferedOutputStream dest = new BufferedOutputStream(fos,
            BUFFER);

            // read and write until last byte is encountered
            while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, currentByte);
            }
            dest.flush();
            dest.close();
            is.close();
        }

        if (currentEntry.endsWith(".zip"))
        {
            // found a zip file, try to open
            extractFolder(destFile.getAbsolutePath());
        }
    }
}

/**************************************************************************************************************
 * Script
 */

// get current thread / Executor
def thr = Thread.currentThread()
// get current build
def build = thr?.executable

def resolver = build.buildVariableResolver
def user_name = resolver.resolve("REMOTE_JENKINS_USER_NAME")
def user_pw = resolver.resolve("REMOTE_JENKINS_USER_PW")
def server = resolver.resolve("REMOTE_JENKINS_SERVER")
def job = resolver.resolve("REMOTE_JENKINS_JOB")
def target_folder = build.getWorkspace().toString()

println "Using workspace folder " + target_folder;
String file = getMethod(server + "/job/" + job + "/ws/*zip*/workspace.zip", user_name, user_pw, target_folder + "\\archives\\");
println "Extracting " + file + " to " + target_folder;
extractFolder(file, target_folder + "\\..\\");
