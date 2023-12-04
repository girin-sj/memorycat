package com.example.memorycat.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Repository_yjw {
    private val _firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val _userDB = _firestore.collection("userDB").document(uid!!)
    private val _bookmarkDB = _firestore.collection("recentbookmarkDB").document(uid!!)
    val firestore get() = _firestore
    val userDB get() = _userDB
    val bookmarkDB get() = _bookmarkDB //이거 이름을 북마크db로 바꿀까
}