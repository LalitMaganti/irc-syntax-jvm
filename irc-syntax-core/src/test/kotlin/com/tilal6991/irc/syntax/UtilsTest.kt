package com.tilal6991.irc.syntax

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.lang.reflect.Modifier

class UtilsTest {

  @Test fun getOrNullTest() {
    assertThat(Utils.getOrNull(listOf(), 0)).isNull()
    assertThat(Utils.getOrNull(oneItemList(), 0)).isEqualTo(oneItemList()[0])
    assertThat(Utils.getOrNull(oneItemList(), 1)).isNull()
  }

  @Test fun testConstructorIsPrivate() {
    val constructor = Utils::class.java.getDeclaredConstructor()
    assertThat(Modifier.isPrivate(constructor.modifiers)).isTrue()
    constructor.isAccessible = true
    constructor.newInstance()
  }
}