package com.example.memorycat.Repository
//데이터를 가져오고 저장하는 역할

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.BookmarkResult
import com.google.firebase.firestore.FieldValue

class TodaywordRepository : ViewModel() {

    val todayWordNames = MutableLiveData<MutableList<String>>()
    private val todayWordNamesTemp = mutableListOf<String>()
    private val _todayWord = MutableLiveData<String>()
    val todayWord: LiveData<String> get() = _todayWord
    private val _meanings = MutableLiveData<List<String>>()

    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _date = MutableLiveData<String>()
    private var dicIdx: Int = 0
    private val repo: Repository = Repository()

    init {
        // 모든 작업 이전에 level과 date를 load 필요
        loadLevel()
        loadDate()
    }

    // 사용자 레벨 load
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

    //오늘의 영단어에서 사용될 오늘의 date load
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

    //db에서 조건에 맞는 단어들로 오늘의 영단어 배열 만들기
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
                                document.get(fieldName) as MutableList<String> //해당 단어의 데이터 가져오기
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
                            while (append < 3) {
                                fieldName = fieldNames[dicIdx]
                                dicIdx = (0..44).random() //45개의 단어 중 랜덤으로

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

                                if (dateGet.toInt() == dateInt) { //date가 1인 단어들
                                    todayWordNamesTemp.add(fieldName)
                                    append++
                                    Log.d(
                                        "TodayWordViewModel",
                                        "list: ${todayWordNamesTemp}"
                                    )
                                }
                            }
                        }
                        todayWordNames.postValue(todayWordNamesTemp) // LiveData 업데이트
                        //LiveData: 앱의 수명 주기 인식, 활성 상태일 때만 데이터 변경 사항 알림
                        // -> 메모리 누수 및 예기치 못한 동작 방지 & 앱 컴포넌트 간의 데이터 흐름을 쉽게 관리
                        Log.d("TodayWordViewModel", "making list end")
                    }
                } else {
                    Log.d("TodayWordViewModel", "some error")
                }
            }.addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting random word: $exception")
            }
    }
    //위에서 만든 list 활용하여 (인덱스로) 오늘의 단어 get
    fun getTodayWord(idx: Int): MutableLiveData<String> {
        _todayWord.value = todayWordNamesTemp[idx]
        return _todayWord
    }
    //화면에 표시되는 단어의 뜻 get
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
    //북마크 db 업데이트
    fun updateBookmarkResult(
        word: String,
        mean1: String,
        mean2: String,
        mean3: String,
        isSelect: String
    ) {
        repo.bookmarkDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                repo.bookmarkDB.update(
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
    //북마크에 정보 load
    fun loadSelectedBookmarks(callback: (List<BookmarkResult>) -> Unit) { //북마크 화면에 뜨우기위해 "O"인 단어 리스트에 넣기
        repo.bookmarkDB.get()
            .addOnSuccessListener { document ->
                val bookmarkResults = mutableListOf<BookmarkResult>()
                val fieldMap = document?.data
                //문서의 모든 data 반복해서 처리
                fieldMap?.forEach { entry ->
                    val word = entry.key
                    val value = entry.value as Map<String, String>
                    val mean1 = value["mean1"] ?: ""
                    val mean2 = value["mean2"] ?: ""
                    val mean3 = value["mean3"] ?: ""
                    val isSelect = value["isSelect"] ?: ""
                    if (isSelect == "O") { //북마크가 되어있는 단어들만 BookmarkResult 객체의 리스트로 변환
                        bookmarkResults.add(BookmarkResult(word, mean1, mean2, mean3, isSelect))
                    }
                }
                callback(bookmarkResults) //콜백 함수 -> 전달
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error loading selected bookmarks: $exception")
                callback(emptyList())
            }
    }
    fun removeBookmark(word: String) { //북마크 화면에서 삭제하기 = update
        repo.bookmarkDB.update(mapOf(word to FieldValue.delete()))
    }

    fun checkSelect(word: String, callback: (Boolean) -> Unit) {
        //DB에 해당 단어 있는지 -> X(이거나 없으면) false, O이면 true
        repo.bookmarkDB.get()
            .addOnSuccessListener { document ->
                val fieldMap = document?.data
                val isSelect: Boolean
                val fieldValue = fieldMap?.get(word) as? Map<String, String>
                val isSelect_str = fieldValue?.get("isSelect").toString()
                isSelect = isSelect_str == "O"

                Log.d(
                    "TodayWordViewModel",
                    "checkSelect: $isSelect, fieldValue: ${fieldValue}"
                )
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