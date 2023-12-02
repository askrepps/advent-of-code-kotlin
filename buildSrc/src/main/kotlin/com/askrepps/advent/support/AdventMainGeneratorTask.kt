/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Andrew Krepps
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

package com.askrepps.advent.support

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.time.ZonedDateTime

abstract class AdventMainGeneratorTask : DefaultTask() {
    @TaskAction
    fun generateMain() {
        val packagePrefix = project.packagePrefixProperty
        val packagePrefixPath = project.packagePrefixDirectoryPath

        val sourceDirectory = File(project.rootDir, "src/main/kotlin/${packagePrefixPath}/advent")
        sourceDirectory.mkdirs()

        val years = sourceDirectory.getPackageNumbers(prefix = "advent", numberLength = 4)

        val daysByYear = years.associateWith { year ->
            File(sourceDirectory, "advent$year").getPackageNumbers(prefix = "day", numberLength = 2)
        }

        val runnerKeys = daysByYear.entries.flatMap { (year, days) ->
            days.map { day -> year to day }
        }

        val runnerImports =
            if (runnerKeys.isEmpty()) {
                ""
            } else {
                runnerKeys.joinToString(separator = "\n", prefix = "\n", postfix = "\n") { (year, day) ->
                    val paddedDay = getZeroPaddedDay(day)
                    "import ${packagePrefix}.advent.advent${year}.day${paddedDay}.main as run${year}Day${paddedDay}"
                }
            }

        val runnerMapType =
            if (runnerKeys.isEmpty()) {
                "<String, () -> Unit>"
            } else {
                ""
            }

        val runnerMappings = runnerKeys.chunked(4)
            .joinToString(separator = ",\n") { subList ->
                subList.joinToString(prefix = "    ") { (year, day) ->
                    val paddedDay = getZeroPaddedDay(day)
                    "\"${year}-${paddedDay}\" to ::run${year}Day${paddedDay}"
                }
            }

        val substitutionMap = mutableMapOf(
            "date_year" to ZonedDateTime.now().year.toString(),
            "package_prefix" to packagePrefix,
            "runner_imports" to runnerImports,
            "runner_map_type" to runnerMapType,
            "runner_mappings" to runnerMappings
        )

        val licenseTemplate = readResourceFileContents("/license.template", javaClass)
        substitutionMap["license"] = licenseTemplate.substituteTemplateVariables(substitutionMap)

        val mainTemplate = readResourceFileContents("/main.template", javaClass)
        val mainSourceFile = File(sourceDirectory, "Main.kt")
        mainSourceFile.writeFileFromTemplate(mainTemplate, substitutionMap)
        logger.lifecycle("${mainSourceFile.name} generated")
    }

    private fun File.getPackageNumbers(prefix: String, numberLength: Int) =
        listFiles()
            ?.filter { it.name.startsWith(prefix) }
            ?.sortedBy { it.name }
            ?.mapNotNull { it.name.takeLast(numberLength).toIntOrNull() }
            ?: emptyList()
}
