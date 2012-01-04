# Scriptler scripts

Groovy scripts in this directory are made available via the
[Scriptler](https://wiki.jenkins-ci.org/display/JENKINS/Scriptler+Plugin)
catalog to be published at jenkins-ci.org.

In order for a script to be included in the catalog, metadata
describing it must be included in its file, like the below:

    /*** BEGIN META {
      "name" : "Clear build queue",
      "comment" : "If you accidently trigger a lot of unneeded builds, it is useful to be able to <b>cancel</b> them all",
      "parameters" : [],
      "core": "1.300",
      "authors" : [
        { name : "Niels Harremoes" }
      ]
    } END META**/
