/*
 * MIT License
 *
 * Copyright (c) 2021 Andrew Krepps
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

package com.askrepps.advent2021.support

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.time.ZonedDateTime

abstract class AdventDayGeneratorTask : DefaultTask() {

    @get:Input
    @set:Option(option = "day", description = "The number of the day to generate")
    var day: String? = null

    @TaskAction
    fun generateDay() {
        requireNotNull(day) { "Generated day must be provided" }
        require(dayNumber > 0) { "Generated day must be positive (provided $dayNumber)" }
        require(!mainSourceFile.exists() && !testSourceFile.exists()) { "Day $dayNumber files already exist" }

        val substitutionMap = mutableMapOf<String, String?>(
            "advent_year" to "2021",
            "date_year" to ZonedDateTime.now().year.toString(),
            "day" to paddedDay
        )

        substitutionMap["license"] = licenseTemplate.substituteTemplateVariables(substitutionMap)

        mainSourceFile.writeFileFromTemplate(mainTemplate, substitutionMap)
        testSourceFile.writeFileFromTemplate(testTemplate, substitutionMap)
    }

    private val dayNumber by lazy {
        requireNotNull(day?.toIntOrNull()) { "Generated day must be a number" }
    }

    private val paddedDay by lazy {
        String.format("%02d", dayNumber)
    }

    private val packagePath by lazy {
        "com/askrepps/advent2021/day${paddedDay}"
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

    private fun String?.substituteTemplateVariables(substitutionMap: Map<String, String?>) =
        this?.let { input ->
            var output = input
            for ((key, value) in substitutionMap) {
                value?.let {
                    output = output.replace("\${$key}", it)
                }
            }
            output
        }

    private fun File.writeFileFromTemplate(template: String?, substitutionMap: Map<String, String?>) {
        parentFile?.mkdirs()
        bufferedWriter().use { writer ->
            for (inputLine in template?.split("\n") ?: emptyList()) {
                writer.write("${inputLine.substituteTemplateVariables(substitutionMap)}\n")
            }
        }
    }
}
