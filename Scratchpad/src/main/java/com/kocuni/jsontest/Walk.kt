package com.kocuni.jsontest

import java.nio.file.Files
import kotlin.io.path.Path

class Walk {


}

fun main() {

    val st = Files.walk(Path(""))
    st.forEach {
        println(it)
    }
}