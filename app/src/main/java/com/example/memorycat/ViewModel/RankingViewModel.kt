package com.example.memorycat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.Repository.RankingRepository
import com.example.memorycat.Repository.Repository
import com.example.memorycat.ViewModel.QuizRepository


class RankingViewModel : ViewModel() {
    private val rankingRepository = RankingRepository()

    val level: LiveData<String>
        get() = rankingRepository.level
    val name: LiveData<String>
        get() = rankingRepository.name

    var correctCount = MutableLiveData<String>()
        get() = rankingRepository._correctCount

    var userlist: MutableMap<String, String> = HashMap()
        get() = rankingRepository.userlist

    fun getUidDocument(): LiveData<List<User>> {
        return rankingRepository.getUidDocument()
    }

    fun updateCorrectCount(correctCount: String) {
        rankingRepository.updateCorrectCount(correctCount)
    }

    fun countCorrectcount() {
        rankingRepository.countCorrectcount()
    }
}