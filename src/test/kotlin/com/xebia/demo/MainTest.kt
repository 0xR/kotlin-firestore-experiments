package com.xebia.demo

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MainTest {
    @Test
    fun initialize() {
        runBlocking {
            val client = Main(Main.createFirestore())
            val experiment = client.getExperiment("ruben")

            println(experiment)

            delay(60 * 1000)
        }
    }
}