## NuagesPompe

### statement

A bit of glue on top of [Wolkenpumpe](http://github.com/Sciss/Wolkenpumpe/) so that set-up of standalone applications is simple. NuagesPompe is (C)opyright 2008-2011 by Hanns Holger Rutz. All rights reserved. It is released under the [GNU General Public License](http://github.com/Sciss/NuagesPompe/blob/master/licenses/NuagesPompe-License.txt) and comes with absolutely no warranties. To contact the author, send an email to `contact at sciss.de`.

### requirements

Builds with xsbt (sbt 0.11) against Scala 2.9.1. Depends on [Wolkenpumpe](http://github.com/Sciss/Wolkenpumpe) and [ScalaCollider-Swing](http://github.com/Sciss/ScalaColliderSwing). Standard sbt targets are `clean`, `update`, `compile`, `package`, `doc`, `publish-local`.

### creating an IntelliJ IDEA project

If you haven't globally installed the sbt-idea plugin yet, create the following contents in `~/.sbt/plugins/build.sbt`:

    resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
    
    addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.0")

Then to create the IDEA project, run the following two commands from the xsbt shell:

    > set ideaProjectName := "NuagesPompe"
    > gen-idea


