package com.xyx.landmark.vo

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.parcel.Parcelize

data class User(
    val uid: String? = null,
    val name: String? = null,
    val notes: List<Note>? = null
) {
    data class Note(
        val content: String? = null,
        val loc: Loc? = null,
        val timestamp: Long? = null,
        // not saving to database
        @Exclude
        var uid: String? = null,
        @Exclude
        var name: String? = null
    ) {
        @Parcelize
        data class Loc(
            val lat: Double? = null,
            val lng: Double? = null
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
        .set(
            mapOf(
                USER_UID to uid,
                USER_NAME to name
            ), SetOptions.merge()
        )
}
