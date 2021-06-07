package com.example.projekchat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.example.projekchat.R
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.ui.home.HomeActivity
import com.example.projekchat.ui.login.LoginActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var imageSplashScreen:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        imageSplashScreen = findViewById(R.id.image_splashScreen)

//        imageSplashScreen.setOnClickListener {
//            val authenticationService = AuthenticationService()
//            var result = authenticationService.isUserSignIn()
//            println(result?.email)
//        }

        Handler().postDelayed({
            doValidate()
        }, 2000)
    }

    private fun doValidate(){
        val authenticationService = AuthenticationService()
        var result = authenticationService.isUserSignIn()
        if (result?.email?.isNotEmpty() == true){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}