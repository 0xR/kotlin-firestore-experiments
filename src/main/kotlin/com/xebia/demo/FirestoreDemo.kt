package com.xebia.demo

import com.google.cloud.firestore.*
import java.lang.Exception
import java.util.concurrent.TimeUnit

class FirestoreDemo() {
    var currentMessage: String? = null;


    init {
        val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
            .setProjectId("ruben-oostinga-speeltuin")
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        val db = firestoreOptions.service

        val result = db
            .collection("messages")
            .document("latest")
            .get()
            .get(1000, TimeUnit.SECONDS)
            .getString("message");


        currentMessage = result ?: throw Exception("no message");

        db
            .collection("messages")
            .document("latest")
            .addSnapshotListener(fun(value, error) {
                if (error != null) {
                    println(error.message);
                    return
                }
                val message = value?.get("message")
                if (message is String) {
                    currentMessage = message;
                }
            })
    }

    fun doIt(): String? {
        return currentMessage;
    }

}