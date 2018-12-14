package com.xebia.demo

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MainTest {
    @Test
    fun initialize() {
        runBlocking {
            val client = Main(Main.createFirestore())

            delay(60 * 1000)
        }
    }
}