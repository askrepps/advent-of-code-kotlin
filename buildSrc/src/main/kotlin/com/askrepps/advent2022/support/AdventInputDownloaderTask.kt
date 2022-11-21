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
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileInputStream
import java.util.Properties

private const val PROPERTIES_FILENAME = "local.properties"
private const val PROPERTY_ADVENT_SESSION = "advent.session.id"

private const val SESSION_COOKIE_NAME = "session"

abstract class AdventInputDownloaderTask : DefaultTask() {
    @get:Input
    abstract var day: Int

    @get:Input
    abstract var adventYear: String

    @TaskAction
    fun downloadInput() {
        if (day <= 0) {
            allDays.forEach { downloadInputForDay(it) }
        } else {
            downloadInputForDay(day)
        }
    }

    fun downloadInputForDay(inputDay: Int) {
        val inputFile = getInputfile(adventYear, inputDay)
        inputFile.parentFile?.mkdirs()
        val propertiesFile = File(project.rootDir, PROPERTIES_FILENAME)
        val sessionId = propertiesFile.readProperty(PROPERTY_ADVENT_SESSION)
        var generated = false
        if (sessionId != null) {
            runBlocking {
                try {
                    val client = HttpClient(OkHttp)
                    val response = client.get(getInputUrl(adventYear, inputDay)) {
                        cookie(SESSION_COOKIE_NAME, sessionId)
                    }
                    if (response.status.isSuccess()) {
                        inputFile.writeText(response.bodyAsText())
                        generated = true
                    } else {
                        throw RuntimeException("Unsucessful response: ${response.status}")
                    }
                } catch (e: Exception) {
                    logger.error("Input data retrieval failed. Generating empty input file...", e)
                }
            }
        } else {
            logger.error("$PROPERTY_ADVENT_SESSION not set in $PROPERTIES_FILENAME. Generating empty input file...")
        }
        if (!generated) {
            inputFile.createNewFile()
        }
        logger.lifecycle("${inputFile.name} generated")
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

    private fun getInputfile(adventYear: String, day: Int) =
        File(project.rootDir, "src/main/resources/${adventYear}/day${getPaddedDay(day)}.txt")

    private fun getInputUrl(adventYear: String, day: Int) =
        "https://adventofcode.com/${adventYear}/day/${day}/input"
}
