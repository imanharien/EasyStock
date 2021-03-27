package com.example.easystock

import at.favre.lib.crypto.bcrypt.BCrypt

class encryption {

    companion object{

        fun encrypt(data:String): String{
            val passHash = BCrypt.withDefaults().hashToString(12, data.toCharArray())
            return passHash
        }
    }
}