package com.xebia.demo

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.Timestamp
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.firestore.ListenerRegistration
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient

data class Experiment(val id : String = "", val name : String = "", val createdAt: Timestamp = Timestamp.now())

class Main {
    private val projectId = "playground-3f8a8"
    private val serviceAccountResource = javaClass.getResourceAsStream("/service-account.json")
    private val collectionName = "experiments"

    private var state : List<Experiment> = emptyList()
    private var listener : ListenerRegistration? = null

    init {
        FirebaseApp.initializeApp(firebaseOptions())

        listener = listenForNewExperiments()
    }

    fun getExperiment(name : String) : Experiment? = state.find { it.name == name }

    fun destroy() = listener?.remove()

    private fun listenForNewExperiments() = client()
        .collection(collectionName)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Something went wrong while listening for FireStore snapshots: $error")
            } else {
                val docs = snapshot?.documents?.map { it.toObject(Experiment::class.java).copy(id = it.id) } ?: emptyList()

                // mutation of the global state of experiments
                state = docs

                println("Received doc $docs")
                println("Docs in state $state")
            }
        }

    private fun client() = FirestoreClient.getFirestore()

    private fun firebaseOptions() = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccountResource))
        .setProjectId(projectId)
        .setFirestoreOptions(firestoreOptions())
        .build()

    private fun firestoreOptions() = FirestoreOptions.newBuilder()
        .setTimestampsInSnapshotsEnabled(true)
        .build()
}