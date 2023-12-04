package com.example.memorycat.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.BookmarkResult
import com.example.memorycat.Repository.Repository_yjw

class TodayWordViewModel: ViewModel() {
    val todayWordNames = MutableLiveData<MutableList<String>>()
    private val todayWordNamesTemp = mutableListOf<String>()
    private val _todayWord = MutableLiveData<String>()
    val todayWord: LiveData<String> get() = _todayWord
    private val _meanings = MutableLiveData<List<String>>()

    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _date = MutableLiveData<String>()
    private var dicIdx: Int = 0
    val repo: Repository_yjw = Repository_yjw()


    init {
        loadLevel()
        loadDate()
    }

    private fun loadLevel() {
        repo.userDB.get().addOnSuccessListener { document ->
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
        repo.userDB.get().addOnSuccessListener { document ->
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
            repo.firestore.collection("englishDictionary").document(level.value!!)
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
                                        "TodayWordViewModel", "${dateGet.toInt()} < ${dateInt}"
                                    )
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
        val meanings = mutableListOf<String>()
        val dictionaryCollection = repo.firestore.collection("englishDictionary")

        dictionaryCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.contains(word)) {
                        val documentMeanings = document.get(word) as? MutableList<String>
                        documentMeanings?.let {
                            meanings.addAll(it)
                        }
                    }
                }
                _meanings.value = meanings
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting meanings: $exception")
            }
        return _meanings
    }

    fun updateBookmarkResult( //북마크 db 업데이트 시 사용
        word: String,
        mean1: String,
        mean2: String,
        mean3: String,
        isSelect: String
    ) {
        repo.recentbDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                repo.recentbDB.update(
                    word, mapOf(
                        "mean1" to mean1,
                        "mean2" to mean2,
                        "mean3" to mean3,
                        "isSelect" to isSelect
                    )
                )
                Log.d(
                    "TodayWordViewModel",
                    "updateBookmarkResult- ${word}, ${mean1}, ${mean2}, ${mean3}, ${isSelect}"
                )
            }
        }
    }
    fun loadSelectedBookmarks(callback: (List<BookmarkResult>) -> Unit) {
        repo.recentbDB.get()
            .addOnSuccessListener { document ->
                val bookmarkResults = mutableListOf<BookmarkResult>()
                val fieldMap = document?.data

                fieldMap?.forEach { entry ->
                    val word = entry.key
                    val value = entry.value as Map<String, String>
                    val mean1 = value["mean1"] ?: ""
                    val mean2 = value["mean2"] ?: ""
                    val mean3 = value["mean3"] ?: ""
                    val isSelect = value["isSelect"] ?: ""

                    if (isSelect == "O") {
                        bookmarkResults.add(BookmarkResult(word, mean1, mean2, mean3, isSelect))
                    }
                }
                callback(bookmarkResults)
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error loading selected bookmarks: $exception")
                callback(emptyList())
            }
    }

    fun checkSelect(word: String, callback: (Boolean) -> Unit) {
        //DB에 해당 단어 있는지 -> X(이거나 없으면) false, O이면 true
        repo.recentbDB.get()
            .addOnSuccessListener { document ->
                val fieldMap = document?.data
                val isSelect: Boolean
                val fieldValue = fieldMap?.get(word) as? Map<String, String>
                val isSelect_str = fieldValue?.get("isSelect").toString()
                isSelect = isSelect_str == "O"

                Log.d(
                    "TodayWordViewModel",
                    "checkSelect: $isSelect, fieldValue: ${fieldValue}"
                ) //change때는 잘 된다. 처음 get할때 안된다.
                // 콜백으로 결과 전달
                callback(isSelect)
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error checking select: $exception")

                // 에러 발생 시 기본값으로 false 전달
                callback(false)
            }
    } //callback을 통해 비동기적으로 결과를 전달 -> 호출하는 부분에서도 콜백 함수를 사용하여 결과를 처리
}
