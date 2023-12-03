package com.example.memorycat.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MypageViewModel: ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val userDB = firestore.collection("userDB").document(uid!!)
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
        getName()
        getGoal()
        getImageProfile()
        getLocalDate()
    }

    fun updateName(nameText: String) {
        userDB.update("nickname", nameText)
    }
    fun updateGoal(goalText: String) {
        userDB.update("goal", goalText)
    }
    fun updateProfileImage(imageUri: String) {
        userDB.update("profileImage", imageUri)
    }
    fun updateLocalDate(localdate: String) {
        userDB.update("logindate", localdate)
    }
    fun updateDate(date: String) {
        userDB.update("date", date)
    }
    fun getName() {
        userDB.get().addOnSuccessListener { document ->
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
        userDB.get().addOnSuccessListener { document ->
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
        userDB.get().addOnSuccessListener { document ->
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
    fun getLocalDate() {
        userDB.get().addOnSuccessListener { document ->
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

    private val accureDB = firestore.collection("accurequizDB").document(uid!!)

    var correctcount = 0

    fun getCount() {
        accureDB.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data // 문서의 데이터
                    // 데이터가 HashMap 형태로 저장 -> 모든 document 하위 문서 가져옴
                    if (data != null) {
                        // key는 필드 이름, value는 해당 필드의 값
                        for ((key, value) in data) {
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
    fun checkDate(currentDate: String): Boolean {
        Log.d("MypageViewModel", "_localdate: ${_localdate.value}")
        Log.d("MypageViewModel", "currentDate: $currentDate")
        return this._localdate.value.toString() == currentDate
    }

}