package me.hoyeon.shortlink.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class BlackListedToken {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Long id;

  @Column(nullable = false, unique = true)
  private String token;

  private LocalDateTime expiredAt;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public LocalDateTime getExpiredAt() {
    return expiredAt;
  }

  public void setExpiredAt(LocalDateTime expiredAt) {
    this.expiredAt = expiredAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlackListedToken that = (BlackListedToken) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "BlackListedToken{"
        + "id=" + id
        + ", token='" + token + '\''
        + ", expiredAt=" + expiredAt
        + ", createdAt=" + createdAt + '}';
  }
}
