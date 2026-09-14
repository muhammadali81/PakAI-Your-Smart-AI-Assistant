package com.example.data.model

data class QuranAyahModel(
    val surahNumber: Int,
    val surahNameUrdu: String,
    val surahNameEnglish: String,
    val ayahNumber: Int,
    val arabicText: String,
    val urduTranslation: String,
    val englishTranslation: String,
    val tafseerTaqiUsmani: String,
    val topic: String
)

data class HadithModel(
    val id: Int,
    val book: String,
    val hadithNumber: String,
    val narrator: String,
    val arabicText: String,
    val urduTranslation: String,
    val englishTranslation: String,
    val grading: String = "صحیح (Sahih)",
    val tashreeh: String,
    val category: String
)

data class MasnoonDuaModel(
    val id: Int,
    val titleUrdu: String,
    val titleEnglish: String,
    val occasion: String,
    val arabicText: String,
    val urduTranslation: String,
    val englishTranslation: String,
    val reference: String,
    val fazeelat: String,
    val category: String
)

data class IslamicAiResponse(
    val arabicText: String,
    val urduTranslation: String,
    val englishTranslation: String,
    val tafseerOrTashreeh: String,
    val reference: String,
    val scholarNotes: String = "مفتی محمد تقی عثمانی صاحب کی تحقیق و تفسیر کے مطابق"
)
