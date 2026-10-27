package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.NutritionStatus
import com.example.data.parser.SampleDataGenerator
import com.example.data.parser.XmlParser
import org.junit.Assert.assertEquals
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
    assertEquals("Anganwadi Connect", appName)
  }

  @Test
  fun `xmlParser parses sample records correctly under Robolectric`() {
    val result = XmlParser.parseText(SampleDataGenerator.sampleXmlText)
    assertTrue("XML errors: ${result.errors}", result.errors.isEmpty())
    assertEquals(2, result.successfulRecords.size)

    val first = result.successfulRecords.first()
    assertEquals("AW-042-06", first.childId)
    assertEquals("Meera Yadav", first.name)
    assertEquals(20, first.ageMonths)
    assertEquals(9.2f, first.weightKg, 0.01f)
    assertEquals(NutritionStatus.MAM, first.nutritionStatus)
  }

  @Test
  fun `mainActivity launches without crashing`() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
    val activity = controller.get()
    org.junit.Assert.assertNotNull(activity)
  }
}
