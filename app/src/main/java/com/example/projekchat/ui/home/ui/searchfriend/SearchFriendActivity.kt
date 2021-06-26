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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.SearchResponse
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

    private var listUserResponse = ArrayList<SearchResponse>()
    private var mapUser = HashMap<String, UserResponse>()
    private var adapter = SearchFriendAdapter()

    private lateinit var viewModel: SearchFriendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_friend)
        initializationLayout()

        viewModel.mapUser.observe(this, Observer {
            mapUser = it
            doSearch()
        })

    }

    private fun setAdapter(listUser:ArrayList<SearchResponse>){
        adapter.setListUser(listUser)
        recycleView.adapter = adapter

        adapter.setOnItemClickCallback(object :SearchFriendAdapter.OnItemClickCallback{
            @SuppressLint("ResourceType")
            override fun onItemClicked(data: SearchResponse) {
                //FRIEND STATUS CLICKED
                var userResponseFriend = UserResponse(
                        data.friendFullname,
                        data.friendEmail,
                        "",
                        data.friendStatus,
                        data.friendProfile,
                        data.friendToken
                )
                var userResponse = mapUser["${getCurrentUser()}"]
                GlobalScope.launch {
                    var result = sendingInvitation(userResponse!!, userResponseFriend)
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
    private suspend fun sendingInvitation(invitationBy:UserResponse, invitationTo:UserResponse): String {
        val firestoreService =FirestoreService()
        return firestoreService.sendingInvitation(invitationBy, invitationTo)
    }

    private suspend fun getAllUserByKey(keysearch:String): ArrayList<SearchResponse> {
        val firestoreService = FirestoreService()
        val listSearch = ArrayList<SearchResponse>()
        firestoreService.getAllUser()?.forEach {
            if (getCurrentUser()!=it["EMAIL"].toString()){
                if ("${it["EMAIL"].toString()}".contains("${keysearch}")){
                    if (getListFriend().contains("${it["EMAIL"].toString()}")){
                        listSearch.add(SearchResponse(
                                friendFullname = "${it["FULL_NAME"].toString()}",
                                friendEmail = "${it["EMAIL"].toString()}",
                                friendProfile = "${it["PROFILE_IMAGE"]}",
                                friendStatus = it?.get("STATUS").toString(),
                                status = 1,
                                friendToken = it?.get("TOKEN").toString()
                        ))
                    }else{
                        listSearch.add(SearchResponse(
                                friendFullname = "${it["FULL_NAME"].toString()}",
                                friendEmail = "${it["EMAIL"].toString()}",
                                friendProfile = "${it["PROFILE_IMAGE"]}",
                                friendStatus = it?.get("STATUS").toString(),
                                status = 0,
                                friendToken = it?.get("TOKEN").toString()
                        ))
                    }
                }
            }
        }
        return listSearch
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

    private suspend fun getListFriend(): ArrayList<String> {
        var list = ArrayList<String>()
        val firestoreService = FirestoreService()
        var result = firestoreService.getListFriedUser("${getCurrentUser()}")
        result?.forEach {
            list.add(it.id)
        }
        return list
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

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[SearchFriendViewModel::class.java]

        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = adapter
    }


}