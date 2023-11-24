package com.example.memorycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.memorycat.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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
                                            "bookmarkDB" to hashMapOf<String, Any>(),
                                            "email" to email,
                                            "goal" to "",
                                            "headerImage" to "",
                                            "level" to "bronze",
                                            "nickname" to "",
                                            "password" to password,
                                            "profileImage" to ""
                                        )
                                    ).addOnSuccessListener {
                                        val quizDB = userDB.collection("quizDB")
                                        val levels =
                                            listOf("bronze", "silver", "gold", "platinum", "master")
                                        for (level in levels) {
                                            quizDB.document(level).set(hashMapOf<String, Any>())
                                        }
                                    }
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