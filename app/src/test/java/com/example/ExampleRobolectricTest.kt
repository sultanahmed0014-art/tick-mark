package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FeedbackStatus
import com.example.data.TickmarkRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Tickmark", appName)
  }

  @Test
  fun `verify repository initializes signals across multi-market assets`() {
    val repository = TickmarkRepository()
    val signals = repository.signals.value
    assertTrue("Signals list should not be empty", signals.isNotEmpty())
    assertTrue("Should include US Stocks", signals.any { it.market == "US Stocks" })
    assertTrue("Should include PSX Pakistan Stocks", signals.any { it.market == "PSX Pakistan" })
    assertTrue("Should include Forex", signals.any { it.market == "Forex" })
    assertTrue("Should include Crypto", signals.any { it.market == "Crypto" })
    assertTrue("Should include Commodities", signals.any { it.market == "Commodities" })
  }

  @Test
  fun `verify authenticated administrator is Muhammad Talha Mazhar and admin controls function`() {
    val repository = TickmarkRepository()
    val profile = repository.userProfile.value
    assertEquals("Muhammad Talha Mazhar", profile.name)
    assertEquals("talha.mazhar@tickmark.pro", profile.email)

    // Test feedback management
    val feedbacks = repository.userFeedbacks.value
    assertTrue("Initial feedback list should not be empty", feedbacks.isNotEmpty())
    val firstFb = feedbacks.first()
    repository.updateFeedbackStatus(firstFb.id, FeedbackStatus.RESOLVED, "Resolution verified by Muhammad Talha Mazhar")
    val updatedFb = repository.userFeedbacks.value.find { it.id == firstFb.id }
    assertNotNull(updatedFb)
    assertEquals(FeedbackStatus.RESOLVED, updatedFb?.status)
    assertEquals("Resolution verified by Muhammad Talha Mazhar", updatedFb?.adminReply)

    // Test system status controls
    val initialStatus = repository.systemStatus.value
    assertEquals(false, initialStatus.isMaintenanceMode)
    repository.toggleMaintenanceMode()
    assertEquals(true, repository.systemStatus.value.isMaintenanceMode)
    repository.toggleMaintenanceMode()
    assertEquals(false, repository.systemStatus.value.isMaintenanceMode)
  }
}
