package com.example.memorycat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memorycat.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val firestore = FirebaseFirestore.getInstance()

        binding.signUpButton.setOnClickListener {
            val email: String = binding.emailInput.text.toString()
            val password: String = binding.passwordInput.text.toString()
            MyAuth.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.emailInput.text.clear()
                    binding.passwordInput.text.clear()
                    if (task.isSuccessful) {
                        MyAuth.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    Toast.makeText(
                                        baseContext,
                                        "회원가입에 성공하였습니다." +
                                                "전송된 메일을 확인해 주세요.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
                                    val userDB = firestore.collection("userDB").document(uid!!)
                                    userDB.set(
                                        hashMapOf(
                                            "date" to "",
                                            "email" to email,
                                            "goal" to "",
                                            "headerImage" to "",
                                            "level" to "bronze",
                                            "nickname" to "",
                                            "password" to password,
                                            "profileImage" to ""
                                        )
                                    )
                                    val bookmarkDB = firestore.collection("bookmarkDB").document(uid!!)
                                    bookmarkDB.set(hashMapOf<Any, Any>())
                                    val accureDB = firestore.collection("accurequizDB").document(uid!!)
                                    accureDB.set(hashMapOf<Any, Any>())
                                    val recentbDB = firestore.collection("recentbookmarkDB").document(uid!!)
                                    recentbDB.set(hashMapOf<Any, Any>())
                                    val recentDB = firestore.collection("recentquizDB").document(uid!!)
                                    recentDB.set(hashMapOf<Any, Any>())
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                    }
                    else {
                        Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}