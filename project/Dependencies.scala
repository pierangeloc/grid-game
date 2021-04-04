import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Dependencies {
  object Versions {
    val http4s = "0.21.0-M6"

    val zio       = "1.0.3"
    val zioCats   = "2.2.0.1"

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

  val scalaTest  = "org.scalatest" %% "scalatest"   % "3.0.5"

  val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

  val zio         = "dev.zio"       %% "zio"              % Versions.zio
  val zioStreams  = "dev.zio"       %% "zio-streams"      % Versions.zio
  val zioCats     = "dev.zio"       %% "zio-interop-cats" % Versions.zioCats

  val cask        =  "com.lihaoyi" %% "cask" % "0.6.5"

  val fs2         = "co.fs2" %% "fs2-io" % Versions.fs2
}
