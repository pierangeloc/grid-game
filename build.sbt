import Dependencies._



lazy val commonSettings = inThisBuild(
    Seq(
      scalaVersion := "2.13.1",
      scalacOptions += "-Ymacro-annotations",
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.3"  cross CrossVersion.full),
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    )
)

lazy val frontend =
  project.in(file(
    "frontend"
  )).disablePlugins(RevolverPlugin)
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(WebScalaJSBundlerPlugin)
    .settings(commonSettings)
    .settings(
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      npmDependencies in Compile ++= Seq("react" -> "16.13.1",
        "react-dom" -> "16.13.1",
        "react-proxy" -> "1.1.8",
        "postcss-import" -> "14.0.1"
      ),
      npmDevDependencies in Compile ++= Seq(
        "file-loader" -> "6.0.0",
        "style-loader" -> "1.2.1",
        "css-loader" -> "3.5.3",
        "html-webpack-plugin" -> "4.3.0",
        "copy-webpack-plugin" -> "5.1.1",
        "webpack-merge" -> "4.2.2",
        "postcss-loader" -> "4.1.0",
        "postcss" -> "8.2.6",
        "tailwindcss" -> "2.0.1",
        "autoprefixer" -> "10.0.2",
      ),
      libraryDependencies ++= Seq(
        "me.shadaj" %%% "slinky-web" % "0.6.5",
        "me.shadaj" %%% "slinky-hot" % "0.6.5"
      ) ++ (Dependencies.Frontend.all.value ++
        zio.value ++ dateTime.value
        ),
      webpack / version := "4.43.0",
      startWebpackDevServer / version := "3.11.0",
      webpackResources := baseDirectory.value / "webpack" * "*",
      fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-fastopt.config.js"),
      fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot"),
      fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),
      fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-opt.config.js"),
      Test / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-core.config.js"),
      Test / requireJsDomEnv := true
    )
    .enablePlugins(ScalaJSBundlerPlugin)

addCommandAlias("dev", ";front/fastOptJS::startWebpackDevServer;~front/fastOptJS")
addCommandAlias("build", "front/fullOptJS::webpack")


lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "laminar-tetris",
    organization := "io.tuliplogic",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq()
  )
