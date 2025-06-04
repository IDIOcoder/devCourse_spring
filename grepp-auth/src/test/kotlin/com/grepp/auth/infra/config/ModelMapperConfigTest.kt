package com.grepp.auth.infra.config

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ModelMapperTest{

    @Test
    fun testMapper(){
        
    }

}

data class SignupRequest(
    val userId:String,
    val password:String,
    val email:String,
    val tel:String
)