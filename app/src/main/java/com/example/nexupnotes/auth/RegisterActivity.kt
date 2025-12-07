package com.example.nexupnotes.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nexupnotes.MainActivity
import com.example.nexupnotes.R
import com.example.nexupnotes.utils.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etUserName = findViewById<TextInputEditText>(R.id.rgUserName)
        val etEmail = findViewById<TextInputEditText>(R.id.rgEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.rgUserPassword)
        val btnSignup = findViewById<MaterialButton>(R.id.registerButton)

        btnSignup.isEnabled = false

        val textWatcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val userName = etUserName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                val isPasswordValid = (password.length >= 6)

                val isUserNameValid = userName.isNotEmpty()

                btnSignup.isEnabled = (isUserNameValid && isEmailValid && isPasswordValid)
            }

        }
        etUserName.addTextChangedListener(textWatcher)
        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)

        btnSignup.setOnClickListener {
            val userName = etUserName.text.toString().trim()
            val emailText = etEmail.text.toString().trim()
            val passwordText = etPassword.text.toString().trim()

            auth.createUserWithEmailAndPassword(emailText,passwordText)
                .addOnSuccessListener {

                    val profileUpdate = userProfileChangeRequest {
                        displayName = userName
                    }

                    val uid = it.user!!.uid
                    it.user!!.updateProfile(profileUpdate)

                    val userMap = hashMapOf(
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Signup Success", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.d("fail",e.toString())
                        }
                }
                .addOnFailureListener { e->
                    Toast.makeText(this, "$e signup failed", Toast.LENGTH_SHORT).show()
                    Log.d("fail","$e")

                }

        }
    }
}