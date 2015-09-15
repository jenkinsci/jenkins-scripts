/*** BEGIN META {
  "name" : "Search Job Configuration",
  "comment" : "Searches job names and configurations for a matching plain text for regexp pattern",
  "parameters" : ['pattern', 'details'],
  "core": "1.300",
  "authors" : [
    { name : "Sebastian Schuberth" }
  ]
} END META**/

def jobs = hudson.model.Hudson.instance.items

if (pattern.startsWith('/') && pattern.endsWith('/')) {
    println "Searching jobs for regexp ${pattern}..."
    pattern = pattern.substring(1, pattern.length() - 1)
    search = 'matches'
} else {
    println "Searching jobs for string '${pattern}'..."
    search = 'contains'
}

jobs.each { job ->
    if (job instanceof hudson.model.AbstractProject) {
        def match = job.configFile.file.find { it."$search"(pattern) } != null
        if (match || job.name."$search"(pattern)) {
            println "<a href=\"${job.absoluteUrl}configure\">${job.name}</a> matches"
            if (details.toBoolean()) {
                job.configFile.file.findAll { it."$search"(pattern) }.each { println '    ' + it.trim() }
            }
        }
    }
}

null
