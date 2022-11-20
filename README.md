# 2022 Advent of Code

Solutions for the 2022 Advent of Code puzzles implemented in Kotlin.

Advent of Code site: https://adventofcode.com/2022

## Instructions

To run the code for all days:

- Obtain input data for each day from the original source at https://adventofcode.com/2022 and save 
  it in a directory and text file named after the corresponding year and day (`2022/day01.txt`,
  `2022/day02.txt`, `2022/day03.txt`, etc.) under the `src/main/resources` directory.
- Run `./gradlew run`

To run the code for a given day:

- Obtain input for the desired day as noted above
- Run `./gradlew run --args='<day #>'`

To generate the starter code and download the input file data for a new day:

- Run `./gradlew generateDay --day=<day #> [--adventYear=<year #>] [--force]`

Note that for input file downloading to work, your Advent of Code session ID must be stored
as an `advent.session.id` property in a `local.properties` file at the root of the repository.
