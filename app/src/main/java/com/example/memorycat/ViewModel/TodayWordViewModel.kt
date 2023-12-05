package com.example.memorycat.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TodayWordViewModel: ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val userDB = firestore.collection("userDB").document(uid!!)

    //리스트 만들고
    val todayWordNames = MutableLiveData<MutableList<String>>()
    val todayWordNamesTemp = mutableListOf<String>()
    private val _todayWord = MutableLiveData<String>()
    val todayWord: LiveData<String> get() = _todayWord
    private val _meanings = MutableLiveData<List<String>>()

    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _date = MutableLiveData<String>()
    private var dicIdx: Int = 0

    init {
        loadLevel()
        loadDate()
    }

    private fun loadLevel() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _level.value = document.getString("level")
                Log.d("TodayWordViewModel", "Level loaded: ${_level.value}")
            } else {
                Log.d("TodayWordViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("TodayWordViewModel", "Error getting document: $exception")
        }
    }

    fun loadDate() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _date.value = document.getString("date")
                Log.d("TodayWordViewModel", "Date loaded: ${_date.value}")
            } else {
                Log.d("TodayWordViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("TodayWordViewModel", "Error getting document: $exception")
        }
    }

    fun makeTodayWordList() {
        val levelDocumentRef =
            firestore.collection("englishDictionary").document(level.value!!)
        val dateInt = _date.value!!.toInt()
        Log.d("TodayWordViewModel", "makeTodayWordList()")
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList()
                        Log.d("TodayWordViewModel", "fieldNames: ${fieldNames}")
                        var fieldName: String = ""
                        var flag = 0
                        while (flag < 7) { //첫 7개
                            fieldName = fieldNames[dicIdx]
                            dicIdx++

                            val meanings =
                                document.get(fieldName) as MutableList<String>
                            Log.d("TodayWordViewModel", "meanings: ${meanings}")
                            val dateGet = meanings[0] //array형식. index 0에 date 있음

                            if (dateGet.toInt() == dateInt) {
                                todayWordNamesTemp.add(fieldName) //조건에 맞는 단어 list에 넣기
                                flag++
                                Log.d(
                                    "TodayWordViewModel",
                                    "list: ${todayWordNamesTemp}"
                                )
                            }
                        }
                        //추가적 3개
                        if (_date.value!!.toInt() > 1) { //2~6째날
                            dicIdx = 0
                            var append = 0
                            while (append < 3) { //무한 loop
                                fieldName = fieldNames[dicIdx]
                                dicIdx = (0..44).random()

                                val meanings =
                                    document.get(fieldName) as MutableList<String>
                                val dateGet = meanings[0] //array형식. index 0에 date 있음

                                if (dateGet.toInt() < dateInt) { //오늘보다 이전 날에 공부했던 단어들
                                    Log.d(
                                        "TodayWordViewModel","${dateGet.toInt()} < ${dateInt}")
                                    todayWordNamesTemp.add(fieldName)
                                    append++
                                    Log.d(
                                        "TodayWordViewModel",
                                        "list: ${todayWordNamesTemp}"
                                    )
                                }
                            }
                        } else { //첫째날
                            var append = 0
                            while (append < 3) {
                                fieldName = fieldNames[dicIdx]
                                dicIdx++
                                val meanings =
                                    document.get(fieldName) as MutableList<String>
                                val dateGet = meanings[0]

                                if (dateGet.toInt() == dateInt) {
                                    todayWordNamesTemp.add(fieldName)
                                    append++
                                    Log.d(
                                        "TodayWordViewModel",
                                        "list: ${todayWordNamesTemp}"
                                    )
                                }
                            }
                        }
                        todayWordNames.postValue(todayWordNamesTemp)
                        Log.d("TodayWordViewModel", "making list end")
                    }
                } else {
                    Log.d("TodayWordViewModel", "some error")
                }
            }.addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting random word: $exception")
            }
    }

    fun getTodayWord(idx: Int): MutableLiveData<String> {
        _todayWord.value = todayWordNamesTemp[idx]
        return _todayWord
    }

    fun getMeanings(word: String): MutableLiveData<List<String>> {
        val levelDocumentRef =
            firestore.collection("englishDictionary").document(level.value!!)
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
                    if (fieldMap != null) {
                        _todayWord.value = word
                        val meanings = document.get(word) as MutableList<String>
                        _meanings.value = meanings
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting random word: $exception")
            }
        return _meanings
    }
}