/*
   Copyright 2016 Mate1 inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

import sbt.Keys._
import sbt._

object Build extends Build {

  // Global build settings
  override lazy val settings = super.settings ++ Seq(
    name         := "MetricsUtils",
    version      := "1.0.1-SNAPSHOT",
    organization := "com.mate1",
    scalaVersion := "2.10.4",
    parallelExecution in ThisBuild := false,
    publishArtifact in packageDoc := false,
    publishArtifact in packageSrc := false,
    sources in doc := Seq.empty,
    sourcesInBase := false,
    resolvers ++= Seq(Resolver.mavenLocal,
      "Mate1 Repository" at "https://raw.github.com/mate1/maven/master/public/"
    ),
    javacOptions ++= Seq("-g:none"),
    scalacOptions ++= Seq("-feature", "-g:none")
    ,credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
    ,publishTo := {
      val nexus = "http://maven.mate1:8081/nexus/content/repositories/"
      if (version.value.contains("SNAPSHOT"))
        Some("snapshots" at nexus + "snapshots")
      else
        Some("releases"  at nexus + "releases")
    }
  )

  // metrics-utils project
  lazy val metricsUtils = Project("metrics-utils", file("."),
    settings = super.settings ++ Seq(
      libraryDependencies ++= Seq(
        // General dependencies
        "io.dropwizard.metrics" %  "metrics-core"     % "4.0.0-SNAPSHOT",
        "io.dropwizard.metrics" %  "metrics-influxdb" % "4.0.0-SNAPSHOT"
      )
    )
  )
}
