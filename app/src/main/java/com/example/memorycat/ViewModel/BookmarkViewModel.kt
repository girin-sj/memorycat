package com.example.memorycat.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorycat.BookmarkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookmarkViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val userDB = firestore.collection("userDB").document(uid!!)
    private val bookmarkDB = firestore.collection("bookmarkDB").document(uid!!)

    val bookmarkResult = MutableLiveData<List<BookmarkResult>>()

    //db 변화
    fun updateBookmarkResult(word: String, select: String) {
        bookmarkDB.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                bookmarkDB.update(
                    hashMapOf(
                        word to select
                    ) as Map<String, Any>
                )
            }
        }
    }

    //지금 북마크 상태 확인
    fun checkBookmarkState(userAnswer: String, correctAnswer: String): Boolean {
        return correctAnswer == userAnswer
    }
}
