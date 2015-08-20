/*** BEGIN META {
      "name" : "Purge M2 repo",
      "comment" : "remove specified packages from m2 repo. Pass parameters to narrow down what's deleted: 'com.example.package1 com.example.package2'",
      "parameters" : ["packages"],
      "core": "1.300",
      "authors" : [
        { name : "Emily Bache" }
      ]
    } END META**/

def env = System.getenv()
def home = env["HOME"]
def m2_home = "${home}/.m2"
if (env["M2_HOME"]) {
  m2_home = env["M2_HOME"]
}

List<String> packageList = [""]

if (packages) {
    packageList = packages.split().collect(new ArrayList()) {p -> p.replace(".", "/")}
}


def mvnDirsToDelete = packageList.collect(new ArrayList()) { p -> new File("${m2_home}/repository/${p}")}

mvnDirsToDelete.each { dir -> dir.deleteDir() }
mvnDirsToDelete.each { dir -> println("deleted dir: ${dir} ${!dir.exists()}")}

true