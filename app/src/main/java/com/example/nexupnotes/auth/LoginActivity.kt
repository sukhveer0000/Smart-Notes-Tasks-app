package com.example.nexupnotes.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nexupnotes.MainActivity
import com.example.nexupnotes.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private  lateinit var auth : FirebaseAuth

    private lateinit var userEmail: TextInputEditText
    private lateinit var userPassword: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signupText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        userEmail = findViewById(R.id.userMail)
        userPassword = findViewById(R.id.userPassword)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.createNewAccount)

        loginButton.isEnabled = false

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
                val email  = userEmail.text.toString().trim()
                val password  = userPassword.text.toString().trim()

                val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                val isPasswordValid = password.length >= 6

                loginButton.isEnabled = (isEmailValid && isPasswordValid)

            }
        }
        userEmail.addTextChangedListener(textWatcher)
        userPassword.addTextChangedListener(textWatcher)

        loginButton.setOnClickListener {
            val email  = userEmail.text.toString().trim()
            val password  = userPassword.text.toString().trim()

            auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()

                }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}