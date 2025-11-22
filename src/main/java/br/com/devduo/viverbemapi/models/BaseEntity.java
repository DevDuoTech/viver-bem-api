package br.com.devduo.viverbemapi.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@MappedSuperclass
@EntityListeners({ AuditingEntityListener.class })
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @CreatedDate
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    protected ZonedDateTime createdAt;

    @LastModifiedDate
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    protected ZonedDateTime updatedAt;

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, updatedAt);
    }
}