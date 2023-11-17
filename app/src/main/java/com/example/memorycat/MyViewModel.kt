package com.example.memorycat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyViewModel : ViewModel() {
    // 가져올 데이터
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val uid : String? = FirebaseAuth.getInstance().currentUser?.uid
    val userDB = firestore.collection("userDB").document(uid!!)
    val usedFieldNames = mutableListOf<String>()

    init {
        loadLevel()
    }

    fun loadLevel() : String? {
        var userLevel: String? = null
        userDB.get().addOnSuccessListener {
                document ->
            if (document != null) {
                userLevel = document.getString("level")
                Log.d("QuizStartFragment", "$userLevel")
            }
            else {
                Log.d("QuizStartFragment", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("QuizStartFragment", "Error getting document: $exception")
        }
        return userLevel
    }

    fun getWords() {
        val level = loadLevel()
        val englishDictionaryCollection =
            firestore.collection("englishDictionary").document(level!!)
        englishDictionaryCollection.get()
            .addOnSuccessListener { dictionaryDocument ->
                if (dictionaryDocument != null) {
                    val fieldMap = dictionaryDocument.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList() // Map 형식의 단어 data field의 key
                        var availableFieldNames = mutableListOf<String>()
                        usedFieldNames.forEach{
                            var searchWord = it
                            var res = fieldNames.find { it==searchWord }
                            if(res!=null) availableFieldNames.add(res)
                        }

                        if (availableFieldNames.isNotEmpty()) {
                            val randomFieldName = availableFieldNames.random() // 단어 랜덤 추출
                            usedFieldNames.add(randomFieldName)
                        }
                    }
                } else {
                    Log.d("QuizMainFragment", "Document does not exist")
                }
            }
    }

    /*
    private val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also {
            loadUsers()
        }
    }

    fun getUsers(): LiveData<List<User>> {
        return users
    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }
   */
}