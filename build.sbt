name := "NuagesPompe"

version := "0.34"

organization := "de.sciss"

homepage := Some( url( "https://github.com/Sciss/NuagesPompe" ))

description := "Some common elements for building SoundProcesses and Wolkenpumpe based music applications"

licenses := Seq( "GPL v2+" -> url( "http://www.gnu.org/licenses/gpl-2.0.txt" ))

scalaVersion := "2.9.2"

resolvers += "Clojars Repository" at "http://clojars.org/repo"

libraryDependencies ++= Seq(
   "de.sciss" %% "wolkenpumpe" % "0.34",
   "de.sciss" %% "scalacolliderswing" % "0.34"
)

retrieveManaged := true

scalacOptions += "-deprecation"

// ---- publishing ----

publishMavenStyle := true

publishTo <<= version { (v: String) =>
   Some( if( v.endsWith( "-SNAPSHOT" ))
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
   else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
   )
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
<scm>
  <url>git@github.com:Sciss/NuagesPompe.git</url>
  <connection>scm:git:git@github.com:Sciss/NuagesPompe.git</connection>
</scm>
<developers>
   <developer>
      <id>sciss</id>
      <name>Hanns Holger Rutz</name>
      <url>http://www.sciss.de</url>
   </developer>
</developers>

// ---- ls.implicit.ly ----

seq( lsSettings :_* )

(LsKeys.tags in LsKeys.lsync) := Seq( "sound-synthesis", "gui", "sound", "music", "supercollider" )

(LsKeys.ghUser in LsKeys.lsync) := Some( "Sciss" )

(LsKeys.ghRepo in LsKeys.lsync) := Some( "NuagesPompe" )

// bug in ls -- doesn't find the licenses from global scope
(licenses in LsKeys.lsync) := Seq( "GPL v2+" -> url( "http://www.gnu.org/licenses/gpl-2.0.txt" ))
