package me.kleinreveche.models

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class SingleTicTacToeGame {
    private val state = MutableStateFlow(GameState())
    private val playerSockets = ConcurrentHashMap<Char, WebSocketSession>()
    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var delayGameJob: Job? = null

    init {
        state.onEach(::broadcast).launchIn(gameScope)
    }

    fun connectPlayer(session: WebSocketSession): Char? {
        println("Connecting player...")
        val isPlayerX = state.value.connectedPlayers.any { it == PLAYER_X }
        val player = if (isPlayerX) PLAYER_O else PLAYER_X

        state.update {

            if (state.value.connectedPlayers.contains(player)) {
                println("Player already connected!")
                return null
            }

            if (!playerSockets.containsKey(player)) {
                playerSockets[player] = session
            }

            it.copy(connectedPlayers = it.connectedPlayers + player)
        }
        println("Player $player connected!")
        return player
    }

    fun disconnectPlayer(player: Char) {
        playerSockets.remove(player)
        state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers - player
            )
        }
    }

    private suspend fun broadcast(state: GameState) {
        playerSockets.values.forEach { socket ->
            socket.send(Json.encodeToString(state))
        }
    }

    fun handleMove(player: Char, move: Int, gameVersion: GameVersion) {
        val currentPlayer = state.value.currentPlayerTurn

        /*
            Here we check if the game version is not the same as the current game version.
            In the future, we can add more game versions and handle them accordingly.
        */
        if (GameVersion.VERSION_1 != gameVersion) return

        if (state.value.board[move] != null || state.value.winningPlayer != null || currentPlayer != player) return

        state.update { s ->
            val newField = s.board.also { field ->
                field[move] = currentPlayer
            }

            val isBoardFull = newField.all { it != null }
            if (isBoardFull) startNewRound()
            val winningPlayerResult = getWinningPlayer()

            s.copy(
                currentPlayerTurn = if (currentPlayer == PLAYER_X) PLAYER_O else PLAYER_X,
                board = newField,
                winningPlayer = winningPlayerResult.first?.also { startNewRound() },
                winningMoves = winningPlayerResult.second,
                isBoardFull = isBoardFull
            )
        }
    }

    private fun getWinningPlayer(): Pair<Char?, List<Int>> {
        val board = state.value.board
        var winningPlayer: Char? = null
        val winningIndices = mutableListOf<Int>()
        val winningMoves = arrayOf(
            arrayOf(0, 1, 2), arrayOf(3, 4, 5), arrayOf(6, 7, 8),
            arrayOf(0, 3, 6), arrayOf(1, 4, 7), arrayOf(2, 5, 8),
            arrayOf(2, 4, 6), arrayOf(0, 4, 8)
        )

        for (move in winningMoves) {
            val (a, b, c) = move
            if (board[a] == board[b] && board[b] == board[c] && board[a] != null) {
                winningIndices.addAll(move)
            }
        }

        if (winningIndices.isNotEmpty()) {
            winningPlayer = board[winningIndices[0]]
        }

        return Pair(winningPlayer, winningIndices)
    }

    private fun startNewRound() {
        delayGameJob?.cancel()
        delayGameJob = gameScope.launch {
            delay(5000L)
            state.update {
                it.copy(
                    currentPlayerTurn = PLAYER_X,
                    board = GameState.emptyField(),
                    winningPlayer = null,
                    isBoardFull = false
                )
            }
        }
    }
}