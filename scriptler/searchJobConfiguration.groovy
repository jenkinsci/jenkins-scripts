/*** BEGIN META {
  "name" : "Search Job Configuration",
  "comment" : "Searches job names and configurations for a matching plain text for regexp pattern",
  "parameters" : ['pattern', 'details', 'disabled'],
  "core": "1.300",
  "authors" : [
    { name : "Sebastian Schuberth" }
  ]
} END META**/

count = 0

if (pattern.startsWith('/') && pattern.endsWith('/')) {
  println "Searching jobs for regexp ${pattern}..."
  pattern = pattern.substring(1, pattern.length() - 1)
  search = 'matches'
} else {
  println "Searching jobs for string '${pattern}'..."
  search = 'contains'
}

def isFolder(item) {
  item instanceof com.cloudbees.hudson.plugins.folder.Folder
}

def isJob(item) {
  item instanceof hudson.model.FreeStyleProject || item instanceof hudson.matrix.MatrixProject || item instanceof com.tikal.jenkins.plugins.multijob.MultiJobProject
}

def processItem(item) {
  if (isFolder(item)) {
    item.items.each { processItem(it) }
  } else if (isJob(item)) {
    if (disabled.toBoolean() || !item.disabled) {
      def match = item.configFile.file.find { it."$search"(pattern) } != null
      if (match || item.name."$search"(pattern)) {
        println "<a href=\"${item.absoluteUrl}configure\">${item.name}</a> matches"
        ++count

        if (details.toBoolean()) {
          item.configFile.file.findAll { it."$search"(pattern) }.each { println '    ' + it.trim() }
        }
      }
    }
  } else {
    println "NOTE: Skipping item '${item.name}' of '${item.getClass()}'."
  }
}

hudson.model.Hudson.instance.items.each { processItem(it) }

println "<strong>${count} match(es) in total</strong>"

null
