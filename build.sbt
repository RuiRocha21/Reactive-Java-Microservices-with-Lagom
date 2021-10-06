import com.lightbend.lagom.core.LagomVersion
organization in ThisBuild := "org.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.13.0"
lagomCassandraPort in ThisBuild := 9402

def dockerSettings = Seq(
  dockerUpdateLatest := true,
  dockerBaseImage := getDockerBaseImage(),
  dockerUsername := sys.props.get("docker.username"),
  dockerRepository := sys.props.get("docker.registry")
)

def getDockerBaseImage(): String = sys.props.get("java.version") match {
  case Some(v) if v.startsWith("11") => "adoptopenjdk/openjdk11"
  case _                             => "adoptopenjdk/openjdk8"
}
//,`blog-censured-impl`
lazy val `blog` = (project in file("."))
  .aggregate(`blog-api`, `blog-impl`,`censured-api`,`censured-impl`)

lazy val `blog-api` = (project in file("blog-api"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )

lazy val `blog-impl` = (project in file("blog-impl"))
  .enablePlugins(LagomJava)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomLogback,
      lagomJavadslTestKit,
      hamcrestLibrary,
      assertj,
      lagomJavadslAkkaDiscovery,
      lombok,
      vavr,
      hibernate,
      akkaDiscoveryKubernetesApi
    )
  )
  .settings(dockerSettings)
  .settings(lagomForkedTestSettings)
  .dependsOn(`blog-api`)

lazy val `censured-api` = (project in file("censured-api"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )
  .dependsOn(`blog-api`)

lazy val `censured-impl` = (project in file("censured-impl"))
  .enablePlugins(LagomJava)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaClient,
      lagomLogback,
      lagomJavadslTestKit,
      hamcrestLibrary,
      assertj,
      lagomJavadslAkkaDiscovery,
      lombok,
      vavr,
      hibernate,
      akkaDiscoveryKubernetesApi
    )
  )
  .settings(dockerSettings)
  .dependsOn(`censured-api`)



val hamcrestLibrary = "org.hamcrest" % "hamcrest-library" % "2.1" % Test
val lagomJavadslAkkaDiscovery = "com.lightbend.lagom" %% "lagom-javadsl-akka-discovery-service-locator" % LagomVersion.current
val lombok = "org.projectlombok" % "lombok" % "1.18.10" % "provided"
val hibernate = "org.hibernate" % "hibernate-core" % "5.2.12.Final"
val assertj = "org.assertj" % "assertj-core" % "3.14.0"
val akkaDiscoveryKubernetesApi = "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % "1.0.10"
val vavr = "io.vavr" % "vavr" % "0.10.4"

def common = Seq(
  javacOptions in Compile += "-parameters"
)

//-Dsbt.log.noformat=true
//libraryDependencies += "io.vavr" % "vavr" % "0.10.4"

// Use Kafka server running in a docker container
lagomKafkaEnabled in ThisBuild := false
lagomKafkaPort in ThisBuild := 9092
