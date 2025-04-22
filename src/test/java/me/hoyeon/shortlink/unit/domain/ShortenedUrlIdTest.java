package me.hoyeon.shortlink.unit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import me.hoyeon.shortlink.domain.InvalidUrlIdException;
import me.hoyeon.shortlink.domain.ShortenedUrlId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ShortenedUrlIdTest {

  @DisplayName("유효한 양수 id 값으로 생성할 수 있다")
  @Test
  void createWithValidPositiveId() {
    var givenId = 1L;

    var id = new ShortenedUrlId(givenId);

    assertThat(id.getValue()).isEqualTo(givenId);
  }

  @DisplayName("0이나 음수 id 값은 예외가 발생한다")
  @ParameterizedTest
  @ValueSource(longs = {0, -1})
  void throwExceptionIfIdValueIsNegativeOrZero(long givenId) {
    assertThatThrownBy(() -> new ShortenedUrlId(givenId))
        .isInstanceOf(InvalidUrlIdException.class);
  }

  @DisplayName("id 값이 null일 경우 예외가 발생한다")
  @Test
  void throwExceptionIfIdValueIsNull() {
    assertThatThrownBy(() -> new ShortenedUrlId(null))
        .isInstanceOf(InvalidUrlIdException.class);
  }

  @DisplayName("같은 값을 가진 id는 동일하다")
  @Test
  void sameIdObjectIfValueIsSame() {
    var givenId1 = 1L;
    var givenId2 = 1L;

    var id1 = new ShortenedUrlId(givenId1);
    var id2 = new ShortenedUrlId(givenId2);

    assertThat(givenId1).isEqualTo(givenId2);
    assertThat(id1).isEqualTo(id2);
  }

  @DisplayName("다른 값을 가진 id는 다르다")
  @Test
  void differentIdObjectIfValueIsNotSame() {
    var givenId1 = 1L;
    var givenId2 = 2L;

    var id1 = new ShortenedUrlId(givenId1);
    var id2 = new ShortenedUrlId(givenId2);

    assertThat(givenId1).isNotEqualTo(givenId2);
    assertThat(id1).isNotEqualTo(id2);
  }
}
