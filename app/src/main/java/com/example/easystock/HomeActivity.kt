package com.example.easystock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle: Bundle? = intent.extras
        val loginType = bundle?.get("loginType")
        if(loginType.toString().equals("manual")){
            val noTelp = bundle?.get("noTelp")
            val username = bundle?.get("username")
            val email = bundle?.get("email")
            findViewById<TextView>(R.id.textView).setText(username.toString())
        } else if(loginType.toString().equals("google")){
            var signInAccount = GoogleSignIn.getLastSignedInAccount(this)
            if(signInAccount!=null){
                textView.setText(signInAccount.email)
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener(){
            if(loginType.toString().equals("google")){
                Firebase.auth.signOut()
            }
            intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}