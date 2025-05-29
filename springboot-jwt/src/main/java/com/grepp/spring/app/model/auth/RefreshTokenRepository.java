package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.model.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
