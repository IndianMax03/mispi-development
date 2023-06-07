
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
    implementation("org.eclipse.persistence:eclipselink:3.0.3")
    implementation("jakarta.platform:jakarta.jakartaee-api:8.0.0")
    implementation("org.mockito:mockito-inline:3.4.6")
    testImplementation("junit:junit:4.13.2")
}

/* Compile start */

val compile = tasks.register("compile") {
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

        var failures: Set<String> = mutableSetOf()
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

tasks.withType<Test>().configureEach {
    group = project.property("tasksGroup").toString()
    project.setProperty("testResultsDirName", "$buildDir/${project.property("testOutputDir").toString()}")
    useJUnit()
    dependsOn(build)
    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("${project.property("testOutputDir").toString()}/${project.property("testHtmlOutputDir").toString()}"))
        junitXml.outputLocation.set(layout.buildDirectory.dir("${project.property("testOutputDir").toString()}/${project.property("testXmlOutputDir").toString()}"))
    }
}

/* test end */

/* report start */

tasks.register("report") {
    group = project.property("tasksGroup").toString()
    dependsOn(tasks.test)
    doLast {
        ant.withGroovyBuilder {
            "echo"("message" to "Выполняю git branch tests")
            "exec"("executable" to "git", "failonerror" to "false") {
                "arg"("value" to "branch")
                "arg"("value" to "tests")
            }
            "echo"("message" to "Выполняю git stash")
            "exec"("executable" to "git", "failonerror" to "true") {
                "arg"("value" to "stash")
            }
            "echo"("message" to "Выполняю git switch tests")
            "exec"("executable" to "git", "failonerror" to "true") {
                "arg"("value" to "switch")
                "arg"("value" to "tests")
            }
            "echo"("message" to "Выполняю git add ${layout.buildDirectory.dir("test-results").get().asFile.path.toString()}")
            "exec"("executable" to "git", "failonerror" to "false") {
                "arg"("value" to "add")
                "arg"("value" to "${layout.buildDirectory.dir("test-results").get().asFile.path.toString()}")
            }
            "echo"("message" to "Выполняю git commit -m \"Test report on version: ${project.property("projectVersion").toString()}\"")
            "exec"("executable" to "git", "failonerror" to "false") {
                "arg"("value" to "commit")
                "arg"("value" to "-m")
                "arg"("value" to "\"Test report on version: ${project.property("projectVersion").toString()}\"")
            }
            "echo"("message" to "Выполняю git switch -")
            "exec"("executable" to "git", "failonerror" to "true") {
                "arg"("value" to "switch")
                "arg"("value" to "-")
            }
            "echo"("message" to "Выполняю git stash apply")
            "exec"("executable" to "git", "failonerror" to "false") {
                "arg"("value" to "stash")
                "arg"("value" to "apply")
            }
        }
    }
}

/* test end */

tasks.register("team") {
    group = project.property("tasksGroup").toString()

    doLast {

        ant.withGroovyBuilder {
            "exec"("executable" to "mkdir", "failonerror" to "false") {
                "arg"("value" to "${layout.projectDirectory.asFile.path.toString()}/teamTmp")
            }
        }
        for (i in 1..4) {
            ant.withGroovyBuilder {
                "exec"("executable" to "mkdir", "failonerror" to "false") {
                    "arg"("value" to "${layout.projectDirectory.asFile.path.toString()}/teamTmp/${i}")
                }
                "exec"("executable" to "git", "failonerror" to "true") {
                    "arg"("value" to "checkout")
                    "arg"("value" to "HEAD~${i}")
                    "arg"("value" to "--")
                    "arg"("value" to "${layout.projectDirectory.dir(project.property("sourceDirectory").toString()).toString()}")
                }
                "exec"("executable" to "gradle", "failonerror" to "false") {
                    "arg"("value" to "l3build")
                }
                "exec"("executable" to "mv", "failonerror" to "false") {
                    "arg"("value" to "${layout.buildDirectory.asFile.get().path.toString()}/libs/")
                    "arg"("value" to "${layout.projectDirectory.asFile.path.toString()}/teamTmp/${i}")
                }
                "exec"("executable" to "git", "failonerror" to "true") {
                    "arg"("value" to "checkout")
                    "arg"("value" to "-")
                    "arg"("value" to "--")
                    "arg"("value" to "${layout.projectDirectory.dir(project.property("sourceDirectory").toString()).toString()}")
                }
            }
        }

        ant.withGroovyBuilder {
            "zip"("destfile" to "${project.buildDir.path.toString()}/teamTask/team.zip", "basedir" to "teamTmp", "includes" to "**/*.jar")
            "exec"("executable" to "rm", "failonerror" to "false") {
                "arg"("value" to "-rf")
                "arg"("value" to "teamTmp")
            }
        }
    }
}


tasks.register("env") {
    group = project.property("tasksGroup").toString()

    File("env.txt").forEachLine {
        if (!it.toString().startsWith("#")) {
            tasks.compileJava.get().options.compilerArgs.add(it)
        }
    }

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(tasks.compileJava.get().options.compilerArgs.get(0)))
    tasks.compileJava.get().options.compilerArgs.removeAt(0)

    finalizedBy(tasks.getByName("l3build"))
}

tasks.register("diff") {
    group = project.property("tasksGroup").toString()
    doLast {
        ant.withGroovyBuilder {
            "exec"("executable" to "git", "failonerror" to "true", "output" to "statusFile.txt") {
                "arg"("value" to "status")
                "arg"("value" to "-s")
                "arg"("value" to "-uno")
            }
        }

        val defendedFiles: ArrayList<File> = ArrayList()

        File("defend.txt").forEachLine {
            val fileToDefend = File(it.toString())
            if (!(fileToDefend.isFile && fileToDefend.toString().endsWith(".java"))) {
                throw BuildException("Wrong file [${it.toString()}] in defend.txt. It should be existing file with pattern <filename>.java.")
            }

            defendedFiles.add(fileToDefend)
        }

        val filesToCommit: ArrayList<File> = ArrayList()

        File("statusFile.txt").forEachLine {
            val fileToCommit = File(it.subSequence(3, it.length).toString())
            if (defendedFiles.contains(fileToCommit)) {
                throw BuildException("Cannot commit changes: file [${fileToCommit.toString()}] is defended!")
            }
            filesToCommit.add(fileToCommit)
        }

        filesToCommit.forEach {
            ant.withGroovyBuilder {
                "exec"("executable" to "git", "failonerror" to "false") {
                    "arg"("value" to "add")
                    "arg"("value" to "${it.path.toString()}")
                }
            }
        }

        ant.withGroovyBuilder {

            "exec"("executable" to "git", "failonerror" to "true") {
                "arg"("value" to "commit")
                "arg"("value" to "-m")
                "arg"("value" to "\"Autogenereated commit except defended files\"")
            }
            "exec"("executable" to "rm", "failonerror" to "false") {
                "arg"("value" to "statusFile.txt")
            }

        }
    }
}

tasks.register("history") {
    group = project.property("tasksGroup").toString()

    doLast {
        ant.withGroovyBuilder {
            "echo"("message" to "Прячу изменения...")
            "exec"("executable" to "git", "failonerror" to "false") {
                "arg"("value" to "stash")
            }
            "echo"("message" to "Проверяю количество коммитов")
            "exec"("executable" to "git", "failonerror" to "true", "output" to "logInfo.txt") {
                "arg"("value" to "rev-list")
                "arg"("value" to "--count")
                "arg"("value" to "--all")
            }
        }

        var commits: Int = 0

        File("logInfo.txt").useLines {
            commits = Integer.parseInt(it.firstOrNull())
        }

        ant.withGroovyBuilder {
            "exec"("executable" to "rm", "failonerror" to "false") {
                "arg"("value" to "logInfo.txt")
            }
        }

        ant.withGroovyBuilder { "echo"("message" to "Начинаю цикл поиска валидной сборки...") }
        for (i in 0..commits) {
            ant.withGroovyBuilder {
                "echo"("message" to "Откатываю src/")
                "exec"("executable" to "git", "failonerror" to "false") {
                    "arg"("value" to "checkout")
                    "arg"("value" to "HEAD~${i}")
                    "arg"("value" to "--")
                    "arg"("value" to "${layout.projectDirectory.dir(project.property("sourceDirectory").toString()).toString()}")
                }
                "echo"("message" to "Компилирую...")
                "exec"("executable" to "gradle", "failonerror" to "false", "output" to "buildResult.txt") {
                    "arg"("value" to "compile")
                }
                if (i != 0) {
                    "echo"("message" to "Возвращаю src/")
                    "exec"("executable" to "git", "failonerror" to "false") {
                        "arg"("value" to "checkout")
                        "arg"("value" to "-")
                        "arg"("value" to "--")
                        "arg"("value" to "${layout.projectDirectory.dir(project.property("sourceDirectory").toString()).toString()}")
                    }
                }
            }

            var isSuccessfull: Boolean = false

            File("buildResult.txt").forEachLine {
                if (it.toString().startsWith("BUILD SUCCESSFUL")) isSuccessfull = true
            }
            ant.withGroovyBuilder {
                "exec"("executable" to "rm", "failonerror" to "false") {
                    "arg"("value" to "buildResult.txt")
                }
            }

            if (isSuccessfull) {
                ant.withGroovyBuilder {
                    "echo"("message" to "Найдена рабочая версия!")
                }
                if (i == 0) {
                    ant.withGroovyBuilder { "echo"("message" to "Она ведь текущая!!!") }
                    break
                }
                ant.withGroovyBuilder {
                    "echo"("message" to "Формирую файл с косяками!")
                    "echo"("message" to "Иду в прошлое")
                    "exec"("executable" to "git", "failonerror" to "true") {
                        "arg"("value" to "checkout")
                        "arg"("value" to "HEAD~${i - 1}")
                    }
                    "exec"("executable" to "mkdir", "failonerror" to "false") {
                        "arg"("value" to "${layout.projectDirectory.asFile.path.toString()}/history")
                    }
                    "echo"("message" to "Выполняю git diff")
                    "exec"("executable" to "git", "failonerror" to "false", "output" to "${layout.projectDirectory.asFile.path.toString()}/history/diffResult.txt") {
                        "arg"("value" to "diff")
                        "arg"("value" to "HEAD~${i}")
                        "arg"("value" to "HEAD~${i - 1}")
                    }
                    "echo"("message" to "Возвращаюсь в настоящее")
                    "exec"("executable" to "git", "failonerror" to "true") {
                        "arg"("value" to "checkout")
                        "arg"("value" to "-")
                    }

                }
                break
            }
        }
        ant.withGroovyBuilder {
            "echo"("message" to "Возвращаю то, что вы не припрятали!")
            "exec"("executable" to "git", "failonerror" to "false") {
                "arg"("value" to "stash")
                "arg"("value" to "apply")
            }
        }
    }

}
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

            executable = "native2ascii"

            args(listOf("-encoding", "8859_1", it.path, "${outputDirectory.asFile.get().path}\\${it.name}"))

            super.exec()

        }
    }
}

val native2ascii = tasks.register<Native2ascii>("native2ascii") {
    delete(layout.projectDirectory.dir(project.property("sourceMainDirectory").toString()).dir("resources").dir("native2ascii"))
    sourceDirectory.set(layout.projectDirectory.dir(project.property("sourceMainDirectory").toString()).dir("resources"))
    outputDirectory.set(layout.projectDirectory.dir(project.property("sourceMainDirectory").toString()).dir("resources").dir("native2ascii"))
}

/* native2ascii end */


/* doc start */

val docSecond = tasks.register<Checksum>("docSecond") {

    inputFiles.setFrom(layout.buildDirectory.dir(project.property("classesDirectory").toString()))

    checksumAlgorithm.set(Checksum.Algorithm.MD5)

    outputDirectory.set(layout.buildDirectory.dir("tmp\\l3build\\md5"))

    finalizedBy(tasks.javadoc)

    doLast {

        val files = outputDirectory.asFileTree.map { it }
        val propertiesFilesMd5 = mutableListOf<File>()
        val regMd5 = """.*\.md5""".toRegex()

        files.forEach {
            if (it.path.matches(regMd5)) {
                propertiesFilesMd5.add(it)
            }
        }

        for (i in 0 .. propertiesFilesMd5.size - 1) {

            ant.withGroovyBuilder {
                "echo" ("message" to "Name: ${propertiesFilesMd5[i].name}\nDigest-Algorithms: ${checksumAlgorithm.get()}\nMD5-Digest: ${propertiesFilesMd5[i].readText()}\n"
                        + "\n"
                    ,
                    "file" to "build/tmp/l3build/MANIFEST.MF",
                    "append" to "true"
                )
            }
        }
    }
}

val doc = tasks.register<Checksum>("doc") {
    group = project.property("tasksGroup").toString()

    dependsOn(build)

    inputFiles.setFrom(layout.buildDirectory.dir(project.property("classesDirectory").toString()))

    checksumAlgorithm.set(Checksum.Algorithm.SHA512)

    outputDirectory.set(layout.buildDirectory.dir("tmp\\l3build\\sha512"))

    finalizedBy(docSecond)

    doLast {
        val files = outputDirectory.asFileTree.map { it }
        val propertiesFilesSha512 = mutableListOf<File>()
        val regSha512 = """.*\.sha512""".toRegex()

        files.forEach {
            if (it.path.matches(regSha512)) {
                propertiesFilesSha512.add(it)
            }
        }

        for (i in 0 .. propertiesFilesSha512.size - 1) {

            ant.withGroovyBuilder {
                "echo" ("message" to "Name: ${propertiesFilesSha512[i].name}\nDigest-Algorithms: ${checksumAlgorithm.get()}\n"
                        + "SHA512-Digest: ${propertiesFilesSha512[i].readText()}\n\n"
                    ,
                    "file" to "build/tmp/l3build/MANIFEST.MF",
                    "append" to "true"
                )
            }
        }
    }

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
