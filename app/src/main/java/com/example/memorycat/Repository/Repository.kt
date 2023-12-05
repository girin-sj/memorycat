package com.example.memorycat.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Repository {
    private val _firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val _userDB = _firestore.collection("userDB").document(uid!!)
    private val _accureDB = _firestore.collection("accurequizDB").document(uid!!)
    private val _recentDB = _firestore.collection("recentquizDB").document(uid!!)

    val firestore get() = _firestore
    val userDB get() = _userDB
    val accureDB get() = _accureDB
    val recentDB get() = _recentDB
}