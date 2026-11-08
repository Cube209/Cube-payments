package com.example

import com.example.data.model.Cube
import com.example.data.model.Currencies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun currencyFormatting_isCorrect() {
    assertEquals("$49.00", Currencies.format(49.0, "USD"))
    assertEquals("¥5000", Currencies.format(5000.0, "JPY"))
    assertEquals("€19.99", Currencies.format(19.99, "EUR"))
  }

  @Test
  fun cubeExpirationCheck_works() {
    val permanentCube = Cube(
      id = "test-1",
      creatorId = "c1",
      name = "Permanent Item",
      description = "No expiration",
      price = 10.0,
      currency = "USD",
      status = "PUBLISHED",
      expirationType = "NEVER",
      expiresAt = 0L
    )
    assertFalse(permanentCube.isExpired)
    assertTrue(permanentCube.canReceivePurchases)

    val expiredCube = Cube(
      id = "test-2",
      creatorId = "c1",
      name = "Expired Item",
      description = "Expired in the past",
      price = 10.0,
      currency = "USD",
      status = "PUBLISHED",
      expirationType = "CUSTOM",
      expiresAt = System.currentTimeMillis() - 10000L
    )
    assertTrue(expiredCube.isExpired)
    assertFalse(expiredCube.canReceivePurchases)
  }
}
