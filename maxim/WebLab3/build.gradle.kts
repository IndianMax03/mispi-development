
import org.apache.tools.ant.BuildException
import org.gradle.crypto.checksum.Checksum
import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.validation.Validator

plugins {
    java
    id("org.gradle.crypto.checksum") version "1.4.0"

}

//ant.importBuild("build.xml")

group = "com.labs.weblab3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.4.0")
    implementation("com.sun.faces:jsf-api:2.2.20")
    implementation("com.sun.faces:jsf-impl:2.2.20")
    implementation("org.primefaces:primefaces:12.0.0")
    implementation("net.bootsfaces:bootsfaces:1.5.0")
    implementation("org.mockito:mockito-inline:3.4.6")
    implementation("org.eclipse.persistence:eclipselink:3.0.3")
    implementation("jakarta.platform:jakarta.jakartaee-api:8.0.0")
    testImplementation("junit:junit:4.13.2")
}

/* Compile start */

tasks.compileJava {
    options.compilerArgs.add("-Xlint:unchecked")
}

val compile = tasks.register<JavaCompile>("compile"){

    group = project.property("tasksGroup").toString()
    dependsOn(tasks.compileJava, tasks.compileTestJava)
}

/* Compile end */

/* Jar start */

val build = tasks.register<Jar>("l3build") {
    dependsOn(compile)
    group = project.property("tasksGroup").toString()
    from("build/${project.property("classesDirectory")}")
    destinationDirectory.set(layout.buildDirectory.dir(project.property("libsDirectory").toString()))
    archiveBaseName.set(project.name)
}

/* Jar end */

/* Music start */

abstract class Music : AbstractExecTask<Music>(Music::class.java) {

    init {
        group = project.property("tasksGroup").toString()
    }

    override fun exec() {
        executable = "afplay"

        args(project.property("mp3File"))

        super.exec()
    }

}


val music = tasks.register<Music>("music") {
    dependsOn(build)
}

/* Music end */

/* Scp start */

open class Scp : AbstractExecTask<Scp>(Scp::class.java) {

    init {
        group = project.property("tasksGroup").toString()
    }

    override fun exec() {
        executable = "scp"

        args(listOf("-P", "2222", "build/libs/WebLab3-1.0-SNAPSHOT.jar", "s333057@helios.se.ifmo.ru:/home/studs/s333057/mispi-lab3"))

        super.exec()
    }

}

val scp = tasks.register<Scp>("scp") {
    dependsOn(build)
}

/* Scp end */

/* XML start */

abstract class Xml : AbstractExecTask<Xml>(Xml::class.java) {

    @get:SkipWhenEmpty
    @get:InputDirectory
    abstract val sourceDirectory: DirectoryProperty

    init {
        group = project.property("tasksGroup").toString()
    }

    override fun exec() {
        val files = sourceDirectory.asFileTree.map { it.path }
        val xmlFiles = mutableListOf<String>()
        val reg = """.*\.x\w*ml$""".toRegex()

        files.forEach {
            if (it.matches(reg)) {
                xmlFiles.add(it)
            }
        }

        var failures : Set<String> = mutableSetOf()
        val parser: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val validator: Validator? = null
        xmlFiles.forEach {
            var document: Document? = null
            try {
                document = parser.parse(it)
            } catch (e: Exception) {
                logger.error("Error while parsing $it: ${e.message}")
                failures = failures.plus(it)
            }
            if (document != null) {
                try {
                    validator?.validate(DOMSource(document))
                } catch (e: Exception) {
                    logger.error("Error while validating $it ${e.message}")
                    failures = failures.plus(it)
                }
            }
        }
        if (failures.isNotEmpty()) throw BuildException("xml validation failures $failures")
    }

}

val xml = tasks.register<Xml>("xml") {
    sourceDirectory.set(layout.projectDirectory)
}

/* XML end */

/* test start */

tasks.test {
    group = project.property("tasksGroup").toString()
    useJUnit()
    dependsOn(build)
    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("testDirOutput"))
        junitXml.outputLocation.set(layout.buildDirectory.dir("testDirOutput"))
    }
}

/* test end */

/* report start */

val report = tasks.register("report") {
    group = project.property("tasksGroup").toString()
    dependsOn(tasks.test)
}

/* test end */

/* clean start */

tasks.clean {
    group = project.property("tasksGroup").toString()
    delete(buildDir)
}

/* clean end */

/* native2ascii start */

abstract class Native2ascii : AbstractExecTask<Native2ascii>(Native2ascii::class.java) {

    @get:SkipWhenEmpty
    @get:InputDirectory
    abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        group = project.property("tasksGroup").toString()
    }

    override fun exec() {

        val files = sourceDirectory.asFileTree.map { it }
        val propertiesFiles = mutableListOf<File>()
        val reg = """.*\.properties""".toRegex()

        files.forEach {
            if (it.path.matches(reg)) {
                propertiesFiles.add(it)
            }
        }

        propertiesFiles.forEach {

            println(it)
            println(outputDirectory.asFile.get().path.toString())

            executable = "native2ascii"

            args(listOf("-encoding", "8859_1", it.path, "${outputDirectory.asFile.get().path}\\${it.name}"))

            super.exec()

        }
    }
}

val native2ascii = tasks.register<Native2ascii>("native2ascii") {
    sourceDirectory.set(layout.projectDirectory.dir(project.property("sourceMainDirectory").toString()).dir("resources"))
    outputDirectory.set(layout.projectDirectory.dir(project.property("sourceMainDirectory").toString()).dir("resources").dir("native2ascii"))
}

/* native2ascii end */


/* doc start */

val doc = tasks.register<Checksum>("doc") {
    group = project.property("tasksGroup").toString()

    dependsOn(build)

    doFirst {
        docSecond
    }

    inputFiles.setFrom(layout.buildDirectory.dir(project.property("classesDirectory").toString()))

    checksumAlgorithm.set(Checksum.Algorithm.MD5)

    outputDirectory.set(layout.buildDirectory.dir("tmp\\l3build\\md5"))

    finalizedBy(tasks.javadoc)

    doLast {

        val files = outputDirectory.asFileTree.map { it }
        val propertiesFiles = mutableListOf<File>()
        val reg = """.*\.(md5|sha512)""".toRegex()

        files.forEach {
            if (it.path.matches(reg)) {
                propertiesFiles.add(it)
            }
        }

        propertiesFiles.forEach {

            println(it.name)

            ant.withGroovyBuilder {
                "echo" ("message" to "Name: ${it.name}\nDigest-Algorithms: ${checksumAlgorithm.get()}\nMD5-Digest: ${it.readText()}\n\n",
                    "file" to "build/tmp/l3build/MANIFEST.MF",
                    "append" to "true"
                )
            }
        }
    }
}

val docSecond = tasks.register<Checksum>("docSecond") {

    dependsOn(build)

    inputFiles.setFrom(layout.buildDirectory.dir(project.property("classesDirectory").toString()))

    checksumAlgorithm.set(Checksum.Algorithm.SHA512)

    outputDirectory.set(layout.buildDirectory.dir("tmp\\l3build\\sha512"))

}

tasks.javadoc {
    group = project.property("tasksGroup").toString()

    source = sourceSets.main.get().java.asFileTree
    setDestinationDir(file("build/tmp/l3build/javadoc"))
}

/* doc end */

/* alt */

val copyAltToMain = tasks.register<Copy>("copyAltToMain") {
    from(layout.projectDirectory.dir(project.property("sourceDirectory").toString()))
    into(layout.projectDirectory.dir(project.property("tmpDirectory").toString()))
    filter {
        line -> line
            .replace(project.property("startedEntityManagerName").toString(), project.property("replacedEntityManagerName").toString())
            .replace(project.property("startedConnectionName").toString(), project.property("replacedConnectionName").toString())
            .replace(project.property("startedTransactionName").toString(), project.property("replacedTransactionName").toString())

    }

    finalizedBy(overwriteFilesInSrcfromAlt)

}

val deleteAlt = tasks.register<Delete>("deleteAlt") {
    delete(layout.projectDirectory.dir(project.property("tmpDirectory").toString()))
}

val overwriteFilesInSrcfromAlt = tasks.register<Copy>("overwriteFilesInSrcfromAlt") {

    from(layout.projectDirectory.dir(project.property("tmpDirectory").toString()))
    into(layout.projectDirectory.dir(project.property("sourceDirectory").toString()))

    dependsOn(compile)

}

val customBuild = tasks.register<Jar>("customBuild") {
    dependsOn(compile)
    from(layout.projectDirectory.dir(project.property("tmpDirectory").toString()))
    destinationDirectory.set(layout.buildDirectory.dir(project.property("libsDirectory").toString()))
    archiveBaseName.set("changedArchive")
    finalizedBy(deleteAlt)
}

val alt = tasks.register("alt") {
    group = project.property("tasksGroup").toString()

    shouldRunAfter(tasks.clean)

    dependsOn(copyAltToMain)

    tasks.clean

    finalizedBy(customBuild)
}

/* alt */
