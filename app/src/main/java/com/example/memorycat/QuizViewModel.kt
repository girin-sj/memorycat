import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val userDB = firestore.collection("userDB").document(uid!!)
    private val usedFieldNames = mutableListOf<String>()
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _words: MutableLiveData<String> = MutableLiveData()
    val words: LiveData<String> get() = _words

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