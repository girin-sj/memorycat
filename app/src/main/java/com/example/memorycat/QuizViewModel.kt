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
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _randomWord = MutableLiveData<String>()
    val randomWord: LiveData<String> get() = _randomWord
    private val _meanings = MutableLiveData<List<String>>()
    val quizResult = MutableLiveData<List<QuizResult>>()

    init {
        loadLevel()
        getRandomWord()
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

    fun getRandomWord(): MutableLiveData<String> {
        val levelDocument = firestore.collection("quizDB").document(level.value!!)

        levelDocument.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldNames = document.data?.keys?.toList() ?: listOf()
                    val randomWord = fieldNames.random()
                    _randomWord.value = randomWord
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Error getting random word: $exception")
            }

        return _randomWord
    }

    fun getMeanings(word: String): LiveData<List<String>> {
        val levelDocument = firestore.collection("quizDB").document(level.value!!)

        levelDocument.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val meanings = document.get(word) as List<String>
                    _meanings.value = meanings
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Error getting meanings: $exception")
            }

        return _meanings
    }

    fun getRandomMeanings(): MutableList<String> {
        val randomMeanings = mutableListOf<String>()
        val levelDocument = firestore.collection("quizDB").document(level.value!!)

        levelDocument.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val allWords = document.data?.keys?.toList() ?: listOf()
                    for (i in 0 until 3) {
                        val randomWord = allWords.random()
                        val meanings = document.get(randomWord) as List<String>
                        randomMeanings.add(meanings.random())
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Error getting random meanings: $exception")
            }

        return randomMeanings
    }

    fun updateQuizResult(answer: String) {

    }

    fun checkAnswer(answer: String): Boolean {
        return _randomWord.value == answer
    }

}