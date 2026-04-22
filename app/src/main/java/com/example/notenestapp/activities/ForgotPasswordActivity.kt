package com.example.notenestapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notenestapp.database.DatabaseHelper
import com.example.notenestapp.databinding.ActivityForgotPasswordBinding
import com.example.notenestapp.utils.HashUtils

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.btnVerifyEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val question = dbHelper.getSecurityQuestion(email)

            if (question != null) {
                binding.tvQuestion.text = "Question: $question"
                binding.layoutReset.visibility = View.VISIBLE
                binding.etEmail.isEnabled = false
                binding.btnVerifyEmail.visibility = View.GONE
            } else {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnReset.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val answer = binding.etAnswer.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()

            if (answer.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.verifySecurityAnswer(email, answer)) {
                val passwordHash = HashUtils.md5(newPassword)
                dbHelper.updatePassword(email, passwordHash)
                Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Wrong answer", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
