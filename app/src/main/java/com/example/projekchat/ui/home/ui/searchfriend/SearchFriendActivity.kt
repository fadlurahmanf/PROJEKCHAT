package com.example.projekchat.ui.home.ui.searchfriend

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFriendActivity : AppCompatActivity() {
    private lateinit var inputSearchFriend:EditText
    private lateinit var recycleView:RecyclerView
    private lateinit var loading:ProgressBar
    private lateinit var emptytext:TextView

    private var listUserResponse = ArrayList<UserResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_friend)
        initializationLayout()

        doSearch()
        setAdapter(listUserResponse)

    }

    private fun setAdapter(listUser:ArrayList<UserResponse>){
        recycleView.layoutManager = LinearLayoutManager(this)
        var adapter = SearchFriendAdapter(listUser)
        recycleView.adapter = adapter

        adapter.setOnItemClickCallback(object :SearchFriendAdapter.OnItemClickCallback{
            @SuppressLint("ResourceType")
            override fun onItemClicked(data: UserResponse) {
                //FRIEND STATUS CLICKED
                var message:String = FirestoreService.FAIL
                GlobalScope.launch {
                    var result = sendingInvitation("${getCurrentUser()}", "${data.email}")
                    if (result==FirestoreService.SUCCESS){
                        //SNACKBAR
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            withContext(Dispatchers.Main){
                                Snackbar.make(requireViewById(R.id.itemFriend_friendStatus), "INVITATION SENT", Snackbar.ANIMATION_MODE_SLIDE).show()
                            }
                        }
                    }
                }
            }

        })
    }
    private suspend fun sendingInvitation(emailUser:String, emailFriend:String): String {
        val firestoreService =FirestoreService()
        return firestoreService.sendingInvitation("${emailUser}", "$emailFriend")
    }

    private suspend fun getAllUserByKey(keysearch:String): ArrayList<UserResponse> {
        val firestoreService = FirestoreService()
        val listUser = ArrayList<UserResponse>()
        firestoreService.getAllUser()?.forEach {
            if (getCurrentUser()!=it["EMAIL"].toString()){
                if ("${it["EMAIL"].toString()}".contains("${keysearch}")){
                    listUser.add(UserResponse(
                        "${it["FULL_NAME"].toString()}",
                        "${it["EMAIL"].toString()}",
                        status = "${it["STATUS"].toString()}"
                    ))
                }
            }
        }
        return listUser
    }

    private fun doSearch() {
        inputSearchFriend.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId==EditorInfo.IME_ACTION_GO){
                    if (inputSearchFriend.text.toString().length>0){
                        GlobalScope.launch {
                            //DO SEARCH
                            withContext(Dispatchers.Main){
                                loading.visibility = View.VISIBLE
                                emptytext.visibility = View.INVISIBLE
                            }
                            var result = getAllUserByKey(inputSearchFriend.text.toString())
                            withContext(Dispatchers.Main){
                                setAdapter(result)
                                if (result.isEmpty()){
                                    loading.visibility = View.INVISIBLE
                                    emptytext.visibility = View.VISIBLE
                                }else{
                                    loading.visibility = View.INVISIBLE
                                    emptytext.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
                return false
            }
        })
    }

    private suspend fun getListFriend(){
//        val firestoreService = FirestoreService()
//        firestoreService.getListFriedUser("${getCurrentUser()}")
    }

    private fun getCurrentUser(): String? {
        var authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }

    private fun initializationLayout() {
        inputSearchFriend = findViewById(R.id.searchFriendActivity_inputSearch)
        recycleView = findViewById(R.id.searchFriendActivity_recycleView)
        emptytext = findViewById(R.id.searchFriendActivity_empty)
        loading = findViewById(R.id.searchFriendActivity_loading)
    }


}