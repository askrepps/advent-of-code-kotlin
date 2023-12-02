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

import org.gradle.api.Project
import java.io.File

private const val PROPERTY_DAY = "day"
private const val PROPERTY_ADVENT_YEAR = "adventYear"
private const val PROPERTY_FORCE = "force"
private const val PROPERTY_PACKAGE_PREFIX = "packagePrefix"

private const val DEFAULT_DAY = Int.MIN_VALUE
private const val DEFAULT_ADVENT_YEAR = "2022"

private const val FIRST_DAY = 1
private const val LAST_DAY = 25

val Project.dayProperty: Int
    get() = findProperty(PROPERTY_DAY)?.toString()?.toIntOrNull() ?: DEFAULT_DAY

val Project.adventYearProperty: String
    get() = findProperty(PROPERTY_ADVENT_YEAR)?.toString() ?: DEFAULT_ADVENT_YEAR

val Project.forceProperty: Boolean
    get() = hasProperty(PROPERTY_FORCE)

val Project.packagePrefixProperty: String
    get() = findProperty(PROPERTY_PACKAGE_PREFIX)?.toString() ?: group.toString()

val Project.packagePrefixDirectoryPath: String
    get() = packagePrefixProperty.replace('.', '/')

val allDays = FIRST_DAY..LAST_DAY

fun getZeroPaddedDay(day: Int) =
    String.format("%02d", day)

fun getSpacePaddedDay(day: Int) =
    String.format("%2d", day)

fun <T> readResourceFileContents(filename: String, javaClass: Class<T>) =
    requireNotNull(javaClass.getResourceAsStream(filename)).bufferedReader().use { it.readText() }

fun File.writeFileFromTemplate(template: String, substitutionMap: Map<String, String>) =
    bufferedWriter().use { writer ->
        for (inputLine in template.split("\n")) {
            writer.write("${inputLine.substituteTemplateVariables(substitutionMap)}\n")
        }
    }

fun String.substituteTemplateVariables(substitutionMap: Map<String, String>) =
    substitutionMap.entries.fold(this) { current, (key, value) ->
        current.replace("\${$key}", value)
    }
