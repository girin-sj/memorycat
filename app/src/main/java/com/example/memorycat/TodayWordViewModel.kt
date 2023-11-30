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
    //private val todayWordNames = mutableListOf<String>() //오늘의 단어들 10개. 해당 리스트 프레그먼트에서 못 사용해?
    val todayWordNames = MutableLiveData<MutableList<String>>()
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

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
        loadDate() //얘네 순서는 상관없나?
    }

    private fun loadLevel() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _level.value = document.getString("level")
                Log.d("TodayWordViewModel", "Level loaded: ${_level.value}")
                //makeTodayWordList() //이게 낫나
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

    //단어+뜻 가져오기 or 단어 가져온 후 이에 맞는 뜻 가져오기
    //인덱스로 움직여야 함.
    //전체 탐색 -> date가 맞는 단어들 list에 넣기
    private var dicIdx: Int = 0

    fun makeTodayWordList() { //MutableList<String>
        val todayWordNamesTemp = mutableListOf<String>()
        val levelDocumentRef =
            firestore.collection("englishDictionary").document(level.value!!) //level고려 //
        val dateInt = _date.value!!.toInt()
        Log.d("TodayWordViewModel", "make TodayWord") //여기까진 실행됨 log 뜸
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList() //단어이름들
                        var fieldName: String = "" //fieldNames: 단어 이름
                        var flag = 0
                        while (flag < 7) { //첫 7개
                            fieldName = fieldNames[dicIdx]
                            dicIdx++
                            val fieldValue = fieldMap[fieldName] as? Map<String, Any>
                            val dateGet = fieldValue?.get("date")?.toString() ?: "0"
                            if (dateGet.toInt() == dateInt) { //date 검사
                                todayWordNamesTemp.add(fieldName)
                                flag ++
                                Log.d("TodayWordViewModel", "list num: ${flag}, list: ${todayWordNamesTemp}")
                            }
                        }
                        //추가적 3개
                        if (_date.value!!.toInt() > 1){ //2~6째날의 경우
                            var append = 0
                            while(append < 3){
                                val randomFieldName = fieldNames.random()
                                val fieldValue = fieldMap[fieldName] as? Map<String, Any>
                                val dateGet = fieldValue?.get("date")?.toString() ?: "0"
                                if (dateGet.toInt() == dateInt) { //date 검사
                                    todayWordNamesTemp.add(randomFieldName)
                                    append++
                                    Log.d("TodayWordViewModel", "list num: ${append+7}, list: ${todayWordNamesTemp}")
                                }
                            }
                        } else{ //첫째날의 경우. 오늘꺼 3개 추가
                            var append = 0
                            while(append < 3){
                                fieldName = fieldNames[dicIdx]
                                dicIdx++
                                val fieldValue = fieldMap[fieldName] as? Map<String, Any>
                                val dateGet = fieldValue?.get("date")?.toString() ?: "0"
                                if (dateGet.toInt() == dateInt) { //date 검사
                                    todayWordNamesTemp.add(fieldName) //최종 list에 넣기
                                    append++
                                    Log.d("TodayWordViewModel", "list num: ${append+7}, list: ${todayWordNamesTemp}")
                                }
                            }
                        }
                        //todayWordNames.value = todayWordNamesTemp
                        todayWordNames.postValue(todayWordNamesTemp)
                    }
                } else {
                    Log.d("TodayWordViewModel", "some error")
                }
            }.addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting random word: $exception")
            }
        //return todayWordNames
    }

    //TodayWordStudyFragment에서 실행. 이거 필요 없을 수도?
    /*
    fun getTodayWord(word_idx: Int) : MutableLiveData<String>{ //이상
        Log.d("TodayWordViewModel", "list: ${todayWordNames}") //list내용 남아있는지 확인 기능 - 왜 여기 안남아있지?
        _todayWord.value = todayWordNames[word_idx]
        Log.d("TodayWordViewModel", "word: ${_todayWord.value}") //단어
        return _todayWord
    }

     */

    fun getMeanings(word: String) { //MutableLiveData<String> //고치자
        val levelDocumentRef =
            firestore.collection("englishDictionary").document(level.value!!) //level고려

        Log.d("TodayWordViewModel", "get meanings")
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
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