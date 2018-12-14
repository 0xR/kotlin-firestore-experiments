package com.xebia.demo

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.Timestamp
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.firestore.ListenerRegistration
import com.google.cloud.firestore.Query
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileInputStream

data class Experiment(val id: String = "", val name: String = "", val createdAt: Timestamp = Timestamp.now())

class Main(val store: Firestore) {
    private var state: List<Experiment> = emptyList()
    private var listener: ListenerRegistration? = null

    init {
        listener = listenForNewExperiments()
    }

    fun getExperiment(name: String): Experiment? = state.find { it.name == name }

    private fun listenForNewExperiments() = store
        .collection("experiments")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Something went wrong while listening for FireStore snapshots: $error")
            } else {
                val docs =
                    snapshot?.documents?.map { it.toObject(Experiment::class.java).copy(id = it.id) } ?: emptyList()

                // mutation of the global state of experiments
                state = docs

                println("Docs in state $state")
            }
        }

    companion object {
        fun createFirestore(): Firestore {
            FirebaseApp.initializeApp(
                FirebaseOptions.builder()
                    .setCredentials(
                        GoogleCredentials.fromStream(
                            FileInputStream("src/main/resources/service-account.json")
                        )
                    )
                    .setProjectId("ruben-oostinga-speeltuin")
                    .setFirestoreOptions(
                        FirestoreOptions.newBuilder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build()
                    )
                    .build()
            )

            return FirestoreClient.getFirestore();
        }
    }

}


