package com.example.projekchat.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.projekchat.R
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.ui.dialogbox.DialogBoxService
import com.example.projekchat.ui.home.HomeActivity
import com.example.projekchat.ui.regis.RegisterActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var btn_login:Button
    private lateinit var btn_regis:Button
    private lateinit var input_password:TextInputEditText
    private lateinit var input_username:TextInputEditText
    private lateinit var layout_inputPassword:TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        initializeLayout()

        btn_login.setOnClickListener {
            val dialogBoxService = DialogBoxService(this)
            GlobalScope.launch {
                if (validateAllTextField()){
                    withContext(Dispatchers.Main){dialogBoxService.startLoading()}
                    if (doLogin()==AuthenticationService.SUCCESS){
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        withContext(Dispatchers.Main){dialogBoxService.failedDialog("${doLogin().toString()}")}
                    }
                }
            }
        }

        btn_regis.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun doLogin(): String {
        val authenticationService = AuthenticationService()
        return authenticationService.loginUser(input_username.text.toString(), input_password.text.toString())
    }

    private suspend fun validateAllTextField():Boolean{
        var isTrue:Boolean = false
        withContext(Dispatchers.Main){
            if (input_username.text?.trim()!!.isNotEmpty() && input_password.text?.trim()!!.isNotEmpty()){
                isTrue = true
            }else if (input_username.text!!.trim().isNullOrEmpty()){
                input_username.error = "REQUIRED"
                isTrue = false
            }else if (input_password.text!!.trim().isNullOrEmpty()){
                input_password.error = "REQUIRED"
                isTrue = false
            }
        }
        return isTrue
    }

    private fun initializeLayout() {
        btn_login = findViewById(R.id.loginActivity_btnLogin)
        btn_regis = findViewById(R.id.loginActivity_btnRegis)
        layout_inputPassword = findViewById(R.id.loginActivity_inputUsernameLayout)
        input_username = findViewById(R.id.loginActivity_inputUsername)
        input_password = findViewById(R.id.loginActivity_inputPassword)
    }
}