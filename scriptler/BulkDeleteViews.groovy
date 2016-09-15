/*** BEGIN META {"name" : "Bulk delete of views",
 "comment" : "It will delete all views provided and optionally the jobs inside them (dry run mode available)",
 "parameters" : [ 'dry', 'views', 'deleteJobs' ],
 "core": "2.0",
 "authors" : [{ name : "Luis del Toro" }]} END META**/


import jenkins.model.*

dry = dry.toBoolean()
println "Dry Run mode: $dry"
views = views.split(',')
println "Views To Delete: $views"
deleteJobs = deleteJobs.toBoolean()
println "Delete Jobs Mode: $deleteJobs"

def jenkins = Jenkins.instance

def deletedViews = 0
def deletedJobs = 0

views.each {

    def view = jenkins.getView(it.trim())
    println "Candidate for deletion found: view '${view.name}'"
    if (deleteJobs) {
        view.items.each {
            println "Candidate for deletion found: job '${it.name}' in view '${view.name}'"
            if (!dry) {
                it.delete()
                deletedJobs++
                println "Job '${it.name}' deleted"
            }
        }
    }
    if (!dry) {
        view.owner.deleteView(view)
        deletedViews++
        println "View '${view.name}' deleted"
    }
}

println "Deleted ${deletedViews} views and ${deletedJobs} jobs"
