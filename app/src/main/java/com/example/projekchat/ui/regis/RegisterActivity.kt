package com.example.projekchat.ui.regis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.projekchat.R
import com.example.projekchat.services.firestore.FirestoreService
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.ui.dialogbox.DialogBoxService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var inputFullname:TextInputEditText
    private lateinit var inputEmail:TextInputEditText
    private lateinit var inputPassword:TextInputEditText
    private lateinit var inputConfPassword:TextInputEditText
    private lateinit var btn_regis:Button
    private lateinit var btn_login:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        initializationLayout()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.registerActivity_btnRegis->{
                var dialogBoxService = DialogBoxService(this)
                GlobalScope.launch {
                    if (validateAllEditText()){
                        if (checkIsPassAndConfPassIsMatch()){
                            withContext(Dispatchers.Main){dialogBoxService.startLoading()}
                            if (doRegisAuth() == AuthenticationService.SUCCESS){
                                if (doRegisToFirestore()== FirestoreService.SUCCESS){
                                    withContext(Dispatchers.Main){
                                        clearAllText()
                                        dialogBoxService.successDialog("SUCCESSFULLY REGISTER")
                                    }
                                }else{
                                    withContext(Dispatchers.Main){ dialogBoxService.failedDialog("${doRegisToFirestore()}") }
                                }
                            }else{
                                withContext(Dispatchers.Main){ dialogBoxService.failedDialog("${doRegisAuth()}") }
                            }
                        }else{
                            //KALAU PASSWORD GA SESUAI
                        }
                    }else{
                        //VALIDATE ALL EDIT TEXT GAGAL
                    }
                }
            }
            R.id.registerActivity_btnLogin->{
                onBackPressed()
            }
        }
    }

    private suspend fun validateAllEditText(): Boolean {
        var isTrue:Boolean = false
        withContext(Dispatchers.Main){
            if (inputFullname.text!!.trim().isNotEmpty() && inputEmail.text!!.trim().isNotEmpty()
                && inputPassword.text!!.trim().isNotEmpty() && inputConfPassword.text!!.trim().isNotEmpty()){
                isTrue =true
            }else if (inputFullname.text?.trim().isNullOrEmpty()){
                inputFullname.error = "REQUIRED"
                isTrue = false
            }else if (inputEmail.text?.trim().isNullOrEmpty()){
                inputEmail.error = "REQUIRED"
                isTrue = false
            }else if (inputPassword.text?.trim().isNullOrEmpty()){
                inputPassword.error = "REQUIRED"
                isTrue = false
            }else if (inputConfPassword.text?.trim().isNullOrEmpty()){
                inputConfPassword.error = "REQUIRED"
                isTrue = false
            }else{
                isTrue = false
            }
        }
        return isTrue
    }

    private suspend fun doRegisAuth(): String {
        var authenticationService = AuthenticationService()
        return authenticationService.regisData(inputEmail.text.toString(), inputPassword.text.toString())
    }

    private suspend fun checkIsPassAndConfPassIsMatch():Boolean{
        var isMatch = false
        withContext(Dispatchers.Main){
            if (inputPassword.text.toString()==inputConfPassword.text.toString()){
                isMatch = true
            }else{
                inputPassword.error = "PASSWORD AND CONFIRM PASSWORD IS NOT MATCH"
                isMatch = false
            }
        }
        return isMatch
    }

    private suspend fun doRegisToFirestore(): String {
        var user = HashMap<String, Any>()
        user.put("FULL_NAME", inputFullname.text.toString())
        user.put("EMAIL", inputEmail.text.toString())
        user.put("PASSWORD", inputPassword.text.toString())

        val firestoreService = FirestoreService()
        return firestoreService.setProfileData(user)
    }

    private fun clearAllText(){
        inputFullname.text?.clear()
        inputEmail.text?.clear()
        inputPassword.text?.clear()
        inputConfPassword.text?.clear()
    }

    private fun initializationLayout() {
        inputFullname = findViewById(R.id.registerActivity_inputFullName)
        inputEmail = findViewById(R.id.registerActivity_inputEmail)
        inputPassword = findViewById(R.id.registerActivity_inputPassword)
        inputConfPassword = findViewById(R.id.registerActivity_inputConfPassword)
        btn_regis = findViewById(R.id.registerActivity_btnRegis)
        btn_login = findViewById(R.id.registerActivity_btnLogin)

        btn_regis.setOnClickListener(this)
        btn_login.setOnClickListener(this)
    }
}