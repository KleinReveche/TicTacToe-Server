package me.kleinreveche.tictactoe.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PlayerMove(val move: Int, val gameVersion: Int)
