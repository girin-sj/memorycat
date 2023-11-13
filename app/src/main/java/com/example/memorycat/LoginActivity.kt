package com.example.memorycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.memorycat.databinding.ActivityLoginBinding
import com.example.memorycat.databinding.ActivitySignUpBinding

class LoginActivity : AppCompatActivity() {
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
                        if (MyAuth.checkAuth()){
                            MyAuth.email = email
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(baseContext, "전송된 메일로 이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    else {
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