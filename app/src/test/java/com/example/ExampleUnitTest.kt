package com.example

import com.example.data.model.NutritionStatus
import com.example.data.parser.CsvParser
import com.example.data.parser.SampleDataGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun csvParser_parsesSampleRecordsCorrectly() {
        val result = CsvParser.parseText(SampleDataGenerator.sampleCsvTemplate)
        assertTrue("CSV errors: ${result.errors}", result.errors.isEmpty())
        assertEquals(2, result.successfulRecords.size)

        val first = result.successfulRecords.first()
        assertEquals("AW-042-04", first.childId)
        assertEquals("Ishaan Nilesh Kadam", first.name)
        assertEquals(19, first.ageMonths)
        assertEquals(10.6f, first.weightKg, 0.01f)
        assertEquals(82.0f, first.heightCm, 0.01f)
        assertEquals(NutritionStatus.NORMAL, first.nutritionStatus)
    }

    @Test
    fun prdChildProfiles_containCorrectData() {
        val profiles = SampleDataGenerator.getInitialProfiles()
        assertEquals(3, profiles.size)

        val aarav = profiles.first { it.childId == "AW-042-01" }
        assertEquals("Aarav Sachin Gaikwad", aarav.name)
        assertEquals("9890123456", aarav.parentContact)
        assertEquals("ACT-7842-AG", aarav.regCode)

        val ananya = profiles.first { it.childId == "AW-042-02" }
        assertEquals("Ananya Rahul Meshram", ananya.name)
        assertEquals("ACT-9912-AM", ananya.regCode)

        val vihaan = profiles.first { it.childId == "AW-042-03" }
        assertEquals("Vihaan Pramod Shinde", vihaan.name)
        assertEquals("ACT-3301-VS", vihaan.regCode)
    }

    @Test
    fun nutritionStatus_evaluation() {
        val normalChild = NutritionStatus.evaluate(24, 12.2f)
        assertEquals(NutritionStatus.NORMAL, normalChild)

        val samChild = NutritionStatus.evaluate(24, 7.0f)
        assertEquals(NutritionStatus.SAM, samChild)

        val mamChild = NutritionStatus.evaluate(24, 9.0f)
        assertEquals(NutritionStatus.MAM, mamChild)
    }
}
