package com.blndr.nabeletebackend.model.Holders;

import com.blndr.nabeletebackend.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
@Table(name = "registration_requests")
public class RegistrationRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int registrationRequestId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;
    private String uuid;

    public RegistrationRequest() {
    }

    public RegistrationRequest(User user, String uuid) {
        this.user = user;
        this.expirationTime = Date.from(Instant.now().plus(10, ChronoUnit.MINUTES));
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getRegistrationRequestId() {
        return registrationRequestId;
    }

    public void setRegistrationRequestId(int registrationRequestId) {
        this.registrationRequestId = registrationRequestId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}
