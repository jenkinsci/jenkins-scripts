/*** BEGIN META {
      "name" : "List all builds from all jobs",
      "comment" : "List <b>all builds</b> from <b>all jobs</b> in your Jenkins server",
      "parameters" : [],
      "core": "1.300",
      "authors" : [
        { name : "Christian Haeussler - https://github.com/cniweb" }
      ]
    } END META**/
import hudson.model.*
def items = Hudson.instance.allItems

items.each { item ->

  if (item instanceof Job) {

    def builds = item.getBuilds()

    builds.each { build ->
      def since = groovy.time.TimeCategory.minus( new Date(), build.getTime() )
      def status = build.getBuildStatusSummary().message
      println "Build: ${build} | Since: ${since} | Status: ${status}" 
    }
  }
}
return
