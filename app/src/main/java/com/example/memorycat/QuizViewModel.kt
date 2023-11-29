
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.QuizResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val userDB = firestore.collection("userDB").document(uid!!)
    private val accureDB = firestore.collection("accurequizDB").document(uid!!)
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _randomWord = MutableLiveData<String>()
    val randomWord: LiveData<String> get() = _randomWord
    private val usedFieldNames = mutableListOf<String>()
    private val _meanings = MutableLiveData<List<String>>()
    val randomMeanings = MutableLiveData<MutableList<String>>()
    val quizResult = MutableLiveData<List<QuizResult>>()

    init {
        loadLevel()
    }

    private fun loadLevel() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _level.value = document.getString("level")
                Log.d("QuizViewModel", "Level loaded: ${_level.value}")
                getRandomWord() // level이 로드된 후에 getRandomWord() 호출
            } else {
                Log.d("QuizViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("QuizViewModel", "Error getting document: $exception")
        }
    }


    fun getRandomWord(): MutableLiveData<String> {
        val levelDocumentRef = firestore.collection("quizDB").document(level.value!!)

        Log.d("quizViewModel", "get random word")
        levelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList()
                        val availableFieldNames = fieldNames - usedFieldNames

                        if (availableFieldNames.isNotEmpty()) {
                            val randomFieldName = availableFieldNames.random()
                            usedFieldNames.add(randomFieldName)

                            _randomWord.value = randomFieldName
                            Log.d("QuizViewModel", "New word: ${_randomWord.value}")
                        }
                    }
                }
                else {
                    Log.d("QuizViewModel", "some error")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Error getting random word: $exception")
            }

        return _randomWord
    }

    fun getMeanings(word: String): MutableLiveData<List<String>> {
        val levelDocument = firestore.collection("quizDB").document(level.value!!)

        levelDocument.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val meanings = document.get(word) as MutableList<String>
                    _meanings.value = meanings
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Error getting meanings: $exception")
            }

        return _meanings
    }

    fun getRandomMeanings() {
        val randomMeaningsTemp = mutableListOf<String>()
        val levelDocument = firestore.collection("quizDB").document(level.value!!)

        levelDocument.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val allWords = document.data?.keys?.toList() ?: listOf()
                    for (i in 0 until 3) {
                        val randomWord = allWords.random()
                        val meanings = document.get(randomWord) as List<String>
                        randomMeaningsTemp.add(meanings.random())
                    }
                }
                randomMeanings.value = randomMeaningsTemp
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Error getting random meanings: $exception")
            }
    }

    fun updateQuizResult(word: String, answer: String) {
        accureDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                accureDB.update(
                    hashMapOf(
                        word to answer
                    ) as Map<String, Any>
                )
            }
        }
    }

    fun checkAnswer(userAnswer: String, correctAnswer: String): Boolean {
        return correctAnswer == userAnswer
    }
}