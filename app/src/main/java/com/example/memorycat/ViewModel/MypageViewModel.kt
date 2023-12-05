package com.example.memorycat.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.Repository.MypageRepository
import com.example.memorycat.Repository.RankingRepository
import com.example.memorycat.Repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class MypageViewModel: ViewModel() {
    private val mypageRepository = MypageRepository()
    val goal: LiveData<String>
        get() = mypageRepository.goal
    val name: LiveData<String>
        get() = mypageRepository.name
    val localdate: LiveData<String>
        get() = mypageRepository.localdate
    val date: LiveData<String>
        get() = mypageRepository.date
    val imageProfile: LiveData<String>
        get() = mypageRepository.imageProfile


    fun updateName(nameText: String) {
        mypageRepository.updateName(nameText)
    }

    fun updateGoal(goalText: String) {
        mypageRepository.updateGoal(goalText)
    }

    fun updateProfileImage(imageUri: String) {
        mypageRepository.updateProfileImage(imageUri)
    }

    fun updateLocalDate(localdate: String) {
        mypageRepository.updateLocalDate(localdate)
    }

    fun updateDate(date: String) {
        mypageRepository.updateDate(date)
    }

    fun getName() {
        mypageRepository.getName()
    }

    fun getGoal() {
        mypageRepository.getGoal()
    }

    fun getImageProfile() {
        mypageRepository.getImageProfile()
    }

    fun getDate() {
        mypageRepository.getDate()
    }

    fun getLocalDate() {
        mypageRepository.getLocalDate()
    }

    fun getCount() {
        mypageRepository.getCount()
    }

    fun checkDate(currentDate: LocalDate): Boolean {
        return mypageRepository.checkDate(currentDate)
    }
}