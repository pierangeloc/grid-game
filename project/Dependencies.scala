import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Dependencies {
  object Versions {
    val http4s = "0.21.0-M6"

    val zio       = "1.0.5"
    val zioCats   = "2.4.0.0"

    val fs2        = "2.4.4"
    val cats       = "2.2.0"
    val catsEffect = "2.2.0"
    val circe      = "0.12.1"

    val laminar    = "0.12.2"
  }

  object Frontend {
    val all = Def.setting(
      Seq(
        "com.raquo" %%% "laminar" % Versions.laminar
      )
    )
  }

  val zio        = Def.setting(
    Seq(
      "dev.zio"       %%% "zio"              % Versions.zio,
      "dev.zio"       %%% "zio-streams"      % Versions.zio,
      "dev.zio"       %%% "zio-interop-cats" % Versions.zioCats
    )
  )

  val dateTime: Def.Initialize[Seq[ModuleID]] = Def.setting {
    Seq(
      "io.github.cquiroz" %%% "scala-java-time"      % "2.2.0",
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.2.0"
    )
  }

}
