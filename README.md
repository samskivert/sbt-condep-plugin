sbt-condep-plugin
=================

This plugin adds the ability to have conditional direct dependencies on other
projects. By default, conditional dependencies will reference an Ivy (or Maven)
artifact, but if you create an appropriately named symlink in your project
directory to the depended-upon project, a direct (SBT) dependency will be
established on that project. This allows automatic rebuilding of depended-upon
projects when running targets in the depending project, and provides a magical
"things just get recompiled when they need to" experience, like one gets with
IDEs.

Usage
-----

Add the plugin to `project/plugins/build.sbt`:

    libraryDependencies += "com.samskivert" %% "sbt-condep-plugin" % "1.0-SNAPSHOT"

In your `project/Build.scala` file, declare and use your conditional
dependencies like so:

    object MyProjBuild extends Build {
      val condeps = com.samskivert.condep.Depends(
        ("foolib",   null,     "foo.bar" % "foolib" % "1.0-SNAPSHOT"),
        ("bazproj", "subbaz",  "foo.baz" % "bazproj-subbaz" % "1.0-SNAPSHOT"),
        ("bazproj", "testbaz", "foo.baz" % "bazproj-testbaz" % "1.0-SNAPSHOT" % "test")
      )

      lazy val myproj = condeps.addDeps(Project(
        "myproj", file("."), settings = Defaults.defaultSettings ++ Seq(
          name := "myproj",
          // ...
          libraryDependencies ++= condeps.libDeps ++ Seq(
            // non-conditional depends
            "org.specs2" %% "specs2" % "1.6.1"
          )
        )
      ))
    }

By default, your project will use the Ivy/Maven artifacts as dependencies, but
if you create `foolib` and `bazproj` symlinks in your top-level project
directory, it will use direct SBT dependencies.

You can confirm that the direct dependencies are working by entering the
following into the SBT console:

    show myproj/project-dependencies

It will output something like the following:

    [info] List(foo.bar:foolib:1.0-SNAPSHOT, foo.baz:bazproj-subbaz:1.0-SNAPSHOT)

Note that the versions in that list will be the versions defined in the SBT
build for those projects, which could differ from the versions you specify for
your Ivy/Maven artifacts.

Issues
------

Presently, if you use this plugin to depend on a project which then uses this
plugin to depend on some other project, you have to add the transitive union of
all condep symlinks to the top-level project, or SBT will become confused.

For example, if project A has a condep on project B which has a condep on
project C, then project B will have a symlink to project C, and project A must
have a symlink to both projects B and C.

At some point I'll pester mharrah into either showing me the required magic to
make things work without the symlink from A to C, or enhancing SBT so that it's
possible (as I ran into some apparent limitations when trying to make this
work).

License
-------

This code is released under the New BSD License. See [LICENSE] for details.

[LICENSE]: https://github.com/samskivert/sbt-condep-plugin/blob/master/LICENSE
