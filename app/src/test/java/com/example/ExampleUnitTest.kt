package com.example

import com.example.data.repository.PakAiRepository
import com.example.utils.LanguageList
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun developerIdentity_matchesMuhammadAli() {
    assertEquals("Muhammad Ali", PakAiRepository.DEVELOPER_NAME)
    assertEquals("alimuhammadhvn81@gmail.com", PakAiRepository.DEVELOPER_EMAIL)
  }

  @Test
  fun languageList_containsOver100Languages() {
    assertTrue("Language list should contain at least 100 languages", LanguageList.languages.size >= 100)
    
    // Verify key regional and international languages are present
    val languageNames = LanguageList.languages.map { it.name }
    assertTrue(languageNames.contains("English"))
    assertTrue(languageNames.contains("Urdu"))
    assertTrue(languageNames.contains("Pashto"))
    assertTrue(languageNames.contains("Punjabi"))
    assertTrue(languageNames.contains("Sindhi"))
    assertTrue(languageNames.contains("Arabic"))
    assertTrue(languageNames.contains("Chinese (Simplified)"))
    assertTrue(languageNames.contains("French"))
    assertTrue(languageNames.contains("German"))
    assertTrue(languageNames.contains("Spanish"))
    assertTrue(languageNames.contains("Japanese"))
    assertTrue(languageNames.contains("Russian"))
  }
}
