package com.example.locations.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.locations.R

class IntroScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)
        var signUpBtn = findViewById<Button>(R.id.btn_sign_up_intro)
        signUpBtn.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        var signInBtn = findViewById<Button>(R.id.btn_sign_in_intro)
        signInBtn.setOnClickListener{
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }


}