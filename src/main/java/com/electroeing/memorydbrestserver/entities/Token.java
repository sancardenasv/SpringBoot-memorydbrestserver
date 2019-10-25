package com.electroeing.memorydbrestserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "token")
public class Token {
    @Id
    @Column
    @GeneratedValue
    private Long id;
    @Column
    private Long userId;
    @Column
    private String tokenString;
    @Column
    private Date expirationDate;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Token{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", tokenString='").append(tokenString).append('\'');
        sb.append(", expirationDate=").append(expirationDate);
        sb.append('}');
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
