package me.hoyeon.shortlink.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@DiscriminatorColumn(
    discriminatorType = DiscriminatorType.STRING,
    name = "member_type",
    columnDefinition = "VARCHAR(20)"
)
public abstract class MemberBaseEntity {

  protected boolean isDeleted = false;

  private String memberType;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  public void delete() {
    this.isDeleted = true;
  }

  public String getMemberType() {
    return memberType;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
