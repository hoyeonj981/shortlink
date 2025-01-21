package me.hoyeon.shortlink;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DummyClassTest {

  @Test
  void adding_test() {
    var dummyClass = new DummyClass(1);
    Assertions.assertThat(dummyClass.addOne()).isEqualTo(1);
  }
}