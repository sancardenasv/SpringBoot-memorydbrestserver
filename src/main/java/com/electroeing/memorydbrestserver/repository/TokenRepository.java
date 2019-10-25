package com.electroeing.memorydbrestserver.repository;

import com.electroeing.memorydbrestserver.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    public Token findByUserId(Long userId);

}
