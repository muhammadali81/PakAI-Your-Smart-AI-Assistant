package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

data class SlideModel(
    val title: String,
    val subtitle: String = "",
    val bullets: List<String> = emptyList(),
    val notes: String = "",
    val visualContent: String = "Visual Diagram & Analytics Summary"
)

data class PptDeckModel(
    val title: String,
    val subtitle: String = "Created with Pak AI Presentation Studio",
    val presenter: String = "Muhammad Ali",
    val themeColor: String = "Emerald", // "Emerald", "Dark", "Gold"
    val slides: List<SlideModel> = emptyList()
)

object PptGeneratorUtils {

    /**
     * Creates a landscape 16:9 PDF slide deck presentation file
     */
    fun createPptPdf(context: Context, deck: PptDeckModel): File {
        val pdfDoc = PdfDocument()
        val slideWidth = 960 // 16:9 standard width
        val slideHeight = 540 // 16:9 standard height

        val isDarkTheme = deck.themeColor != "Gold"
        val bgColor = if (isDarkTheme) Color.rgb(15, 23, 30) else Color.rgb(250, 248, 240)
        val primaryColor = if (isDarkTheme) Color.rgb(0, 255, 136) else Color.rgb(180, 130, 20)
        val textColor = if (isDarkTheme) Color.rgb(240, 245, 250) else Color.rgb(30, 35, 40)
        val mutedColor = if (isDarkTheme) Color.rgb(130, 145, 160) else Color.rgb(100, 105, 110)
        val accentCardColor = if (isDarkTheme) Color.rgb(25, 36, 48) else Color.rgb(235, 230, 215)

        val titlePaint = Paint().apply {
            color = primaryColor
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = mutedColor
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val bulletTitlePaint = Paint().apply {
            color = primaryColor
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = textColor
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val footerPaint = Paint().apply {
            color = mutedColor
            textSize = 10f
            isAntiAlias = true
        }

        deck.slides.forEachIndexed { index, slide ->
            val pageInfo = PdfDocument.PageInfo.Builder(slideWidth, slideHeight, index + 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            // Background
            val bgPaint = Paint().apply { color = bgColor }
            canvas.drawRect(0f, 0f, slideWidth.toFloat(), slideHeight.toFloat(), bgPaint)

            // Top decorative accent line
            val accentPaint = Paint().apply { color = primaryColor }
            canvas.drawRect(0f, 0f, slideWidth.toFloat(), 6f, accentPaint)

            val margin = 50f
            var currentY = 55f

            if (index == 0) {
                // Cover Slide Layout
                val coverCardPaint = Paint().apply { color = accentCardColor }
                canvas.drawRoundRect(margin, 80f, slideWidth - margin, slideHeight - 80f, 20f, 20f, coverCardPaint)

                currentY = 180f
                val bigTitlePaint = Paint().apply {
                    color = primaryColor
                    textSize = 34f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                canvas.drawText(slide.title, margin + 40f, currentY, bigTitlePaint)
                currentY += 40f

                if (slide.subtitle.isNotBlank()) {
                    canvas.drawText(slide.subtitle, margin + 40f, currentY, subtitlePaint)
                    currentY += 35f
                }

                val presenterPaint = Paint().apply {
                    color = textColor
                    textSize = 14f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                canvas.drawText("Presenter: ${deck.presenter}", margin + 40f, currentY, presenterPaint)
                currentY += 22f
                canvas.drawText("Pak AI Presentation Studio • Automated AI Deck", margin + 40f, currentY, subtitlePaint)

            } else {
                // Content Slide Layout
                canvas.drawText(slide.title, margin, currentY, titlePaint)
                currentY += 28f

                if (slide.subtitle.isNotBlank()) {
                    canvas.drawText(slide.subtitle, margin, currentY, subtitlePaint)
                    currentY += 25f
                }

                val linePaint = Paint().apply {
                    color = primaryColor
                    strokeWidth = 2f
                }
                canvas.drawLine(margin, currentY, slideWidth - margin, currentY, linePaint)
                currentY += 35f

                // Bullets in styled cards
                slide.bullets.forEachIndexed { bIndex, bullet ->
                    if (currentY < slideHeight - 70f) {
                        val cardPaint = Paint().apply { color = accentCardColor }
                        canvas.drawRoundRect(margin, currentY - 18f, slideWidth - margin, currentY + 34f, 10f, 10f, cardPaint)

                        canvas.drawText("${bIndex + 1}. ", margin + 16f, currentY + 8f, bulletTitlePaint)

                        // Wrap bullet text
                        val words = bullet.split(" ")
                        var bLine = ""
                        val maxW = slideWidth - margin * 2 - 80f
                        for (w in words) {
                            val test = if (bLine.isEmpty()) w else "$bLine $w"
                            if (bodyPaint.measureText(test) > maxW) {
                                canvas.drawText(bLine, margin + 45f, currentY + 8f, bodyPaint)
                                bLine = w
                            } else {
                                bLine = test
                            }
                        }
                        if (bLine.isNotEmpty()) {
                            canvas.drawText(bLine, margin + 45f, currentY + 8f, bodyPaint)
                        }

                        currentY += 60f
                    }
                }
            }

            // Slide Footer
            canvas.drawText(
                "Pak AI Presentation Studio • Developed by Muhammad Ali",
                margin,
                slideHeight - 20f,
                footerPaint
            )
            val slideNumStr = "${index + 1} / ${deck.slides.size}"
            canvas.drawText(
                slideNumStr,
                slideWidth - margin - footerPaint.measureText(slideNumStr),
                slideHeight - 20f,
                footerPaint
            )

            pdfDoc.finishPage(page)
        }

        val fileName = "PakAI_Presentation_${deck.title.replace(Regex("[^a-zA-Z0-9]"), "_").take(20)}_${System.currentTimeMillis()}.pdf"
        val outFile = File(context.cacheDir, fileName)
        FileOutputStream(outFile).use { fos ->
            pdfDoc.writeTo(fos)
        }
        pdfDoc.close()
        return outFile
    }

    /**
     * Generates an interactive HTML5 slide presentation
     */
    fun createHtmlSlideDeck(context: Context, deck: PptDeckModel): File {
        val htmlContent = buildString {
            append("""
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>${deck.title} - Pak AI Presentation</title>
                <style>
                  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #0b1118; color: #f0f4f8; margin: 0; padding: 20px; }
                  .deck-header { text-align: center; margin-bottom: 25px; }
                  .deck-title { font-size: 28px; color: #00FF88; font-weight: bold; margin-bottom: 5px; }
                  .deck-author { font-size: 14px; color: #8fa0b5; }
                  .slide-card { background: #151f2b; border: 1px solid #223244; border-radius: 16px; padding: 30px; margin: 20px auto; max-width: 800px; box-shadow: 0 8px 24px rgba(0,0,0,0.3); }
                  .slide-title { font-size: 22px; color: #00FF88; font-weight: bold; margin-bottom: 8px; border-bottom: 2px solid #00FF88; padding-bottom: 8px; }
                  .slide-sub { font-size: 14px; color: #9bb0c4; margin-bottom: 20px; font-style: italic; }
                  ul { list-style: none; padding-left: 0; }
                  li { background: #1c2a3b; margin-bottom: 12px; padding: 14px 18px; border-radius: 10px; border-left: 4px solid #00FF88; font-size: 15px; }
                  .footer { text-align: center; margin-top: 40px; font-size: 12px; color: #62778d; }
                </style>
                </head>
                <body>
                  <div class="deck-header">
                    <div class="deck-title">${deck.title}</div>
                    <div class="deck-author">Presenter: ${deck.presenter} • Generated by Pak AI</div>
                  </div>
            """.trimIndent())

            deck.slides.forEachIndexed { i, s ->
                append("""
                  <div class="slide-card">
                    <div class="slide-title">Slide ${i + 1}: ${s.title}</div>
                    ${if (s.subtitle.isNotBlank()) "<div class=\"slide-sub\">${s.subtitle}</div>" else ""}
                    <div class="visual-box">📊 Visual Content & Architecture: ${s.visualContent}</div>
                    <ul>
                """.trimIndent())
                s.bullets.forEach { b ->
                    append("<li>$b</li>\n")
                }
                append("</ul>\n")
                if (s.notes.isNotBlank()) {
                    append("<div style=\"margin-top: 15px; font-size: 12px; color: #7f95ab;\"><strong>Speaker Notes:</strong> ${s.notes}</div>")
                }
                append("</div>\n")
            }

            append("""
                  <div class="footer">Pak AI Presentation Studio • Developed by Muhammad Ali</div>
                </body>
                </html>
            """.trimIndent())
        }

        val fileName = "PakAI_Slides_${deck.title.replace(Regex("[^a-zA-Z0-9]"), "_").take(20)}_${System.currentTimeMillis()}.html"
        val outFile = File(context.cacheDir, fileName)
        outFile.writeText(htmlContent)
        return outFile
    }
}
