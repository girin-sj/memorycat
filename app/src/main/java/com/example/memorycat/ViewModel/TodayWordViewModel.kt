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
    val todayWordNamesTemp = mutableListOf<String>() //이 내용이 저장이 되나..?
    //단어 하나씩 가져와서. getTodayWord 만들면 그대로, 그냥 study에서 넣어주려면 string으로 바꿔야 함.
    private val _todayWord = MutableLiveData<String>()
    val todayWord: LiveData<String> get() = _todayWord
    //이렇게 mutablelist로 가져오기
    private val _meanings = MutableLiveData<List<String>>() //서영

    //레벨 가져오기
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    //1~6일 가져오기
    private val _date = MutableLiveData<String>()
    //val date: LiveData<String> get() = _date
    private var dicIdx: Int = 0

    init {
        loadLevel()
        loadDate() //얘네 순서는 상관없나?
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

    fun makeTodayWordList() { //MutableList<String>
        //val todayWordNamesTemp = mutableListOf<String>()
        val levelDocumentRef =
            firestore.collection("englishDictionary").document(level.value!!)
        val dateInt = _date.value!!.toInt()
        Log.d("TodayWordViewModel", "makeTodayWordList()") //OK
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList() //단어이름들
                        Log.d("TodayWordViewModel", "fieldNames: ${fieldNames}")
                        var fieldName: String = "" //fieldNames: 단어 이름들로 이루어진 list
                        var flag = 0
                        while (flag < 7) { //첫 7개
                            fieldName = fieldNames[dicIdx]
                            Log.d(
                                "TodayWordViewModel",
                                "dicIdx: ${dicIdx}, fieldName: ${fieldName}"
                            )
                            dicIdx++

                            val meanings =
                                document.get(fieldName) as MutableList<String>
                            Log.d("TodayWordViewModel", "meanings: ${meanings}") //
                            val dateGet = meanings[0] //array형식. index 0에 date 있음
                            Log.d(
                                "TodayWordViewModel",
                                "dateGet: ${dateGet}, dateInt: ${dateInt}"
                            )
                            if (dateGet.toInt() == dateInt) {
                                todayWordNamesTemp.add(fieldName) //조건에 맞는 단어 list에 넣기
                                flag++
                                Log.d(
                                    "TodayWordViewModel",
                                    "list7 num: ${flag}, list: ${todayWordNamesTemp}"
                                )
                            }
                        }
                        //추가적 3개
                        if (_date.value!!.toInt() > 1) { //2~6째날의 경우
                            var append = 0
                            while (append < 3) {
                                val randomFieldName = fieldNames.random()
                                val meanings =
                                    document.get(fieldName) as MutableList<String> //여기에 date 존재하니 index 1부터 가져오면 됨. //WORD 확인해보기 //MAP //GET으로 가져오고 타입이 ㅁ뭔지 확인하기
                                Log.d("TodayWordViewModel", "meanings: ${meanings}")
                                val dateGet = meanings[0]
                                Log.d(
                                    "TodayWordViewModel",
                                    "dateGet: ${dateGet}, dateInt: ${dateInt}, ${dateGet.toInt() == dateInt}"
                                )

                                if (dateGet.toInt() > dateInt) { //오늘보다 이전 날에 공부했던 단어들
                                    todayWordNamesTemp.add(randomFieldName)
                                    append++
                                    Log.d(
                                        "TodayWordViewModel",
                                        "list3-1 num: ${append + 7}, list: ${todayWordNamesTemp}"
                                    )
                                }
                            }
                        } else { //첫째날의 경우. 오늘꺼 3개 추가
                            var append = 0
                            while (append < 3) {
                                fieldName = fieldNames[dicIdx]
                                dicIdx++
                                val meanings =
                                    document.get(fieldName) as MutableList<String> //여기에 date 존재하니 index 1부터 가져오면 됨. //WORD 확인해보기 //MAP //GET으로 가져오고 타입이 ㅁ뭔지 확인하기
                                Log.d("TodayWordViewModel", "meanings**: ${meanings}")
                                val dateGet = meanings[0]
                                Log.d(
                                    "TodayWordViewModel",
                                    "dateGet: ${dateGet}, dateInt: ${dateInt}, ${dateGet.toInt() == dateInt}"
                                )

                                if (dateGet.toInt() == dateInt) { //오늘꺼 3개 추가
                                    todayWordNamesTemp.add(fieldName) //최종 list에 넣기
                                    append++
                                    Log.d(
                                        "TodayWordViewModel",
                                        "list3-2 num: ${append + 7}, list: ${todayWordNamesTemp}"
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
    } // 완성

    //위에서 만든 리스트를 인덱스로 접근해서 요소들 빼오기!!불가
    //수명 주기 고려 -> 프레그먼트 이동시 저장 뷰 모델 죽어버림. 전역변수로 내용 옮기지 않는 한.
    fun getTodayWord(idx: Int): MutableLiveData<String> { //여기서 인덱스 받아서 해당 단어 todayWord 반환해주어야 함.
        Log.d("TodayWordViewModel", "getTodayWord()")
        _todayWord.value = todayWordNamesTemp[idx]
        return _todayWord
    }

    fun getMeanings(word: String): MutableLiveData<List<String>> { //MutableList<String>
        val levelDocumentRef =
            firestore.collection("englishDictionary").document(level.value!!)
        Log.d("TodayWordViewModel", "get meanings()")
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data //사실 fieldMap도 필요 없음
                    if (fieldMap != null) {
                        _todayWord.value = word // 파라미터로 얻어 단어 이렇게 변수게 넣어도 되나?
                        val meanings = document.get(word) as MutableList<String>
                        Log.d("TodayWordViewModel", "getMeanings - meanings: ${meanings}")
                        _meanings.value = meanings  //서영이 코드와 유사
                        Log.d("TodayWordViewModel", "get Meanings end")
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting random word: $exception")
            }
        Log.d("TodayWordViewModel", "return _meanings")
        return _meanings //서영이가 이렇게 반환함.
    }
}