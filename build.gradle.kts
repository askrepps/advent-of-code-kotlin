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

import com.askrepps.advent.support.AdventDayGeneratorTask
import com.askrepps.advent.support.AdventInputDownloaderTask
import com.askrepps.advent.support.AdventMainGeneratorTask
import com.askrepps.advent.support.adventYearProperty
import com.askrepps.advent.support.dayProperty
import com.askrepps.advent.support.forceProperty
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

group = "com.askrepps"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val downloadTask = task<AdventInputDownloaderTask>("downloadInput") {
    group = "advent"
    day = project.dayProperty
    adventYear = project.adventYearProperty
}

val updateMainTask = task<AdventMainGeneratorTask>("generateMain") {
    group = "advent"
    adventYear = project.adventYearProperty
}

task<AdventDayGeneratorTask>("generateDay") {
    group = "advent"
    day = project.dayProperty
    adventYear = project.adventYearProperty
    force = project.forceProperty
    finalizedBy(downloadTask, updateMainTask)
}

application {
    mainClass.set("com.askrepps.advent${project.adventYearProperty}.MainKt")
}
