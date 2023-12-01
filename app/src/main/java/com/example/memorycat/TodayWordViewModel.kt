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
    val meanings = MutableLiveData<MutableList<String>>() //date 가져올때 사용
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _todayWord = MutableLiveData<String>()
    val todayWord: LiveData<String> get() = _todayWord

    //이렇게 mutablelist로 가져오기
    private val _meanings = MutableLiveData<List<String>>() //실제 단어들 가져올떄 사용
    val meanings_1: LiveData<List<String>> get() = _meanings //확인해보려고 만든 변수


    /*
    private val _means1: MutableLiveData<String> = MutableLiveData()
    val means1: LiveData<String> get() = _means1
    private val _means2: MutableLiveData<String> = MutableLiveData()
    val means2: LiveData<String> get() = _means2
    private val _means3: MutableLiveData<String> = MutableLiveData()
    val means3: LiveData<String> get() = _means3

     */

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
            firestore.collection("englishDictionary").document(level.value!!) 
        val dateInt = _date.value!!.toInt()
        Log.d("TodayWordViewModel", "make TodayWord") //OK
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList() //단어이름들
                        Log.d("TodayWordViewModel", "fieldNames: ${fieldNames}") //다 들어가긴 하네 ok
                        var fieldName: String = "" //fieldNames: 단어 이름
                        var flag = 0
                        while (flag < 7) { //첫 7개
                            fieldName = fieldNames[dicIdx]
                            Log.d("TodayWordViewModel", "dicIdx: ${dicIdx}, fieldName: ${fieldName}")
                            dicIdx++
                            /*
                            val fieldValue = fieldMap[fieldName] as? Map<String, Any>
                            val dateGet = fieldValue?.get("date")?.toString() ?: "0"
                            */
                            val words = document.get(fieldName) as MutableList<String> //여기에 date 존재하니 index 1부터 가져오면 됨. //WORD 확인해보기 //MAP //GET으로 가져오고 타입이 ㅁ뭔지 확인하기
                            Log.d("TodayWordViewModel", "meanings: ${words}")
                            val dateGet = words[0]
                            Log.d("TodayWordViewModel", "dateGet: ${dateGet}, dateInt: ${dateInt}, ${dateGet.toInt() == dateInt}")

                            if (dateGet.toInt() == dateInt) { //제대로 작동함
                                todayWordNamesTemp.add(fieldName)
                                flag ++
                                Log.d("TodayWordViewModel", "list7 num: ${flag}, list: ${todayWordNamesTemp}")
                            }
                        }
                        //추가적 3개
                        if (_date.value!!.toInt() > 1){ //2~6째날의 경우
                            var append = 0
                            while(append < 3){
                                val randomFieldName = fieldNames.random()
                                val words = document.get(fieldName) as MutableList<String> //여기에 date 존재하니 index 1부터 가져오면 됨. //WORD 확인해보기 //MAP //GET으로 가져오고 타입이 ㅁ뭔지 확인하기
                                Log.d("TodayWordViewModel", "meanings: ${words}")
                                val dateGet = words[0]
                                Log.d("TodayWordViewModel", "dateGet: ${dateGet}")

                                if (dateGet.toInt() > dateInt) { //date 검사
                                    todayWordNamesTemp.add(randomFieldName)
                                    append++
                                    Log.d("TodayWordViewModel", "list3-1 num: ${append+7}, list: ${todayWordNamesTemp}")
                                }
                            }
                        } else{ //첫째날의 경우. 오늘꺼 3개 추가
                            var append = 0
                            while(append < 3){
                                fieldName = fieldNames[dicIdx]
                                dicIdx++
                                val words = document.get(fieldName) as MutableList<String> //여기에 date 존재하니 index 1부터 가져오면 됨. //WORD 확인해보기 //MAP //GET으로 가져오고 타입이 ㅁ뭔지 확인하기
                                Log.d("TodayWordViewModel", "meanings: ${words}")
                                val dateGet = words[0]
                                Log.d("TodayWordViewModel", "dateGet: ${dateGet}")

                                if (dateGet.toInt() == dateInt) { //date 검사
                                    todayWordNamesTemp.add(fieldName) //최종 list에 넣기
                                    append++
                                    Log.d("TodayWordViewModel", "list3-2 num: ${append+7}, list: ${todayWordNamesTemp}")
                                }
                            }
                        }
                        todayWordNames.postValue(todayWordNamesTemp)
                    }
                } else {
                    Log.d("TodayWordViewModel", "some error")
                }
            }.addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting random word: $exception")
            }
    }


    fun getMeanings(word: String): MutableLiveData<List<String>> {
        val levelDocument = firestore.collection("englishDictionaryDB").document(level.value!!)

        levelDocument.get() //여기서 failure가 되어버리네
            .addOnSuccessListener { document ->
                if (document != null) {
                    val meanings = document.get(word) as MutableList<String> //여기에 date 존재하니 index 1부터 가져오면 됨. //WORD 확인해보기 //MAP //GET으로 가져오고 타입이 ㅁ뭔지 확인하기
                    _meanings.value = meanings
                    Log.e("TodayWordViewModel", "${meanings}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordViewModel", "Error getting meanings: $exception")
            }

        return _meanings
    }
}