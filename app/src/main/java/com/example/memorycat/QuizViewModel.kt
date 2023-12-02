import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.Repository
import com.example.memorycat.QuizResult

class QuizViewModel : ViewModel() {
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _randomWord = MutableLiveData<String>()
    val randomWord: LiveData<String> get() = _randomWord
    private val usedFieldNames = mutableListOf<String>()
    private val _meanings = MutableLiveData<List<String>>()
    val randomMeanings = MutableLiveData<MutableList<String>>()
    val quizResult = MutableLiveData<List<QuizResult>>()
    val repo: Repository = Repository()

    init {
        loadLevel()
    }

    private fun loadLevel() {
        repo.userDB.get().addOnSuccessListener { document ->
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

    fun updateLevel() {
        if (_level.value=="bronze"){
            repo.userDB.update(hashMapOf("level" to "silver") as Map<String, String>)
        }
        else if (_level.value=="silver"){
            repo.userDB.update(hashMapOf("level" to "gold") as Map<String, String>)
        }
        else if (_level.value=="gold"){
            repo.userDB.update(hashMapOf("level" to "platinum") as Map<String, String>)
        }
        else {
            repo.userDB.update(hashMapOf("level" to "master") as Map<String, String>)
        }
    }


    fun getRandomWord(): MutableLiveData<String> {
        val levelDocumentRef = repo.firestore.collection("quizDB").document(level.value!!)

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
        val levelDocument = repo.firestore.collection("quizDB").document(level.value!!)

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
        val levelDocument = repo.firestore.collection("quizDB").document(level.value!!)

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
        repo.accureDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                repo.accureDB.update(
                    hashMapOf(
                        word to answer
                    ) as Map<String, String>
                )
            }
        }
    }

    fun updateNoteResult(word: String, select: String, answer: String, isCorrect: String) {
        repo.recentDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                repo.recentDB.update(
                    hashMapOf(
                        word to hashMapOf(
                            "select" to select,
                            "answer" to answer,
                            "isCorrect" to isCorrect
                        )
                    ) as Map<String, String>
                )
            }
        }
    }

    fun loadNoteResult(): LiveData<List<QuizResult>> {
        val liveData = MutableLiveData<List<QuizResult>>()
        repo.recentDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val quizResults = mutableListOf<QuizResult>()
                val document = task.result
                document?.data?.forEach { entry ->
                    val word = entry.key
                    val value = entry.value as Map<String, String>
                    val select = value["select"] ?: ""
                    val answer = value["answer"] ?: ""
                    val isCorrect = value["isCorrect"] ?: ""
                    quizResults.add(QuizResult(word, select, answer, isCorrect))
                }
                liveData.postValue(quizResults)
            }
        }
        return liveData
    }

    fun resetNoteResult(){
        repo.recentDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                repo.recentDB.set(hashMapOf<Any, Any>())
            }
        }
    }

    fun checkAnswer(userAnswer: String, correctAnswer: String): Boolean {
        return correctAnswer == userAnswer
    }
}
