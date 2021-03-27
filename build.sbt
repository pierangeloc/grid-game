import Dependencies._
enablePlugins(ScalaJSPlugin)

lazy val commonSettings = inThisBuild(
    Seq(
      scalaVersion := "2.13.1",
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.3"  cross CrossVersion.full),
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    )
)

lazy val frontend =
  (crossProject(JSPlatform).crossType(CrossType.Pure) in file(
    "frontend"
  )).disablePlugins(RevolverPlugin)
    .settings(commonSettings)
    .jsSettings(
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      libraryDependencies ++= Dependencies.Frontend.all.value,
    )

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "laminar-tetris",
    organization := "io.tuliplogic",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq()
  )
