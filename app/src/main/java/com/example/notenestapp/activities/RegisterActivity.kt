package com.example.notenestapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notenestapp.database.DatabaseHelper
import com.example.notenestapp.databinding.ActivityRegisterBinding
import com.example.notenestapp.utils.HashUtils

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val answer = binding.etSecurityAnswer.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || answer.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordHash = HashUtils.md5(password)
            val result = dbHelper.addUser(fullName, email, passwordHash, "Your first pet's name?", answer)

            if (result != -1L) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}
