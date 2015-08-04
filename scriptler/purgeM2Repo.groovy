/*** BEGIN META {
      "name" : "Purge M2 repo",
      "comment" : "remove specified folders from m2 repo.",
      "parameters" : [],
      "core": "1.300",
      "authors" : [
        { name : "Emily Bache" }
      ]
    } END META**/

List<File> mvnDirsToDelete = [new File("/home/jenkins/.m2/repository/com/pagero/"),
								new File("/home/jenkins/.m2/repository/se/diamo/"),
								new File("/home/jenkins/.m2/repository/se/pagero/")
 								]

mvnDirsToDelete.each { dir -> dir.deleteDir() }
mvnDirsToDelete.each { dir -> println("deleted dir: ${dir} ${!dir.exists()}")}
