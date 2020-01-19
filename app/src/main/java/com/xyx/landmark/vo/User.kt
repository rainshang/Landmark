package com.xyx.landmark.vo

import android.os.Parcelable
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.parcel.Parcelize

data class User(
    val uid: String,
    val name: String,
    val notes: List<Note>
) {
    data class Note(
        val content: String,
        val loc: Loc,
        val timestamp: Long
    ) {
        @Parcelize
        data class Loc(
            val lat: Double,
            val lng: Double
        ) : Parcelable
    }
}

const val COLLECTION = "users"
const val USER_UID = "uid"
const val USER_NAME = "name"
const val USER_NOTES = "notes"

fun updateUserInfo(uid: String, name: String) {
    FirebaseFirestore.getInstance()
        .collection(COLLECTION)
        .document(uid)
        .update(
            mapOf(
                USER_UID to uid,
                USER_NAME to name
            )
        )
}
