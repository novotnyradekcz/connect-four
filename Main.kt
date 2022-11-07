package connectfour

fun setDimensions(): MutableList<String> {
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
    val dimensions = readln().replace("\\s".toRegex(), "")
    var dimensionList = mutableListOf("6", "7")
    try {
        if (dimensions != "") {
            dimensionList = dimensions.split("x", ignoreCase = true).toMutableList()
        }
        if (dimensionList[0].toInt() !in 5..9) {
            println("Board rows should be from 5 to 9")
            dimensionList = mutableListOf("0", "0")
        } else if (dimensionList[1].toInt() !in 5..9) {
            println("Board columns should be from 5 to 9")
            dimensionList = mutableListOf("0", "0")
        }
    }
    catch (e: Exception) {
        println("Invalid input")
        dimensionList = mutableListOf("0", "0")
    }
    return dimensionList
}

fun singleOrMultiple(): Int {
    println("Do you want to play single or multiple games?")
    println("For a single game, input 1 or press Enter")
    println("Input a number of games:")
    val numberGames = readln()
    val numbers = Regex("[0-9]?[0-9]?[0-9]")
    if (numberGames == "") return 1
    if (numbers.matches(numberGames) && numberGames != "0") {
        return numberGames.toInt()
    } else {
        println("Invalid input")
        return 0
    }
}

class Board(val rows: Int, val columns: Int) {
    val game = mutableListOf<MutableList<String>>()
    init {
        for (i in 0 until rows) {
            game.add(MutableList(columns) {" "})
        }
    }
    fun drawBoard() {
        for (i in 1..columns) {
            print(" $i")
        }
        println()
        for (j in 1..rows) {
            for (i in 1..columns) {
                print("║${game[j - 1][i - 1]}")
            }
            println("║")
        }
        print("╚")
        for (i in 2..columns) {
            print("═╩")
        }
        println("═╝")
    }
    fun updateBoard(column: Int, token: String): Int {
        if (game[0][column - 1] != " ") {
            println("Column $column is full")
            return 1
        } else if (game[rows - 1][column - 1] == " ") {
            game[rows - 1][column - 1] = token
            drawBoard()
            return -1
        } else {
            for (i in 1 until rows) {
                if (game[i - 1][column - 1] == " " && game[i][column - 1] != " ") {
                    game[i - 1][column - 1] = token
                    drawBoard()
                }
            }
            return -1
        }
    }
    fun checkWin(player: String, token: String): Boolean {
        for (i in 3 until rows) {
            for (j in 3 until columns) {
                if (game[i - 3][j - 3] == game[i - 2][j - 2] && game[i - 2][j - 2] == game[i - 1][j - 1] && game[i - 1][j - 1] == game[i][j] && game[i][j] == token) {
                    println("Player $player won")
                    return true
                }
            }
        }
        for (i in 3 until rows) {
            for (j in 0 until columns - 3) {
                if (game[i - 3][j + 3] == game[i - 2][j + 2] && game[i - 2][j + 2] == game[i - 1][j + 1] && game[i - 1][j + 1] == game[i][j] && game[i][j] == token) {
                    println("Player $player won")
                    return true
                }
            }
        }
        for (i in 0 until rows) {
            for (j in 3 until columns) {
                if (game[i][j - 3] == game[i][j - 2] && game[i][j - 2] == game[i][j - 1] && game[i][j - 1] == game[i][j] && game[i][j] == token) {
                    println("Player $player won")
                    return true
                }
            }
        }
        for (i in 3 until rows) {
            for (j in 0 until columns) {
                if (game[i - 3][j] == game[i - 2][j] && game[i - 2][j] == game[i - 1][j] && game[i - 1][j] == game[i][j] && game[i][j] == token) {
                    println("Player $player won")
                    return true
                }
            }
        }
        return false
    }
    fun checkDraw(): Boolean {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (game[i][j] == " ") return false
            }
        }
        println("It is a draw")
        return true
    }
}

fun main() {
    // entering parameters
    println("Connect Four")
    println("First player's name:")
    val player1 = readln()
    println("Second player's name:")
    val player2 = readln()
    var dimensions: MutableList<String>
    do {
        dimensions = setDimensions()
    } while (dimensions[0].toInt() !in 5..9 || dimensions[1].toInt() !in 5..9)
    // single or multiple games
    var games: Int
    do {
        games = singleOrMultiple()
    } while (games == 0)
    // printing initial information
    println("$player1 VS $player2")
    println("${dimensions[0]} X ${dimensions[1]} board")
    if (games == 1) {
        println("Single game")
    } else {
        println("Total $games games")
    }
    // storing the score
    val score = mutableMapOf(player1 to 0, player2 to 0)
    // playing the game
    var gameCount = 0
    var turn: String
    do {
        if (games != 1) println("Game #${gameCount + 1}")
        // creating and printing the board
        val board = Board(dimensions[0].toInt(), dimensions[1].toInt())
        board.drawBoard()
        // player's taking turns to play
        var turns = if (gameCount % 2 == 0) -1 else 1
        val numbers = Regex("[0-9]?[0-9]?[0-9]")
        do {
            val player = if (turns < 0) player1 else player2
            println("$player's turn:")
            turn = readln()
            val token = if (player == player1) "o" else "*"
            if (numbers.matches(turn)) {
                if (turn.toInt() in 1..dimensions[1].toInt()) {
                    val thisTurn = board.updateBoard(turn.toInt(), token)
                    // checking win and draw conditions
                    val win = board.checkWin(player, token)
                    val draw = board.checkDraw()
                    if (win || draw) {
                        turn = "endgame"
                        if (win) score.merge(player, 2, Int::plus)
                        if (draw) {
                            score.merge(player1, 1, Int::plus)
                            score.merge(player2, 1, Int::plus)
                        }
                        if (games != 1) {
                            println("Score")
                            println("$player1: ${score[player1]} $player2: ${score[player2]}")
                        }
                        gameCount++
                        if (gameCount == games) println("Game over!")
                    }
                    turns *= thisTurn
                } else {
                    println("The column number is out of range (1 - ${dimensions[1]})")
                }
            } else if (turn != "end" && turn != "endgame") {
                println("Incorrect column number")
            } else {
                println("Game over!")
            }
        } while (turn != "end" && turn != "endgame")
    } while (gameCount < games && turn != "end")
}