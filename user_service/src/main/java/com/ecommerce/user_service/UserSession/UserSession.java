package com.ecommerce.user_service.UserSession;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "usersession")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionId;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private LocalDate creationDate;
    @Column(nullable = false)
    private LocalTime creationTime;
    @Column(nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    private LocalTime endTime;
    @Column(nullable = false)
    private LocalTime maxSessionTime;
    @Column(nullable = false)
    private boolean expired;

    public UserSession() {
    }

    public UserSession(int sessionId, String username, LocalDate creationDate, LocalTime creationTime, LocalDate endDate, LocalTime endTime, LocalTime maxSessionTime, boolean expired) {
        this.sessionId = sessionId;
        this.username = username;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.maxSessionTime = maxSessionTime;
        this.expired = expired;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalTime creationTime) {
        this.creationTime = creationTime;
    }


    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getMaxSessionTime() {
        return maxSessionTime;
    }

    public void setMaxSessionTime(LocalTime maxSessionTime) {
        this.maxSessionTime = maxSessionTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
