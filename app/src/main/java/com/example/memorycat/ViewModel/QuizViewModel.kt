package com.example.memorycat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.QuizResult

class QuizViewModel : ViewModel() {

    private val quizRepository = QuizRepository()

    val level: LiveData<String>
        get() = quizRepository.level

    val randomWord: LiveData<String>
        get() = quizRepository.randomWord

    val randomMeanings: MutableLiveData<MutableList<String>>
        get() = quizRepository.randomMeanings

    fun getRandomWord() {
        quizRepository.getRandomWord()
    }

    fun getMeanings(word: String): MutableLiveData<List<String>> {
        return quizRepository.getMeanings(word)
    }

    fun getNoteMeanings(word: String): MutableLiveData<List<String>> {
        return quizRepository.getNoteMeanings(word)
    }

    fun getRandomMeanings() {
        quizRepository.getRandomMeanings()
    }

    fun updateQuizResult(word: String, answer: String) {
        quizRepository.updateQuizResult(word, answer)
    }

    fun updateNoteResult(word: String, select: String, answer: String, isCorrect: String) {
        quizRepository.updateNoteResult(word, select, answer, isCorrect)
    }

    fun loadNoteResult(): LiveData<List<QuizResult>> {
        return quizRepository.loadNoteResult()
    }

    fun resetNoteResult() {
        quizRepository.resetNoteResult()

    fun resetNoteResult(){
        repo.recentDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                repo.recentDB.set(hashMapOf<Any, Any>())
            }
        }
