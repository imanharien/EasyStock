package com.example.easystock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.btnLogin).setOnClickListener() {
            this.login()
        }

        findViewById<TextView>(R.id.tvDaftarAkun).setOnClickListener() {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun login(){
        //email
        if(etEmailLoginActivity.text.toString().isEmpty()){
            etEmailLoginActivity.error = "Silahkan Masukkan Email"
            etEmailLoginActivity.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(etEmailLoginActivity.text.toString()).matches()){
            etEmailLoginActivity.error = "Silahkan Masukkan Email yang Valid"
            etEmailLoginActivity.requestFocus()
            return
        }
        //Password
        if(etPasswordLoginActivity.text.toString().isEmpty()){
            etPasswordLoginActivity.error = "Silahkan Masukkan Password"
            etPasswordLoginActivity.requestFocus()
            return
        }
        //process
        val email = etEmailLoginActivity.text.toString().trim()
        val password = etPasswordLoginActivity.text.toString().trim()

        db.database.getReference("Users/$email/")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach{
                        val post = it.getValue(UserModel::class.java)
                        val emailPost = post!!.email
                        val passwordPost = post!!.password
                        if(email == emailPost){
                            val hash = passwordPost
                            val hasil = BCrypt.verifyer().verify(password.toCharArray(), hash)
                            if(hasil.verified){
                                intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                intent.putExtra("email", snapshot.child("email").value.toString())
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(baseContext, "Password Salah!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    Toast.makeText(baseContext, "Akun belum terdaftar", Toast.LENGTH_SHORT).show()
                    reload()
                }
            })
    }

    fun reload(){
        etEmailLoginActivity.setText("")
        etPasswordLoginActivity.setText("")
    }
}