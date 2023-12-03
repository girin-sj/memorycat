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
        val levelDocumentRef = repo.firestore.collection("englishDictionary").document(level.value!!)
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

    //북마크
    fun updateBookmarkResult( //우선 뜻까지 받자
        word: String,
        mean1: String,
        mean2: String,
        mean3: String,
        isSelect: String
    ) {
        repo.recentbDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                repo.recentbDB.update(word, mapOf(
                    "mean1" to mean1,
                    "mean2" to mean2,
                    "mean3" to mean3,
                    "isSelect" to isSelect
                ))
                Log.d("TodayWordViewModel", "updateBookmarkResult- ${word}, ${mean1}, ${mean2}, ${mean3}, ${isSelect}")
            }
        }
    }

    fun loadBookmarkResult(): LiveData<List<BookmarkResult>> {
        val liveData = MutableLiveData<List<BookmarkResult>>()
        repo.recentbDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val bookmarkResults = mutableListOf<BookmarkResult>()
                val document = task.result
                document?.data?.forEach { entry ->
                    val word = entry.key
                    val value = entry.value as Map<String, String>
                    val mean1 = value["mean1"] ?: ""
                    val mean2 = value["mean2"] ?: ""
                    val mean3 = value["mean3"] ?: ""
                    val isSelect = value["isSelect"] ?: ""
                    bookmarkResults.add(BookmarkResult(word, mean1, mean2, mean3, isSelect))
                    Log.d("TodayWordViewModel", "loadBookmarkResult: word: ${word}, mean1: ${mean1}, mean2: ${mean2}, mean3: ${mean3}, isSelect: ${isSelect} X}")

                }
                liveData.postValue(bookmarkResults)
            }
        }
        return liveData
    }
    fun checkSelect(word: String, callback: (Boolean) -> Unit) {
        //DB에 해당 단어 있는지 -> X(이거나 없으면) false, O이면 true
        repo.recentbDB.get()
            .addOnSuccessListener { document ->
                val fieldMap = document?.data
                val fieldValue = fieldMap?.get("$word.isSelect") as? String
                val isSelect = fieldValue == "O"
                Log.d("TodayWordViewModel", "checkSelect: $isSelect")

                // 콜백으로 결과 전달
                callback(isSelect)
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error checking select: $exception")

                // 에러 발생 시 기본값으로 false 전달
                callback(false)
            }
    } //callback을 통해 비동기적으로 결과를 전달 -> 호출하는 부분에서도 콜백 함수를 사용하여 결과를 처리
    /*
    fun checkSelect(word: String): Boolean {
        val document = repo.recentbDB.get().await() // get() 메서드로 데이터 가져오기
        val isSelect = document.getString("$word.isSelect")
        Log.d("TodayWordViewModel", "checkSelect: $isSelect")
        return isSelect == "O"
    }

    fun checkSelect(word: String): Boolean { //북마크 안되어있는데 왜 되어있다고 판단하는거야
        var isSelect: String = ""
        //DB에 해당 단어 있는지 -> X(이거나 없으)면 false, O이면 true -> 일단 bookmarkDB에 전부 X로 넣어놓음 ->
        repo.recentbDB.get().addOnSuccessListener { document ->
            if (document != null) {
                val fieldMap = document.data

                val fieldValue = fieldMap?.get(word) as? Map<String, String>
                isSelect = fieldValue?.get("isSelect").toString()
                Log.d("TodayWordViewModel", "isSelect 판단: ${isSelect}")

            }
        }

     */
}
