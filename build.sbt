import PlayCrossCompilation._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import uk.gov.hmrc.SbtArtifactory

enablePlugins(SbtGitVersioning, ScalaJSPlugin, UniversalPlugin, SbtArtifactory)

name := "paye-estimator"

LocalTempBuildSettings.localDefaultSettings

// TODO: I get complains when the major version is anything but 0, 
// again, not sure why.
majorVersion := 0

scalaVersion := "2.12.17"

crossScalaVersions := Seq("2.11.12")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-java-time" % "0.2.0",
  "com.lihaoyi" %%% "upickle" % "0.4.3"
) ++ Dependencies()

playCrossCompilationSettings

scalaJSStage in Global := FullOptStage

topLevelDirectory := None

stagingDirectory := (target.value / "scala-2.11")

mappings in Universal ++= Seq((target.value / "scala-2.11" / s"${name.value}-opt.js", s"${name.value}.js"))

val packageTgz = taskKey[File]("package-tgz")
packageTgz := target.value / "universal" / (name.value + "-" + version.value + ".tgz")

artifact in(Universal, packageTgz) ~= { art: Artifact => art.withType("tgz").withExtension("tgz") }
addArtifact(artifact in(Universal, packageTgz), packageTgz in Universal)

// TODO: this doesn't work and I'm not sure why.
// publishAndDistribute := (publishAndDistribute dependsOn (fullOptJS in Compile)).value

publish := publish dependsOn (packageZipTarball in Universal)

publishM2 := publishM2 dependsOn (packageZipTarball in Universal)

publishLocal := publishLocal dependsOn (packageZipTarball in Universal)