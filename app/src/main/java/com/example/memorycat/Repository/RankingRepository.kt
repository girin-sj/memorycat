package com.example.memorycat.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memorycat.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RankingRepository:ViewModel() {
    var _correctCount = MutableLiveData<String>()
    val correctCount: LiveData<String> get() = _correctCount
    var userlist: MutableMap<String, String> = HashMap()
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name
    val repo: Repository = Repository()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            countCorrectcount()
            getUidDocument()
        }
    }

    fun getUidDocument(): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()
        repo.firestore.collection("userDB").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val tempList =
                    mutableListOf<Pair<String, String>>()
                val usersForRecycler = mutableListOf<User>()

                for (document in task.result) {
                    getCorrectCount(document.id) { count ->
                        tempList.add(document.id to count)

                        if (tempList.size == task.result.size()) {
                            userlist = tempList.toMap().toMutableMap()
                        }

                        // 데이터를 가져온 후에 정렬하고 정렬된 목록을 LiveData에 추가
                        if (tempList.size == task.result.size()) {
                            val sortedUserlist =
                                userlist.toList().sortedByDescending { it.second.toInt() }.toMap().toMutableMap()
                            sortedUserlist.keys.forEachIndexed { index, key ->
                                repo.firestore.collection("userDB").document(key).get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            _name.value = document.getString("nickname")
                                            _correctCount.value = document.getString("correctCount")
                                            _level.value = document.getString("level")
                                            val user = User(
                                                "${_name.value}",
                                                "Lv. ${_level.value}",
                                                "누적 ${_correctCount.value}개",
                                                "${index + 1}등"
                                            )
                                            usersForRecycler.add(user)


                                            if (usersForRecycler.size == sortedUserlist.size) {
                                                Log.d("RankingViewModel", "usersForRecycler: $usersForRecycler")
                                                liveData.postValue(usersForRecycler)
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
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