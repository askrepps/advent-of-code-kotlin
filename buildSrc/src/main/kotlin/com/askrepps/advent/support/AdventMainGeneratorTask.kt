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

package com.askrepps.advent.support

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.time.ZonedDateTime

abstract class AdventMainGeneratorTask : DefaultTask() {
    @get:Input
    abstract var adventYear: String

    @TaskAction
    fun generateMain() {
        val days = sourceDirectory.listFiles()
            ?.filter { it.name.startsWith("day") }
            ?.sortedBy { it.name }
            ?.mapNotNull { it.name.takeLast(2).toIntOrNull() }
            ?: emptyList()

        val dayRunnerImports =
            if (days.isEmpty()) {
                ""
            } else {
                days.joinToString(separator = "\n", prefix = "\n", postfix = "\n") {
                    val paddedDay = getZeroPaddedDay(it)
                    "import com.askrepps.advent${adventYear}.day${paddedDay}.main as runDay${paddedDay}"
                }
            }

        val dayRunnerMapType =
            if (days.isEmpty()) {
                "<Int, () -> Unit>"
            } else {
                ""
            }

        val dayRunnerMappings = days.chunked(5)
            .joinToString(separator = ",\n") { subList ->
                subList.joinToString(prefix = "    ") {
                    "${getSpacePaddedDay(it)} to ::runDay${getZeroPaddedDay(it)}"
                }
            }

        val substitutionMap = mutableMapOf<String, String>(
            "advent_year" to adventYear,
            "date_year" to ZonedDateTime.now().year.toString(),
            "runner_imports" to dayRunnerImports,
            "runner_map_type" to dayRunnerMapType,
            "runner_mappings" to dayRunnerMappings
        )

        substitutionMap["license"] = licenseTemplate.substituteTemplateVariables(substitutionMap)

        mainSourceFile.writeFileFromTemplate(mainTemplate, substitutionMap)
        logger.lifecycle("${mainSourceFile.name} generated")
    }

    private val sourceDirectory by lazy {
        File(project.rootDir, "src/main/kotlin/com/askrepps/advent${adventYear}")
    }

    private val mainSourceFile by lazy {
        File(sourceDirectory, "Main.kt")
    }

    private val licenseTemplate by lazy {
        readResourceFileContents("/license.template", javaClass)
    }

    private val mainTemplate by lazy {
        readResourceFileContents("/main.template", javaClass)
    }
}
