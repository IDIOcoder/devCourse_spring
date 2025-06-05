package com.grepp.gateway.security.token.repository

import com.grepp.gateway.security.token.entity.UserBlackList
import org.springframework.data.repository.CrudRepository

interface UserBlackListRepository : CrudRepository<UserBlackList, String>
