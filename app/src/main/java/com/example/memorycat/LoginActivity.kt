package com.example.memorycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.memorycat.ViewModel.MypageViewModel
import com.example.memorycat.databinding.ActivityLoginBinding
import java.time.LocalDate

class LoginActivity : AppCompatActivity() {
    private val mypageViewModel: MypageViewModel by viewModels() //뷰모델
    val localDate: LocalDate = LocalDate.now()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email: String = binding.emailInput.text.toString()
            val password: String = binding.passwordInput.text.toString()

            MyAuth.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.emailInput.text.clear()
                    binding.passwordInput.text.clear()
                    if (task.isSuccessful) {
                        if (MyAuth.checkAuth()) {
                            MyAuth.email = email
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            // 다른 날짜에 로그인
                            if (mypageViewModel.checkDate(localDate) == false) {
                                Toast.makeText(this, "출석 체크 성공!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(
                                baseContext,
                                "전송된 메일로 이메일 인증이 되지 않았습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }

                }
        }

        binding.signUpPageButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}