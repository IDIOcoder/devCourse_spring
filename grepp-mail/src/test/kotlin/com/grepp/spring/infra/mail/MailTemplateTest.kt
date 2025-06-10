package com.grepp.spring.infra.mail

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class MailTemplateTest{

    @Autowired
    lateinit var template: MailTemplate

    @Test
    fun testSend() = runBlocking{


    }
}