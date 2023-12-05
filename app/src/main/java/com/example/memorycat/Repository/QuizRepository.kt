package com.example.memorycat.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memorycat.QuizResult
import com.example.memorycat.Repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizRepository : ViewModel() {
    private val _level = MutableLiveData<String>()
    val level: LiveData<String> get() = _level
    private val _randomWord = MutableLiveData<String>()
    val randomWord: LiveData<String> get() = _randomWord
    private val usedFieldNames = mutableListOf<String>()
    private val _meanings = MutableLiveData<List<String>>()
    val randomMeanings = MutableLiveData<MutableList<String>>()
    private val repo: Repository = Repository()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadLevel()
        }
    }

    private fun loadLevel() {
        repo.userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _level.value = document.getString("level")
                Log.d("com.example.memorycat.ViewModel.QuizViewModel", "Level loaded: ${_level.value}")
                getRandomWord() // level이 로드된 후에 getRandomWord() 호출
            } else {
                Log.d("com.example.memorycat.ViewModel.QuizViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("com.example.memorycat.ViewModel.QuizViewModel", "Error getting document: $exception")
        }
    }

    fun updateLevel() {
        when (_level.value) {
            "bronze" -> repo.userDB.update(mapOf("level" to "silver"))
            "silver" -> repo.userDB.update(mapOf("level" to "gold"))
            "gold" -> repo.userDB.update(mapOf("level" to "platinum"))
            else -> repo.userDB.update(mapOf("level" to "master"))
        }
    }

    fun getRandomWord(): MutableLiveData<String> {
        val currentLevel = level.value ?: return _randomWord

        val currentLevelDocumentRef = repo.firestore.collection("quizDB").document(currentLevel)
        val previousLevelDocumentRef = when (currentLevel) {
            "silver" -> repo.firestore.collection("quizDB").document("bronze")
            "gold" -> repo.firestore.collection("quizDB").document("silver")
            "platinum" -> repo.firestore.collection("quizDB").document("gold")
            "master" -> repo.firestore.collection("quizDB").document("platinum")
            else -> null
        }

        Log.d("quizViewModel", "get random word")

        currentLevelDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList()
                        val availableFieldNames = fieldNames - usedFieldNames.toSet()

                        if (availableFieldNames.isNotEmpty()) {
                            val randomFieldName = availableFieldNames.random()
                            usedFieldNames.add(randomFieldName)

                            _randomWord.value = randomFieldName
                            Log.d("com.example.memorycat.ViewModel.QuizViewModel", "New word: ${_randomWord.value}")
                        }
                    }
                } else {
                    Log.d("com.example.memorycat.ViewModel.QuizViewModel", "some error")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("com.example.memorycat.ViewModel.QuizViewModel", "Error getting random word: $exception")
            }

        // Fetch additional words from the previous level
        previousLevelDocumentRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    val fieldMap = document.data
                    if (fieldMap != null) {
                        val fieldNames = fieldMap.keys.toList()
                        val availableFieldNames = fieldNames - usedFieldNames.toSet()

                        if (availableFieldNames.isNotEmpty()) {
                            val randomFieldName = availableFieldNames.random()
                            usedFieldNames.add(randomFieldName)

                            // You can do something with the additional word from the previous level if needed
                            Log.d("com.example.memorycat.ViewModel.QuizViewModel", "Previous level word: $randomFieldName")
                        }
                    }
                } else {
                    Log.d("com.example.memorycat.ViewModel.QuizViewModel", "some error in fetching previous level word")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.e("com.example.memorycat.ViewModel.QuizViewModel", "Error getting random word from previous level: $exception")
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
                Log.e("com.example.memorycat.ViewModel.QuizViewModel", "Error getting meanings: $exception")
            }
        return _meanings
    }

    fun getNoteMeanings(word: String): MutableLiveData<List<String>> {
        val meanings = mutableListOf<String>()
        val dictionaryCollection = repo.firestore.collection("englishDictionary")

        // Query all documents in the collection
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
                Log.e("com.example.memorycat.ViewModel.QuizViewModel", "Error getting random meanings: $exception")
            }
    }

    fun updateQuizResult(word: String, answer: String) {
        repo.accureDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.contains(word)) {
                    val existingAnswer = document.getString(word)
                    if (existingAnswer == "X" && answer == "O") {
                        repo.accureDB.update(
                            hashMapOf(
                                word to answer
                            ) as Map<String, String>
                        )
                    }
                }
                else {
                    repo.accureDB.update(
                        hashMapOf(
                            word to answer
                        ) as Map<String, String>
                    )
                }
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
