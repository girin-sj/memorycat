package com.example.memorycat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.BookmarkResult
import com.example.memorycat.Repository.TodaywordRepository

class TodayWordViewModel: ViewModel() {

    private val todaywordRepository = TodaywordRepository()
    val level: LiveData<String>
        get() = todaywordRepository.level

    val todayWordNames: MutableLiveData<MutableList<String>>
        get() = todaywordRepository.todayWordNames

    val todayWord: LiveData<String>
        get() = todaywordRepository.todayWord

    fun makeTodayWordList(){
        todaywordRepository.makeTodayWordList()
    }
    fun getTodayWord(idx: Int): MutableLiveData<String> {
        return todaywordRepository.getTodayWord(idx)
    }
    fun getMeanings(word: String): MutableLiveData<List<String>> {
        return todaywordRepository.getMeanings(word)
    }
    fun updateBookmarkResult(word: String, mean1: String, mean2: String, mean3: String, isSelect: String){
        todaywordRepository.updateBookmarkResult(word, mean1, mean2, mean3, isSelect)
    }
    fun loadSelectedBookmarks(callback: (List<BookmarkResult>) -> Unit){
        return todaywordRepository.loadSelectedBookmarks(callback)
    }
    fun removeBookmark(word: String){
        return todaywordRepository.removeBookmark(word)
    }
    fun checkSelect(word: String, callback: (Boolean) -> Unit){
        return todaywordRepository.checkSelect(word, callback)
    }

}