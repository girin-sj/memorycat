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
        // 모든 작업이 level을 load하고 실행되어야 함
        viewModelScope.launch(Dispatchers.IO) {
            loadLevel()
        }
    }

    // 사용자 레벨 load
    private fun loadLevel() {
        repo.userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _level.value = document.getString("level")
                Log.d("QuizViewModel", "Level loaded: ${_level.value}")
                getCurrentRandomWord() // level이 로드된 후에 getRandomWord() 호출
            } else {
                Log.d("QuizViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("QuizViewModel", "Error getting document: $exception")
        }
    }

    // 퀴즈를 다 맞혔을 때 다음 레벨로 update
    fun updateLevel() {
        when (_level.value) {
            "bronze" -> repo.userDB.update(mapOf("level" to "silver"))
            "silver" -> repo.userDB.update(mapOf("level" to "gold"))
            "gold" -> repo.userDB.update(mapOf("level" to "platinum"))
            else -> repo.userDB.update(mapOf("level" to "master"))
        }
    }

    // 현재 레벨의 단어를 랜덤하게 get
    fun getCurrentRandomWord(): MutableLiveData<String> {
        val currentLevel = level.value ?: return _randomWord

        val currentLevelDocumentRef = repo.firestore.collection("quizDB").document(currentLevel)

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
                            Log.d("QuizViewModel", "New word: ${_randomWord.value}")
                        }
                    }
                } else {
                    Log.d("QuizViewModel", "some error")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Error getting random word: $exception")
            }

        return _randomWord
    }

    // 화면에 표시되는 단어의 뜻(정답)을 get
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

    // 현재 레벨의 뜻(선택지)를 랜덤하게 get
    fun getRandomMeanings(word: String) {
        val randomMeaningsTemp = mutableListOf<String>()
        val levelDocument = repo.firestore.collection("quizDB").document(level.value!!)

        levelDocument.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val allWords = document.data?.keys?.toList() ?: listOf()

                    // 정답을 제외한 랜덤을 가져옴
                    val remainingWords = allWords.filterNot { it == word }

                    for (i in 0 until 3) {
                        val randomWord = remainingWords.random()
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

    // Dictionary의 상세한 정보를 dialog에 get
    fun getNoteMeanings(word: String): MutableLiveData<List<String>> {
        val meanings = mutableListOf<String>()
        val dictionaryCollection = repo.firestore.collection("englishDictionary")

        // 모든 디렉토리 탐색
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

    // 맞힌 단어를 누적해서 update
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

    // 오답노트에 표시할 정보를 update
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

    // 오답노트에 정보를 load
    fun loadNoteResult(): LiveData<List<QuizResult>> {
        val liveData = MutableLiveData<List<QuizResult>>()
        repo.recentDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val quizResults = mutableListOf<QuizResult>()
                val document = task.result
                //문서의 모든 data를 반복해서 처리, QuizResult 객체의 리스트로 변환
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

    // 새 quiz가 실행될 때마다 reset
    fun resetNoteResult(){
        repo.recentDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                repo.recentDB.set(hashMapOf<Any, Any>())
            }
        }
    }

    // 정답 여부 check
    fun checkAnswer(userAnswer: String, correctAnswer: String): Boolean {
        return correctAnswer == userAnswer
    }
}
