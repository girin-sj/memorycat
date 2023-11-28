package com.example.memorycat

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
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date
    private val _imageProfile = MutableLiveData<String>()
    val imageProfile: LiveData<String> get() = _imageProfile

    init {
        getName()
        getGoal()
        getImageProfile()
        getDate()
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
    fun getDate() {
        userDB.get().addOnSuccessListener { document ->
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
}