
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class QuizViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val userDB = firestore.collection("userDB").document(uid!!)
    private val usedFieldNames = mutableListOf<String>()
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _words: MutableLiveData<String> = MutableLiveData()
    val words: LiveData<String> get() = _words
    private val _means1: MutableLiveData<String> = MutableLiveData()
    val means1: LiveData<String> get() = _means1
    private val _means2: MutableLiveData<String> = MutableLiveData()
    val means2: LiveData<String> get() = _means2
    private val _means3: MutableLiveData<String> = MutableLiveData()
    val means3: LiveData<String> get() = _means3

    init {
        loadLevel()
    }

    private fun loadLevel() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _level.value = document.getString("level")
                Log.d("MyViewModel", "Level loaded: ${_level.value}")
            } else {
                Log.d("MyViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("MyViewModel", "Error getting document: $exception")
        }
    }

    fun getWords() {
        val level = _level.value
        if (level == null) {
            Log.e("MyViewModel", "Level is null. Unable to get words.")
            return
        }

        val englishDictionaryCollection = firestore.collection("englishDictionary").document(level)
        englishDictionaryCollection.get()
            .addOnSuccessListener { dictionaryDocument ->
                if (dictionaryDocument != null) {
                    val fieldMap = dictionaryDocument.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList()
                        val availableFieldNames = fieldNames - usedFieldNames

                        if (availableFieldNames.isNotEmpty()) {
                            val randomFieldName = availableFieldNames.random()
                            usedFieldNames.add(randomFieldName)

                            // Update the value of _words LiveData
                            _words.value = randomFieldName
                            Log.d("MyViewModel", "New word: ${_words.value}")
                        }
                    }
                }
            }
    } //getWords()

    var previousWord: String? = null // 이전에 표시한 단어를 저장하는 변수

    //DB에서 단어, 뜻, 북마크 가져오기
    fun getTodayWord() {
        val level = _level.value
        if (level == null) {
            Log.e("MyViewModel", "Level is null. Unable to get words.")
            return
        }

        val englishDictionaryCollection = firestore.collection("englishDictionary").document(level)
        englishDictionaryCollection.get()
            .addOnSuccessListener { dictionaryDocument ->
                if (dictionaryDocument != null) {
                    val fieldMap = dictionaryDocument.data //문서의 데이터. map 형태
                    if (fieldMap != null) {
                        // 단어 가져오기
                        val fieldNames = fieldMap.keys.toList()  //필드의 key
                        val availableFieldNames = fieldNames - usedFieldNames // 중복 방지

                        if (fieldNames.isNotEmpty()) { //모든 단어가 학습했던 단어들이어도 오늘의 영단어 학습할 수 있게함
                            val randomFieldName: String

                            if (usedFieldNames.size < 10) {
                                // 처음 학습할 때는 1번째부터 10번째까지의 단어 중 하나를 랜덤으로 선택
                                randomFieldName = availableFieldNames.random()
                            } else {
                                // 그 이후 학습에서는 이전에 학습했던 3개의 단어와 그 다음 단어부터 7개의 단어 중 하나를 랜덤으로 선택
                                val previouslyLearnedWords = usedFieldNames.takeLast(3)
                                val remainingWords =
                                    availableFieldNames - previouslyLearnedWords
                                randomFieldName =
                                    (remainingWords.takeLast(7) + previouslyLearnedWords).random()
                            }

                            usedFieldNames.add(randomFieldName)
                            //binding.TodayWord.text = randomFieldName
                            _words.value = randomFieldName
                            Log.d("MyViewModel", "New word: ${_words.value}")

                            //해당 단어의 뜻 가져오기 -> DB에 내용 전부 Map, String으로 넣으면 됨.
                            val meaningsMap = fieldMap[randomFieldName] as? Map<String, String>
                            if (meaningsMap != null && meaningsMap.isNotEmpty()) {
                                _means1.value = meaningsMap.get("mean1") ?: ""
                                _means2.value = meaningsMap.get("mean2") ?: ""
                                _means3.value = meaningsMap.get("mean3") ?: ""
                                //binding.TodayWordMean1.text =

                            }

                            // 단어를 북마크DB에 추가 또는 토글. 위치 여기 맞음
                            // toggleBookmark(randomFieldName)

                            //bookmarkChangeColor()
                            // todaywordBookmarkButton 누를때 색 바뀌는 동시에 해당 정보 db에 넣어야 함.

                            // 지금은 이전 단어를 하나만 저장을 했는데, map으로 단어 list로 전부 전부 저장해야 할듯.
                            // 처음나왔던 것부터 stack형태로.
                            previousWord = randomFieldName
                        }
                    }


                } else {
                    Log.d("TodayWordStudyFragment", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordStudyFragment", "Error getting document: $exception")
            }

    } //getTodayWord() 단어, 뜻, 북마크 가져오기

    //toggleBookmark()에서 사용
    private fun convertToDocumentId(word: String): String {
        return word.lowercase(Locale.ROOT).replace(" ", "_")
    }

    //bookmarkDB안에 map에서 key로 되어있는 단어를 불러오기. -> englishDictionary에서 단어로 뜻 가져오기
    fun toggleBookmark(word: String) { //단어랑 뜻 하나한 넘겨줘야 할 것 같음.
        userDB.get().addOnSuccessListener { dictionaryDocument ->
            if (dictionaryDocument != null) {

                val fieldMap = dictionaryDocument.data //문서의 데이터. map 형태
                val bookmarkMap = fieldMap?.get("bookmarkDB") as? Map<String, String>
                if (!bookmarkMap.isNullOrEmpty()) {
                    _words.value = bookmarkMap["word1"] ?: ""//해당 시점에 맞는 단어 가져오기

                }
            }

        }
        //해당 단어의 뜻 가져오기
        val level = _level.value
        if (level == null) {
            Log.e("MyViewModel", "Level is null. Unable to get words.")
            return
        }
        val englishDictionaryCollection = firestore.collection("englishDictionary").document(level)
        englishDictionaryCollection.get()
            .addOnSuccessListener { dictionaryDocument ->
                if (dictionaryDocument != null) {
                    val meaningsMap = dictionaryDocument.data as? Map<String, String>
                    if (!meaningsMap.isNullOrEmpty()) {
                        // LiveData를 통해 UI에 뜻을 전달
                        _means1.value = meaningsMap.get("mean1") ?: ""
                        _means2.value = meaningsMap.get("mean2") ?: ""
                        _means3.value = meaningsMap.get("mean3") ?: ""
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MyViewModel", "Error getting document: $exception")
            }
    }

        //색 바뀌는 동시에 firebase에 정보도 바꾸어야. 버튼 눌리는 건 fragment에서 해야할 것 같은데


        //이전 단어는 하나만 저정해두는게 아니라 리스트 형태로 넣어둬야 할듯?
        // 단어만 리스트 형태로 들어가 있어도 다시 단어db로 가서 뜻까지 찾아서 쓸 수 있잖아.
        // 찾는 방법은? 가장 최근에 만든 field 찾는 방법이 있나..? 그냥 field 이름 붙이자.
        // 이름을 순차로 붙이고, 붙일 때마다 개수를 세면 이전 단어를 알 수 있을 것.
        //getPreviousTodayWord()에서 사용
        // var previousWord: String? = null // 이전에 표시한 단어를 저장하는 변수
    fun getPreviousTodayWord(word: String) { //단어 자체를 받으면
        val level = _level.value
        if (level == null) {
            Log.e("MyViewModel", "Level is null. Unable to get words.")
            return
        }
            //뜻 가져오기
            val englishDictionaryCollection = firestore.collection("englishDictionary").document(level)
            englishDictionaryCollection.get()
                .addOnSuccessListener { dictionaryDocument ->
                    if (dictionaryDocument != null) {
                        val meaningsMap = dictionaryDocument.data as? Map<String, String>
                        if (!meaningsMap.isNullOrEmpty()) {
                            // LiveData를 통해 UI에 뜻을 전달
                            _means1.value = meaningsMap.get("mean1") ?: ""
                            _means2.value = meaningsMap.get("mean2") ?: ""
                            _means3.value = meaningsMap.get("mean3") ?: ""
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("MyViewModel", "Error getting document: $exception")
                }

        }
    }

    fun checkAnswer(answer: String): Boolean {
        return _words.value == answer
    }

    fun updateQuizResult(answer: String) {
        if (_words.value.isNullOrEmpty()) {
            Log.e("MyViewModel", "_words.value is null or empty.")
            return
        }
        val quizResult = if (checkAnswer(answer)) "O" else "X"

        // Update the currentquizDB with the current result
        val currentQuizData = mapOf(answer to quizResult)
        userDB.update("currentquizDB", currentQuizData)

        // Update the accurequizDB (only if it exists)
        userDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val accurequiz = task.result?.get("accurequizDB") as? MutableMap<String, String>
                if (accurequiz != null) {
                    if (accurequiz.containsKey(answer)) {
                        // 이미 푼 문제일 때 누적 퀴즈 db 처리
                        if (quizResult == "X" && checkAnswer(answer)) {
                            // 틀렸던 문제를 맞히면 O로 변경
                            accurequiz[answer] = "O"
                        }
                        // 이미 맞은 문제라면 아무것도 하지 않는다
                    } else {
                        // 푼 적 없는 문제라면 누적 퀴즈 db에 추가
                        accurequiz[answer] = quizResult
                    }
                    // 누적 퀴즈 db 업데이트
                    userDB.update("accurequizDB", accurequiz)
                }
            } else {
                // 에러 로그
                Log.e("MyViewModel", "Error getting userDB document: ${task.exception}")
            }
        }
    }
    fun resetCurrentQuizDB() {
        userDB.update("currentquizDB", mapOf<String, String>()) // assuming it's a map
    }
}