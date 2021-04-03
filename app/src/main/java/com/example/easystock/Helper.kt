package com.example.easystock

import android.util.Log
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.database.*

class Helper {

    companion object{

        private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        private var reference: DatabaseReference = database.getReference("Users")
        private var idUser:Long = 0

        fun encrypt(data:String): String{
            val passHash = BCrypt.withDefaults().hashToString(12, data.toCharArray())
            return passHash
        }

        fun getIdCount(): Long{
            reference.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Cancel", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        idUser = snapshot.childrenCount
                    }
                }
            })
            return idUser
        }



    }
}