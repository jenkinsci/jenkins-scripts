/*** BEGIN META {
  "name" : "Clear build queue",
  "comment" : "If you accidently trigger a lot of unneeded builds, it is useful to be able to <b>cancel</b> them all",
  "parameters" : [],
  "core": "1.300",
  "authors" : [
    { name : "Niels Harremoes" }
  ]
} END META**/
import hudson.model.*
def queue = Hudson.instance.queue
println "Queue contains ${queue.items.length} items"
queue.clear()
println "Queue cleared"
