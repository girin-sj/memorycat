package com.example.memorycat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RankingViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mFirestore: FirebaseFirestore = Firebase.firestore
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val accureDB = firestore.collection("accurequizDB").document(uid!!)
    private val userDB = firestore.collection("userDB").document(uid!!)

    var correctcount = 0
    private val _correctCount = MutableLiveData<String>()
    val correctCount: LiveData<String> get() = _correctCount
    val users = MutableLiveData<List<String>>()
    init {
        updateCorrectCount()
        getCorrectCount()
        getUserDocument()
    }

    fun getUserDocument() {
        
    }
    fun getCorrectCount() {
        userDB.get().addOnSuccessListener { document ->
            if (document != null) {
                _correctCount.value = document.getString("correctCount")
                Log.d("RankingViewModel", "correctCount loaded: ${_correctCount.value}")
            } else {
                Log.d("RankingViewModel", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.e("RankingViewModel", "Error getting document: $exception")
        }
    }

    fun updateCorrectCount() {
        accureDB.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data // 문서의 데이터
                    // 데이터가 HashMap 형태로 저장 -> 모든 document 하위 문서 가져옴
                    if (data != null) {
                        // key는 필드 이름, value는 해당 필드의 값
                        for ((key, value) in data) {
                            if (value == "O") {
                                correctcount++
                            }
                        }
                        updateCorrectCount(correctcount.toString())
                    }
                } else {
                    Log.d("RankingViewModel", "문서가 존재하지 않습니다.")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("RankingViewModel", "데이터를 가져오는 도중 오류 발생")
            }
    }

    private fun updateCorrectCount(correctCount: String) {
        userDB.update("correctCount", correctCount)
    }
}