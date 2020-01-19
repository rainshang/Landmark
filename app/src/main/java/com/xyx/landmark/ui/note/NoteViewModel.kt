package com.xyx.landmark.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.xyx.landmark.vo.COLLECTION
import com.xyx.landmark.vo.USER_NOTES
import com.xyx.landmark.vo.User

class NoteViewModel : ViewModel() {

    private val _isLoad = MutableLiveData<Boolean>().apply { value = false }
    val isLoad: LiveData<Boolean> = _isLoad

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean> = _isSuccessful

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> = _errMsg

    val content = MutableLiveData<String>()

    fun publishNote(loc: User.Note.Loc) {
        FirebaseAuth.getInstance().currentUser?.run {
            _isLoad.value = true
            FirebaseFirestore.getInstance()
                .collection(COLLECTION)
                .document(uid)
                .update(
                    USER_NOTES, FieldValue.arrayUnion(
                        User.Note(content.value!!, loc, System.currentTimeMillis())
                    )
                )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        _isSuccessful.value = true
                    } else {
                        _errMsg.value = it.exception?.localizedMessage
                    }
                    _isLoad.value = false
                }
        }
    }

}