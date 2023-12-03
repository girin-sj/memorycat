package com.example.memorycat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.Repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RankingViewModel : ViewModel() {
    val repo: Repository = Repository()

    var correctCount = MutableLiveData<String>()
    var userlist: MutableMap<String, String> = HashMap()
    private val _level = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()

    init {
        countCorrectcount()
        getUidDocument()
    }

    fun getUidDocument(): LiveData<List<User>> {
        var grade = 0
        val liveData = MutableLiveData<List<User>>()
        repo.firestore.collection("userDB").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val tempList = mutableListOf<Pair<String, String>>() // Temporary list to hold user data
                val usersForRecycler = mutableListOf<User>() // Create a local list to hold users

                for (document in task.result) {
                    getCorrectCount(document.id) { count ->
                        tempList.add(document.id to count)

                        if (tempList.size == task.result.size()) {
                            userlist = tempList.toMap().toMutableMap()
                            userlist = userlist.toList().sortedByDescending { it.second }.toMap().toMutableMap()

                            for ((key) in userlist) {
                                repo.firestore.collection("userDB").document(key).get().addOnSuccessListener { document ->
                                    if (document != null) {
                                        _name.value = document.getString("nickname")
                                        correctCount.value = document.getString("correctCount")
                                        _level.value = document.getString("level")
                                        val user = User(
                                            "${_name.value}",
                                            "Lv. ${_level.value}",
                                            "Score ${correctCount.value}/10",
                                            "${++grade}등"
                                        )
                                        Log.d("RankingViewModel", "grade: $grade")
                                        usersForRecycler.add(user) // Add user to local list
                                    }

                                    if (usersForRecycler.size == userlist.size) {
                                        liveData.postValue(usersForRecycler) // Publish the updated list once all users are added
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Log.d("RankingViewModel", "Error getting documents: ", task.exception)
            }
        }
        return liveData
    }



    fun getCorrectCount(uid: String, callback: (String) -> Unit) {
        repo.firestore.collection("userDB").document(uid).get().addOnSuccessListener { document ->
            val count = document.getString("correctCount") ?: "0"
            callback(count)
        }.addOnFailureListener { exception ->
            Log.e("RankingViewModel", "Error getting document: $exception")
        }
    }

    fun updateCorrectCount(correctCount: String) {
        repo.userDB.update("correctCount", correctCount).addOnSuccessListener {
            // Successfully updated correct count
        }.addOnFailureListener { exception ->
            Log.e("RankingViewModel", "Error updating correct count: $exception")
        }
    }

    fun countCorrectcount() {
        repo.accureDB.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data
                var correctcount = 0
                data?.forEach { (_, value) ->
                    if (value == "O") {
                        correctcount++
                    }
                }
                updateCorrectCount(correctcount.toString())
            } else {
                Log.d("RankingViewModel", "Document does not exist.")
            }
        }.addOnFailureListener { exception ->
            Log.e("RankingViewModel", "Error getting data: $exception")
        }
    }
}