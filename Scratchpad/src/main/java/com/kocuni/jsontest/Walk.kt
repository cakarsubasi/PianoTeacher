package com.kocuni.jsontest

import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.name

class Walk {


}

fun main() {

    val st = Files.walk(Path("Scratchpad"))
    st.filter {
        it.name.contains(Regex.fromLiteral(".json"))
    }.forEach {
        println(it)
    }
}