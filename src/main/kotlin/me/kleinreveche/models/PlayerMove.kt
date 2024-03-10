package me.kleinreveche.models

import kotlinx.serialization.Serializable

@Serializable
data class PlayerMove(val move: Int, val gameVersion: GameVersion)
