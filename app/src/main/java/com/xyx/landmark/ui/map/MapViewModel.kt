package com.xyx.landmark.ui.map

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.xyx.landmark.vo.COLLECTION
import com.xyx.landmark.vo.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val _allDocuments = MutableLiveData<List<DocumentSnapshot>>()
    val allNotes: LiveData<List<User.Note>> = _allDocuments.switchMap {
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(docs2Notes(it))
        }
    }
    private val _searchNotes = MutableLiveData<List<User.Note>>()
    val searchNotes: LiveData<List<User.Note>> = _searchNotes

    init {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION)
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    if (snapshot != null && !snapshot.isEmpty) {
                        _allDocuments.value = snapshot.documents
                    } else {
                        _allDocuments.value = emptyList()
                    }
                }
            }
    }

    @WorkerThread
    private fun docs2Notes(docs: List<DocumentSnapshot>): List<User.Note> {
        val notes = mutableListOf<User.Note>()
        for (doc in docs) {
            doc.toObject(User::class.java)?.let {
                it.notes?.run {
                    for (note in it.notes) {
                        note.apply {
                            uid = it.uid
                            name = it.name
                        }
                        notes.add(note)
                    }
                }
            }
        }
        return notes
    }

    fun search(keyword: String) {
        allNotes.value?.run {
            viewModelScope.launch(Dispatchers.Default) {
                val notes = mutableListOf<User.Note>()
                forEach { if (it.containsKeyword(keyword)) notes.add(it) }
                _searchNotes.postValue(notes)
            }
        }
    }

    private fun User.Note.containsKeyword(keyword: String): Boolean {
        return name?.contains(keyword) ?: false
                || content?.contains(keyword) ?: false
    }

}