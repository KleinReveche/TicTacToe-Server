package me.kleinreveche.models

import kotlinx.serialization.Serializable

const val PLAYER_X = 'X'
const val PLAYER_O = 'O'

enum class GameVersion {
    VERSION_1
}

@Serializable
data class GameState (
    val currentPlayerTurn: Char? = 'X',
    val board: Array<Char?> = emptyField(),
    val winningPlayer: Char? = null,
    val winningMoves: List<Int> = emptyList(),
    val isBoardFull: Boolean = false,
    val connectedPlayers: List<Char> = emptyList(),
    val playerXWins: Int = 0,
    val playerOWins: Int = 0,
    val draws: Int = 0,
    val gameVersion: GameVersion = GameVersion.VERSION_1
) {
    companion object {
        fun emptyField(): Array<Char?> {
            return Array(9) { null }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (currentPlayerTurn != other.currentPlayerTurn) return false
        if (!board.contentEquals(other.board)) return false
        if (winningPlayer != other.winningPlayer) return false
        if (winningMoves != other.winningMoves) return false
        if (isBoardFull != other.isBoardFull) return false
        if (connectedPlayers != other.connectedPlayers) return false
        if (playerXWins != other.playerXWins) return false
        if (playerOWins != other.playerOWins) return false
        if (draws != other.draws) return false
        if (gameVersion != other.gameVersion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = currentPlayerTurn?.hashCode() ?: 0
        result = 31 * result + board.contentHashCode()
        result = 31 * result + (winningPlayer?.hashCode() ?: 0)
        result = 31 * result + winningMoves.hashCode()
        result = 31 * result + isBoardFull.hashCode()
        result = 31 * result + connectedPlayers.hashCode()
        result = 31 * result + playerXWins
        result = 31 * result + playerOWins
        result = 31 * result + draws
        result = 31 * result + gameVersion.hashCode()
        return result
    }
}