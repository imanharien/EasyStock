package com.example.easystock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if(user!=null){
            intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("loginType","google")
            startActivity(intent)
            finish()
        }

        //event click on button 'get started' to start activity LoginActivity
        findViewById<Button>(R.id.btnGetStartedMainActivity).setOnClickListener() {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}