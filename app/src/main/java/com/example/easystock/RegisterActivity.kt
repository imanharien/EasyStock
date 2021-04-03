package com.example.easystock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var mGoogleSignInClient:GoogleSignInClient
    private lateinit var auth:FirebaseAuth
    private var registered:Boolean = false
    private val RC_SIGN_IN: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        createRequestGoogle()

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users")

        //event click on button 'sign up'
        findViewById<Button>(R.id.btnSignUp).setOnClickListener() {
            signUpUser()
        }

        findViewById<ImageButton>(R.id.imagebtnGoogleAccount).setOnClickListener(){
            signIn()
        }

        //event click on textView 'login' to start activity LoginActivity
        findViewById<TextView>(R.id.tvLogin).setOnClickListener() {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }        
    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val user = auth.currentUser
//        if(user!=null){
//            intent = Intent(applicationContext, HomeActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }


    //method to save user data on database
    private fun signUpUser(){
        val no_telp = etNoTelpRegisterActivity.text.toString().trim()
        val username = etUsernameRegisterActivity.text.toString().trim()
        val email = etEmailRegisterActivity.text.toString().trim()
        val password = etPasswordRegisterActivity.text.toString().trim()
        val confirm_password = etConfirmPasswordRegisterActivity.text.toString().trim()

        //verification input No. Telp
        if(no_telp.isEmpty()){
            etNoTelpRegisterActivity.error = "Silahkan Masukkan No.Telp"
            etNoTelpRegisterActivity.requestFocus()
            return
        }
        if (!Patterns.PHONE.matcher(no_telp).matches()){
            etNoTelpRegisterActivity.error = "Silahkan Masukkan No.Telp yang Valid"
            etNoTelpRegisterActivity.requestFocus()
            return
        }
        //verification input Username
        if(username.isEmpty()){
            etUsernameRegisterActivity.error = "Silahkan Masukkan Username"
            etUsernameRegisterActivity.requestFocus()
            return
        }
        //verification input Email
        if(email.isEmpty()){
            etEmailRegisterActivity.error = "Silahkan Masukkan Email"
            etEmailRegisterActivity.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmailRegisterActivity.error = "Silahkan Masukkan Email yang Valid"
            etEmailRegisterActivity.requestFocus()
            return
        }
        //verification input Password
        if(password.isEmpty()){
            etPasswordRegisterActivity.error = "Silahkan Masukkan Password"
            etPasswordRegisterActivity.requestFocus()
            return
        }
        //verification input Confirm Password
        if(confirm_password.isEmpty()){
            etConfirmPasswordRegisterActivity.error = "Silahkan Masukkan Confirm Password"
            etConfirmPasswordRegisterActivity.requestFocus()
            return
        }
        //verification input Password and Confirm Password
        if(!password.equals(confirm_password)){
            etConfirmPasswordRegisterActivity.error = "Silahkan Masukkan Confirm Password yang sesuai dengan Password"
            etConfirmPasswordRegisterActivity.requestFocus()
            return
        }
        //create object class UserModel
        val userModel = UserModel(no_telp, username, email, Helper.encrypt(password))

        //check account data has been registered or no
        checkAccountRegistered(email)
        if(!registered){
            //push object data to database with auto increment id
            reference.child((Helper.getIdCount()+1).toString()).setValue(userModel)
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                    reload()
                }
                .addOnFailureListener{
                    Toast.makeText(baseContext, "Registrasi Gagal!", Toast.LENGTH_SHORT).show()
                }
        } else {
            registered = false
            Toast.makeText(baseContext, "Registrasi Gagal\nEmail telah terdaftar sebelumnya", Toast.LENGTH_SHORT).show()
        }

        //push object data to database with id unique
//        var id = reference.push().key
//        reference.child(id!!).setValue(userModel)
//            .addOnSuccessListener {
//                Toast.makeText(baseContext, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener{
//                Toast.makeText(baseContext, "Registrasi Gagal!", Toast.LENGTH_SHORT).show()
//            }
//        reload()
    }

    //method to check account data has been registered or no
    fun checkAccountRegistered(email:String?){
        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel", error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var index = 1
                    while (index <= Helper.getIdCount()){
                        val emailDB = snapshot.child((index).toString()).child("email").getValue().toString()
                        if(email.equals(emailDB)){
                            registered = true
                            break
                        }
                        index++
                    }
                }
            }
        })
    }

    //method to clean value editText
    private fun reload(){
        etNoTelpRegisterActivity.setText("")
        etUsernameRegisterActivity.setText("")
        etEmailRegisterActivity.setText("")
        etPasswordRegisterActivity.setText("")
        etConfirmPasswordRegisterActivity.setText("")
    }

    private fun createRequestGoogle(){
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    intent = Intent(applicationContext, HomeActivity::class.java)
                    intent.putExtra("loginType","google")
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Sign in fails", Toast.LENGTH_SHORT).show()
                }
            }
    }

}