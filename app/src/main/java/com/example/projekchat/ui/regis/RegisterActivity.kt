package com.example.projekchat.ui.regis

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.projekchat.R
import com.example.projekchat.services.firestore.FirestoreService
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.storage.FirebaseStorageServices
import com.example.projekchat.ui.dialogbox.DialogBoxService
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var inputFullname:TextInputEditText
    private lateinit var inputEmail:TextInputEditText
    private lateinit var inputPassword:TextInputEditText
    private lateinit var inputConfPassword:TextInputEditText
    private lateinit var btn_regis:Button
    private lateinit var btn_login:Button
    private lateinit var image:ShapeableImageView

    private var imageProfileUser: Uri? = null

    companion object{
        const val REQUEST_CODE_PICK_IMAGE_FROM_GALLERY = 100
    }

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
                                    if (imageProfileUser!=null){
                                        saveImageToFirestore(imageProfileUser, inputEmail.text.toString())
                                    }else{
                                        saveImageToFirestore(null, "null")
                                    }
                                    withContext(Dispatchers.Main){
                                        clearAllText()
                                        image.setContentPadding(15,15,15,15)
                                        image.setImageResource(R.drawable.ic_person_black)
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
            R.id.registerActivity_inputProfileImage->{
                pickImageFromGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode== REQUEST_CODE_PICK_IMAGE_FROM_GALLERY && data!=null){
            image.setImageURI(data?.data)
            image.setContentPadding(0,0,0,0)
            imageProfileUser = data?.data
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE_FROM_GALLERY)
    }

    private suspend fun saveImageToFirestore(image:Uri?, email:String){
        val service = FirebaseStorageServices()
        service.saveImage(image, email)
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
        user.put("PROFILE_IMAGE", "${inputEmail.text.toString()}.png")

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
        image = findViewById(R.id.registerActivity_inputProfileImage)

        btn_regis.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        image.setOnClickListener(this)
    }
}