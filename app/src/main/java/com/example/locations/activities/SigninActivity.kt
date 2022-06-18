package com.example.locations.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.browser.trusted.Token
import com.example.locations.R
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        auth = FirebaseAuth.getInstance()

        val auth: FirebaseAuth

        setUpActionBar()
        findViewById<Button>(R.id.btn_sign_in).setOnClickListener { signinUser() }

    }

    private fun signinUser() {
        val email = findViewById<EditText>(R.id.et_email_signin).text.toString().trim(' ')
        val password = findViewById<EditText>(R.id.et_password_signin).text.toString().trim(' ')

        validateForm(email, password)

        var mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.show()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                mProgressDialog.hide()
                if (task.isSuccessful) {
                    Toast.makeText(this, "User signedin", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HappyPlaceDetails::class.java))
                } else {
                    Toast.makeText(this, "${task.exception!!.message}", Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun setUpActionBar() {
        var actionBar =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_sign_in_activity)
        setSupportActionBar(actionBar)
        var actionBarDefault = supportActionBar
        actionBarDefault?.setDisplayHomeAsUpEnabled(true)
        actionBarDefault?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        actionBar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateForm(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}