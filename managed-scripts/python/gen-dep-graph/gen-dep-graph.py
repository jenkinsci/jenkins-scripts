#!/usr/bin/env python

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
# 
#  http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

import urllib
import sys
import os
import ConfigParser
import shlex

from subprocess import check_call

JENKINS_URL = ""
IGNORED = []

file_name = "project.properties"

if os.path.exists("personal.properties"):
    file_name = "personal.properties"

if os.path.exists(file_name):
    cp = ConfigParser.RawConfigParser()
    cp.read(file_name)
    
    JENKINS_URL = cp.get("jenkins", "url")
    try:
        IGNORED = cp.get("jenkins", "ignore")
        IGNORED = map((lambda foo: foo.strip()), IGNORED.split(","))
    except ConfigParser.NoOptionError:
        pass

print "Using url %s" % (JENKINS_URL)
print "Ignoring %d job(s)" % (len(IGNORED))

if len(JENKINS_URL) == 0:
    print "Please provide JENKINS_URL"
    sys.exit(1)

try:
    print "Opening url %s" % JENKINS_URL
    obj = eval(urllib.urlopen(JENKINS_URL).read());
    print "Done fetching list of all jobs."
except SyntaxError:
    print "Unable to parse the API result from %s" % JENKINS_URL
    print "Exiting..."
    sys.exit(1)


result = []
print "Analyzing %d job(s)" % (len(obj['jobs']))

for job in obj['jobs']:
    job['name'] = job['name'].replace("-", "_")
    if job['name'] in IGNORED:
        continue
    job_info = job['url']
    url = job['url'] + "api/python/"
    job_obj = eval(urllib.urlopen(url+"api/python").read())
    for proj in job_obj['downstreamProjects']:
        proj['name'] = proj['name'].replace("-", "_")
        result.append('"%s" -> "%s"' % (job['name'], proj['name']))

print "Writing output.dot for Graphviz"
f = open("output.dot", "w")
f.write("digraph G {\n")
for line in result:
    f.write(line)
    f.write("\n")
f.write("}")
f.close()

print "Generating output.svg with Graphviz"
path = os.path.join(os.path.abspath("."), "output.svg")
result = check_call(shlex.split("dot -Tsvg output.dot -o%s" % (path),posix=(os.name == 'posix')))
print "All done. For quick browser view follow the url:"
print "file://%s" % path
