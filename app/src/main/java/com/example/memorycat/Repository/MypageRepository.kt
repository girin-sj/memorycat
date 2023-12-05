package com.example.memorycat.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MypageRepository: ViewModel() {
    val repo: Repository = Repository()

    var correctcount = 0
    private val _goal = MutableLiveData<String>()
    val goal: LiveData<String> get() = _goal
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name
    private val _localdate = MutableLiveData<String>()
    val localdate: LiveData<String> get() = _localdate

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date
    private val _imageProfile = MutableLiveData<String>()
    val imageProfile: LiveData<String> get() = _imageProfile

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getName()
            getGoal()
            getImageProfile()
            getLocalDate()
            getCount()
            getDate()
        }
    }

    fun updateName(nameText: String) {
        repo.userDB.update("nickname", nameText)
    }
    fun updateGoal(goalText: String) {
        repo.userDB.update("goal", goalText)
    }
    fun updateProfileImage(imageUri: String) {
        repo.userDB.update("profileImage", imageUri)
    }
    fun updateLocalDate(localdate: String) {
        repo.userDB.update("logindate", localdate)
    }
    fun updateDate(date: String) {
        repo.userDB.update("date", date)
    }
    fun getName() {
        repo.userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _name.value = document.getString("nickname")
                Log.d("MypageViewModel", "Level loaded: ${_name.value}")
            } else {
                Log.d("MypageViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("MypageViewModel", "Error getting document: $exception")
        }
    }
    fun getGoal() {
        repo.userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _goal.value = document.getString("goal")
                Log.d("MypageViewModel", "Goal loaded: ${_goal.value}")
            } else {
                Log.d("MypageViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("MypageViewModel", "Error getting document: $exception")
        }
    }
    fun getImageProfile() {
        repo.userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _imageProfile.value = document.getString("profileImage")
                Log.d("MypageViewModel", "Image loaded: ${_imageProfile.value}")
            } else {
                Log.d("MypageViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("MypageViewModel", "Error getting document: $exception")
        }
    }
    fun getDate() {
        repo.userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _date.value = document.getString("date")
                Log.d("MypageViewModel", "Date loaded: ${_date.value}")
            } else {
                Log.d("MypageViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("MypageViewModel", "Error getting document: $exception")
        }
    }
    fun getLocalDate() {
        repo.userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _localdate.value = document.getString("logindate")
                Log.d("MypageViewModel", "Date loaded: ${_localdate.value}")
            } else {
                Log.d("MypageViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("MypageViewModel", "Error getting document: $exception")
        }
    }



    fun getCount() {
        repo.accureDB.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data // 문서의 데이터
                    // 데이터가 HashMap 형태로 저장 -> 모든 document 하위 문서 가져옴
                    if (data != null) {
                        correctcount = 0
                        // key는 필드 이름, value는 해당 필드의 값
                        for ((value) in data) {
                            if(value == "O") {
                                correctcount++
                            }
                        }
                    }
                } else {
                    println("문서가 존재하지 않습니다.")
                }
            }
            .addOnFailureListener { exception ->
                println("데이터를 가져오는 중 오류 발생: $exception")
            }
    }
    fun checkDate(currentDate: LocalDate): Boolean {
        Log.d("MypageViewModel", "_localdate: ${_localdate.value}")
        Log.d("MypageViewModel", "currentDate: $currentDate")
        return this._localdate.value.toString() == currentDate.toString()
    }

}