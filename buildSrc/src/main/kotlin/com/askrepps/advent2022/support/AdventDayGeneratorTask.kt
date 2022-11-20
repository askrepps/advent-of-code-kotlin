/*
 * MIT License
 *
 * Copyright (c) 2022 Andrew Krepps
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.askrepps.advent2022.support

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.io.FileInputStream
import java.time.ZonedDateTime
import java.util.Properties

private const val DEFAULT_ADVENT_YEAR = "2022"

private const val PROPERTIES_FILENAME = "local.properties"
private const val PROPERTY_ADVENT_SESSION = "advent.session.id"

private const val SESSION_COOKIE_NAME = "session"

abstract class AdventDayGeneratorTask : DefaultTask() {
    @get:Input
    @set:Option(option = "day", description = "The number of the day to generate")
    var day: String? = null

    @get:Input
    @get:Optional
    @set:Option(option = "adventYear", description = "The year of the advent of code event")
    var adventYear: String? = null

    @get:Input
    @set:Option(option = "force", description = "Overwrite existing files")
    var force: Boolean = false

    @TaskAction
    fun generateDay() {
        requireNotNull(day) {
            "Generated day must be provided"
        }
        require(dayNumber > 0) {
            "Generated day must be positive (provided $dayNumber)"
        }

        val substitutionMap = mutableMapOf<String, String>(
            "advent_year" to configuredAdventYear,
            "date_year" to ZonedDateTime.now().year.toString(),
            "day" to paddedDay
        )

        substitutionMap["license"] = licenseTemplate.substituteTemplateVariables(substitutionMap)

        if (force) {
            mainSourceFile.delete()
            testSourceFile.delete()
            inputFile.delete()
        }

        mainSourceFile.writeFileFromTemplateIfNeeded(mainTemplate, substitutionMap)
        testSourceFile.writeFileFromTemplateIfNeeded(testTemplate, substitutionMap)
        inputFile.downloadInputDataIfNeeded(configuredAdventYear, dayNumber)
    }

    private val configuredAdventYear by lazy {
        adventYear ?: DEFAULT_ADVENT_YEAR
    }

    private val dayNumber by lazy {
        requireNotNull(day?.toIntOrNull()) { "Generated day must be a number" }
    }

    private val paddedDay by lazy {
        String.format("%02d", dayNumber)
    }

    private val packagePath by lazy {
        "com/askrepps/advent${configuredAdventYear}/day${paddedDay}"
    }

    private val mainSourceFile by lazy {
        File(project.rootDir, "src/main/kotlin/${packagePath}/Day${paddedDay}.kt")
    }

    private val testSourceFile by lazy {
        File(project.rootDir, "src/test/kotlin/${packagePath}/Day${paddedDay}Test.kt")
    }

    private val inputFile by lazy {
        File(project.rootDir, "src/main/resources/${configuredAdventYear}/day${paddedDay}.txt")
    }

    private fun getInputUrl(adventYear: String, dayNumber: Int) =
        "https://adventofcode.com/${adventYear}/day/${dayNumber}/input"

    private val licenseTemplate by lazy {
        readFileContents("/license.template")
    }

    private val mainTemplate by lazy {
        readFileContents("/main.template")
    }

    private val testTemplate by lazy {
        readFileContents("/test.template")
    }

    private fun readFileContents(filename: String) =
        requireNotNull(javaClass.getResourceAsStream(filename)).bufferedReader().use { it.readText() }

    private fun String.substituteTemplateVariables(substitutionMap: Map<String, String>) =
        substitutionMap.entries.fold(this) { current, (key, value) ->
            current.replace("\${$key}", value)
        }

    private fun File.writeFileFromTemplateIfNeeded(template: String, substitutionMap: Map<String, String>) =
        generateIfNeeded {
            bufferedWriter().use { writer ->
                for (inputLine in template.split("\n")) {
                    writer.write("${inputLine.substituteTemplateVariables(substitutionMap)}\n")
                }
            }
        }

    private fun File.downloadInputDataIfNeeded(adventYear: String, dayNumber: Int) =
        generateIfNeeded {
            val propertiesFile = File(project.rootDir, PROPERTIES_FILENAME)
            val sessionId = propertiesFile.readProperty(PROPERTY_ADVENT_SESSION)
            if (sessionId != null) {
                runBlocking {
                    try {
                        val client = HttpClient(OkHttp)
                        val response = client.get(getInputUrl(adventYear, dayNumber)) {
                            cookie(SESSION_COOKIE_NAME, sessionId)
                        }
                        if (response.status.isSuccess()) {
                            writeText(response.bodyAsText())
                        } else {
                            throw RuntimeException("Unsucessful response: ${response.status}")
                        }
                    } catch (e: Exception) {
                        logger.error("Input data retrieval failed. Generating empty input file...", e)
                        createNewFile()
                    }
                }
            } else {
                logger.error("$PROPERTY_ADVENT_SESSION not set in $PROPERTIES_FILENAME. Generating empty input file...")
                createNewFile()
            }
        }

    private fun File.readProperty(key: String, defaultValue: String? = null) =
        if (exists()) {
            FileInputStream(this).use {
                val properties = Properties()
                properties.load(it)
                properties.getProperty(key, defaultValue)
            }
        } else {
            defaultValue
        }

    private fun File.generateIfNeeded(generator: File.() -> Unit) {
        if (exists()) {
            logger.lifecycle("$name exists. skipping...")
        } else {
            parentFile?.mkdirs()
            generator()
            logger.lifecycle("$name generated")
        }
    }
}
