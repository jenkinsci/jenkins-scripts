/*** BEGIN META {
      "name" : "Purge Dockers",
      "comment" : "stop and remove all docker containers, remove all dangling and SNAPSHOT images.",
      "parameters" : [],
      "core": "1.300",
      "authors" : [
        { name : "Emily Bache" }
      ]
    } END META**/


def env = System.getenv()

if (env["DOCKER_HOST"]) {
	println("This script will not work without a properly set up docker environment. Please define the environment variable 'DOCKER_HOST'")
	false
} else {
	runningDockers = "docker ps -q".execute().text.split(/\n/)
	runningDockers.each {id -> println("docker stop ${id}".execute().text)}

	stoppedDockers = "docker ps -a -f status=exited -q".execute().text.split(/\n/)
	stoppedDockers.each {id -> println("docker rm ${id}".execute().text)}

	dockerImages = "docker images".execute().text.split(/\n/)
	snapshotImages = dockerImages.findAll{ it.contains("SNAPSHOT") || it.contains("<none>") }
	snapshotImages.each {output -> println("docker rmi ${output.split()[2]}".execute().text)}

	true
}
