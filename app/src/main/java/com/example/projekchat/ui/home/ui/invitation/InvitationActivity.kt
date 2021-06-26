package com.example.projekchat.ui.home.ui.invitation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvitationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText:TextView
    private lateinit var loading:ProgressBar

    private var listUser = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invitation)
        supportActionBar?.title = "Invitation"
        initializationLayout()

        GlobalScope.launch {
            withContext(Dispatchers.Main){
                loading.visibility = View.VISIBLE
                emptyText.visibility = View.INVISIBLE
                if (getAllInvitation().isEmpty()){
                    loading.visibility = View.INVISIBLE
                    emptyText.visibility =View.VISIBLE
                }else{
                    loading.visibility = View.INVISIBLE
                    emptyText.visibility = View.INVISIBLE
                }
                setAdapter()
            }
        }
    }

    private suspend fun setAdapter(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        var adapter =InvitationAdapter(getAllInvitation())
        recyclerView.adapter = adapter

        adapter.setOnItemClickCallback(object :InvitationAdapter.OnItemClickCallback{
            @SuppressLint("WrongConstant")
            override fun onItemClicked(user: String, position: Int) {
                GlobalScope.launch {
                    FirestoreService().setFriend("${getCurrentUser().toString()}", "${user.toString()}")
                    var result = FirestoreService().removeInvitation("${getCurrentUser().toString()}", "${user}")
                    withContext(Dispatchers.Main){
                        if (result==FirestoreService.SUCCESS){
                            var list = getAllInvitation()
                            recyclerView.adapter = InvitationAdapter(list)
                            Snackbar.make(this@InvitationActivity.recyclerView, "ADDED TO FRIEND", Snackbar.ANIMATION_MODE_SLIDE).show()
                        }
                    }
                }
            }

        })
    }

    private fun initializationLayout() {
        recyclerView = findViewById(R.id.invitationActivity_recycleView)
        emptyText = findViewById(R.id.invitationActivity_empty)
        loading = findViewById(R.id.invitationActivity_loading)

    }

    private suspend fun getCurrentUser(): String? {
        var authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }

    private suspend fun getAllInvitation(): ArrayList<String> {
        var listInvitation = ArrayList<String>()
        var firestoreService = FirestoreService()
        var result = firestoreService.getAllInvitation("${getCurrentUser().toString()}")
        result?.documents?.forEach {
            listInvitation.add(it.id)
        }
        return listInvitation
    }


}