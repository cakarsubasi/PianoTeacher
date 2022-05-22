package com.kocuni.pianoteacher.ui.music

import androidx.compose.ui.graphics.Color

interface Block {
    var name: String
    var color: Color

    object Colors {
        val measure = Color(0xFF888888)
        val C = Color(0xFF8080FF)
        val D = Color(0xFFBF80FF)
        val E = Color(0xFFFF80FF)
        val F = Color(0xFFFF80BF)
        val G = Color(0xFFFF8080)
        val A = Color(0xFFFFBF80)
        val B = Color(0xFFFFFF80)
        val black = Color(0xFF000000)

        val Played = Color(0xFFB882AE)
        val First = Color(0xFF1ED61E)
        val Second = Color(0xFFF6F31B)
        val Third = Color(0xFFD6AB22)

        fun getColor(name: String): Color {
            return when (name[0].lowercase()) {
                "c" -> C
                "d" -> D
                "e" -> E
                "f" -> F
                "g" -> G
                "a" -> A
                "b" -> B
                else -> black
            }
        }
    }
}