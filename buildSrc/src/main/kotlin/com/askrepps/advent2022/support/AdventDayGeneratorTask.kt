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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.time.ZonedDateTime

abstract class AdventDayGeneratorTask : DefaultTask() {
    @get:Input
    abstract var day: Int

    @get:Input
    abstract var adventYear: String

    @get:Input
    var force: Boolean = false

    @TaskAction
    fun generateDay() {
        require(day != Int.MIN_VALUE) {
            "Generated day must be provided as a property (i.e., -Pday=<day #>)"
        }
        require(day > 0) {
            "Generated day must be positive (provided $day)"
        }

        val substitutionMap = mutableMapOf<String, String>(
            "advent_year" to adventYear,
            "date_year" to ZonedDateTime.now().year.toString(),
            "day" to paddedDay
        )

        substitutionMap["license"] = licenseTemplate.substituteTemplateVariables(substitutionMap)

        if (mainSourceFile.exists() || testSourceFile.exists()) {
            logger.warn("One or more files for day $day already exist and will be skipped (use -Pforce to overwrite)")
        }

        if (force) {
            mainSourceFile.delete()
            testSourceFile.delete()
        }

        mainSourceFile.writeFileFromTemplateIfNeeded(mainTemplate, substitutionMap)
        testSourceFile.writeFileFromTemplateIfNeeded(testTemplate, substitutionMap)
    }

    private val paddedDay by lazy {
        getPaddedDay(day)
    }

    private val packagePath by lazy {
        "com/askrepps/advent${adventYear}/day${paddedDay}"
    }

    private val mainSourceFile by lazy {
        File(project.rootDir, "src/main/kotlin/${packagePath}/Day${paddedDay}.kt")
    }

    private val testSourceFile by lazy {
        File(project.rootDir, "src/test/kotlin/${packagePath}/Day${paddedDay}Test.kt")
    }

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
