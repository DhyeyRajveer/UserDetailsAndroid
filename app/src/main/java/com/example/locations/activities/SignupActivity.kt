package com.example.locations.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout.make
import android.text.TextUtils
import android.widget.*
import androidx.core.app.NavUtils
import com.example.locations.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setUpActionBar()
        findViewById<Button>(R.id.btn_sign_up).setOnClickListener { registerUser() }
    }

    private fun setUpActionBar() {
        var actionBar =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_sign_up_activity)
        setSupportActionBar(actionBar)
        var actionBarDefault = supportActionBar
        actionBarDefault?.setDisplayHomeAsUpEnabled(true)
        actionBarDefault?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        actionBar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser() {
        val nameET = "hell"
        val emailET = findViewById<EditText>(R.id.et_email).text.toString().trim(' ')
        val passwordET = findViewById<EditText>(R.id.et_password).text.toString()

        if (validateForm(nameET, emailET, passwordET)) {
            var mProgressDialog = Dialog(this)
            mProgressDialog.setContentView(R.layout.dialog_progress)
            mProgressDialog.show()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailET, passwordET)
                .addOnCompleteListener {
                    task ->
                    mProgressDialog.hide()
                    if(task.isSuccessful){
                        val firebaseUser :FirebaseUser= task.result!!.user!!
                        val registeredEmail = firebaseUser.email

                        startActivity(Intent(this, AddHappyPlaceActivity::class.java))

//                        Toast.makeText(this,"${registeredEmail} is registered",Toast.LENGTH_SHORT).show()
//                        FirebaseAuth.getInstance().signOut()
//                        finish()
                    }else{
                        Toast.makeText(this,"${task.exception!!.message}",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkUser(){
        FirebaseAuth.getInstance()!!.currentUser
    }


}