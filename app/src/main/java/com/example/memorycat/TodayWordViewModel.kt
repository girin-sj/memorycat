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
    private val todayWordNames = mutableListOf<String>()

    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _date = MutableLiveData<Double>()
    val date: LiveData<Double> get() = _date

    private val _todayWord = MutableLiveData<String>()
    val todayWord: LiveData<String> get() = _todayWord

    private val _means1: MutableLiveData<String> = MutableLiveData()
    val means1: LiveData<String> get() = _means1
    private val _means2: MutableLiveData<String> = MutableLiveData()
    val means2: LiveData<String> get() = _means2
    private val _means3: MutableLiveData<String> = MutableLiveData()
    val means3: LiveData<String> get() = _means3


    init {
        loadLevel()
        loadDate()
        makeTodayWordList()
    }

    private fun loadLevel() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _level.value = document.getString("level")
                Log.d("TodayWordViewModel", "Level loaded: ${_level.value}")
                getTodayWord(0) // level이 로드된 후에 loadDate() 호출
                //makeTodayWordList()
            } else {
                Log.d("TodayWordViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("TodayWordViewModel", "Error getting document: $exception")
        }
    }

    private fun loadDate() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _date.value = document.getDouble("date")
                Log.d("TodayWordViewModel", "Date loaded: ${_date.value}")
                //getTodayWord() // date이 로드된 후에 getTodayWord() 호출 -> 해당 레벨과 date에 맞는 단어 호출
            } else {
                Log.d("TodayWordViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("TodayWordViewModel", "Error getting document: $exception")
        }
    }

    //단어+뜻 가져오기 or 단어 가져온 후 이에 맞는 뜻 가져오기
    //인덱스로 움직여야 함.
    //전체 탐색 -> date가 맞는 단어들 list에 넣기
    private var dic_idx: Int = 0

    //TodayWordStartFragment에서 실행
    fun makeTodayWordList(): MutableList<String> {
        val levelDocumentRef =
            firestore.collection("englishDictionary").document(level.value!!) //level고려

        Log.d("TodayWordViewModel", "get word")
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data //
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList() //
                        var fieldName: String = "" //fieldNames: 단어 이름
                        var flag = 0
                        while (flag == 0) {
                            fieldName = fieldNames[dic_idx]
                            dic_idx++
                            val meaningsMap = fieldMap[fieldName] as? Map<String, Double>
                            val wordDate = meaningsMap?.get("date")
                            if (wordDate == _date.value) { //date가 맞으면
                                todayWordNames.add(fieldName) //최종 list에 넣기
                                Log.d("TodayWordViewModel", "New word: ${_todayWord.value}")
                            } else if (wordDate!! > _date.value!!) flag = 1
                        }
                        if (_date.value!!.toInt() > 1){ //이전에 공부했던 단어들 중에 3개 추가
                            var append = 0
                            while(append < 3){
                                val randomFieldName = fieldNames.random()
                                val meaningsMap = fieldMap[fieldName] as? Map<String, Double>
                                if (meaningsMap?.get("date")!! < _date.value!!) todayWordNames.add(randomFieldName)
                                append++
                            }

                        }
                    }
                } else {
                    Log.d("QuizViewModel", "some error")
                }
            }.addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Error getting random word: $exception")
            }
        return todayWordNames
    }

    //TodayWordStudyFragment에서 실행
    fun getTodayWord(word_idx: Int) : String { //내꺼 복붙
        _todayWord.value = todayWordNames[word_idx]
        return todayWordNames[word_idx]
    }

    fun getMeanings(word: String) { //단어를 파라미터로 주면 dictionary에서 뜻 찾아서 알려줌.
        val levelDocumentRef =
            firestore.collection("englishDictionary").document(level.value!!) //level고려

        Log.d("TodayWordViewModel", "get meanings")
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data //
                    if (fieldMap != null) {
                        val fieldName = word //fieldNames: 단어 이름

                        val meaningsMap = fieldMap[fieldName] as? Map<String, String>
                        if (!meaningsMap.isNullOrEmpty()) {
                            _means1.value = meaningsMap["mean1"] ?: ""
                            _means2.value = meaningsMap["mean2"] ?: ""
                            _means3.value = meaningsMap["mean3"] ?: ""
                        }
                    }
                } else {
                    Log.d("TodayWordViewModel", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting document: $exception")
            }
    }//getMeanings()

}