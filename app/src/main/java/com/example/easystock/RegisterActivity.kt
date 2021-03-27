package com.example.easystock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users")

        findViewById<Button>(R.id.btnSignUp).setOnClickListener() {
            signUpUser();
        }

        findViewById<TextView>(R.id.tvLogin).setOnClickListener() {
            intent = Intent(this, LoginActivity::class.java);
            startActivity(intent);
        }        
    }

    private fun signUpUser(){
        //No. Telp
        if(etNoTelpRegisterActivity.text.toString().isEmpty()){
            etNoTelpRegisterActivity.error = "Silahkan Masukkan No.Telp"
            etNoTelpRegisterActivity.requestFocus()
            return
        }
        if (!Patterns.PHONE.matcher(etNoTelpRegisterActivity.text.toString()).matches()){
            etNoTelpRegisterActivity.error = "Silahkan Masukkan No.Telp yang Valid"
            etNoTelpRegisterActivity.requestFocus()
            return
        }
        //Email
        if(etEmailRegisterActivity.text.toString().isEmpty()){
            etEmailRegisterActivity.error = "Silahkan Masukkan Email"
            etEmailRegisterActivity.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(etEmailRegisterActivity.text.toString()).matches()){
            etEmailRegisterActivity.error = "Silahkan Masukkan Email yang Valid"
            etEmailRegisterActivity.requestFocus()
            return
        }
        //Password
        if(etPasswordRegisterActivity.text.toString().isEmpty()){
            etPasswordRegisterActivity.error = "Silahkan Masukkan Password"
            etPasswordRegisterActivity.requestFocus()
            return
        }
        //Confirm Password
        if(etConfirmPasswordRegisterActivity.text.toString().isEmpty()){
            etConfirmPasswordRegisterActivity.error = "Silahkan Masukkan Confirm Password"
            etConfirmPasswordRegisterActivity.requestFocus()
            return
        }
        if(etPasswordRegisterActivity.text.toString() != etConfirmPasswordRegisterActivity.text.toString()){
            etConfirmPasswordRegisterActivity.error = "Silahkan Masukkan Confirm Password yang sesuai dengan Password"
            etConfirmPasswordRegisterActivity.requestFocus()
            return
        }
        var userModel = UserModel(
            etNoTelpRegisterActivity.text.toString(),
            etEmailRegisterActivity.text.toString(),
            encryption.encrypt(etPasswordRegisterActivity.text.toString()))

        database.getReference("Users/")

        var id = reference.push().key
        reference.child(id!!).setValue(userModel)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Registrasi Gagal!", Toast.LENGTH_SHORT).show()
            }
        reload()
    }
    private fun reload(){
        etNoTelpRegisterActivity.setText("")
        etEmailRegisterActivity.setText("")
        etPasswordRegisterActivity.setText("")
        etConfirmPasswordRegisterActivity.setText("")
    }
}