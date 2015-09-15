/*** BEGIN META {
  "name" : "Search Job Configuration",
  "comment" : "Searches job names and configurations for a matching plain text for regexp pattern",
  "parameters" : ['pattern', 'details', 'disabled'],
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

def count = 0

jobs.each { job ->
    if (job instanceof hudson.model.AbstractProject && (disabled.toBoolean() || !job.disabled)) {
        def match = job.configFile.file.find { it."$search"(pattern) } != null
        if (match || job.name."$search"(pattern)) {
            println "<a href=\"${job.absoluteUrl}configure\">${job.name}</a> matches"
            ++count

            if (details.toBoolean()) {
                job.configFile.file.findAll { it."$search"(pattern) }.each { println '    ' + it.trim() }
            }
        }
    }
}

println "<strong>${count} match(es) in total</strong>"

null
