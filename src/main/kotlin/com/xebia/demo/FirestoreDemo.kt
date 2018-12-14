package com.xebia.demo

import com.google.cloud.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel


fun main() = runBlocking {
    val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
        .setProjectId("ruben-oostinga-speeltuin")
        .setTimestampsInSnapshotsEnabled(true)
        .build();

    val db = firestoreOptions.service;

    val channel = Channel<String>()

    db
        .collection("messages")
        .document("latest")
        .addSnapshotListener(fun(value, error) {
            if (error != null) {
                println(error.message);
                return
            }
            launch {
                val message = value?.get("message")
                if (message is String) {
                    channel.send(message)
                }
            }
        })

    for (message in channel) {
        println(message)
    }
}

