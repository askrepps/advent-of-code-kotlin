# Advent of Code - Kotlin

Solutions for Advent of Code 2021-2023 implemented in Kotlin

Advent of Code site: https://adventofcode.com

## Instructions

To run the code for all days:

- Obtain input data as shown below
- Run `./gradlew run`

To run the code for all days in a specific year:

- Obtain input for the desired days as shown below
- run `./gradlew run --args='<year #>'`

To run the code for a specific day:

- Obtain input for the desired day as shown below
- Run `./gradlew run --args='<year #> <day #>'`

To download the input file data for a specific day:

- Run `./gradlew downloadInput -Pday=<day #> [-PadventYear=<year #>]`

To generate the starter code and download the input file data for a new day:

- Run `./gradlew generateDay -Pday=<day #> [-PadventYear=<year #>] [-Pforce]`

Note that for input file downloading to work, your Advent of Code session ID must be stored
as an `advent.session.id` property in a `local.properties` file at the root of the repository.
