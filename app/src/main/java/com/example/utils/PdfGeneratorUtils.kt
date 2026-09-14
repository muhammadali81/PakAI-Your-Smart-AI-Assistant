package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PdfSection(
    val heading: String,
    val content: String,
    val bullets: List<String> = emptyList()
)

data class PdfDocumentModel(
    val title: String,
    val subtitle: String = "",
    val author: String = "Muhammad Ali",
    val organization: String = "Pak AI Professional Studio",
    val documentType: String = "Report",
    val sections: List<PdfSection> = emptyList()
)

object PdfGeneratorUtils {

    fun createPdf(context: Context, docModel: PdfDocumentModel): File {
        val pdfDoc = PdfDocument()
        val pageWidth = 595 // A4 standard width in points
        val pageHeight = 842 // A4 standard height in points

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDoc.startPage(pageInfo)
        var canvas = page.canvas

        val margin = 50f
        var currentY = 50f

        val titlePaint = Paint().apply {
            color = Color.rgb(0, 122, 61) // Pak AI Dark Green
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.rgb(80, 90, 100)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }

        val metaPaint = Paint().apply {
            color = Color.rgb(100, 110, 120)
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headingPaint = Paint().apply {
            color = Color.rgb(10, 40, 25)
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.rgb(30, 35, 40)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val bulletPaint = Paint().apply {
            color = Color.rgb(0, 150, 75)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(0, 200, 100)
            strokeWidth = 2f
        }

        val footerPaint = Paint().apply {
            color = Color.rgb(140, 150, 160)
            textSize = 8f
            isAntiAlias = true
        }

        // Draw Header Banner on first page
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 3f, linePaint)
        currentY += 24f

        canvas.drawText(docModel.title, margin, currentY, titlePaint)
        currentY += 18f

        if (docModel.subtitle.isNotBlank()) {
            canvas.drawText(docModel.subtitle, margin, currentY, subtitlePaint)
            currentY += 16f
        }

        val dateStr = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val metaInfo = "Author: ${docModel.author}  |  Org: ${docModel.organization}  |  Date: $dateStr"
        canvas.drawText(metaInfo, margin, currentY, metaPaint)
        currentY += 16f

        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint)
        currentY += 25f

        fun checkPageBreak(neededHeight: Float) {
            if (currentY + neededHeight > pageHeight - 60f) {
                // Draw footer
                canvas.drawText(
                    "Pak AI Document Studio • Developed by Muhammad Ali • Page $pageNumber",
                    margin,
                    pageHeight - 30f,
                    footerPaint
                )
                pdfDoc.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDoc.startPage(pageInfo)
                canvas = page.canvas
                currentY = 50f

                // Mini header on continuation pages
                canvas.drawText("${docModel.title} (Cont.)", margin, currentY, subtitlePaint)
                currentY += 20f
                canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint)
                currentY += 25f
            }
        }

        // Draw Sections
        for (section in docModel.sections) {
            checkPageBreak(50f)

            // Section Heading
            canvas.drawText(section.heading, margin, currentY, headingPaint)
            currentY += 18f

            // Section Body (multiline wrap)
            val words = section.content.split(" ")
            var currentLine = ""
            val maxLineWidth = pageWidth - (margin * 2)

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val measure = bodyPaint.measureText(testLine)
                if (measure > maxLineWidth) {
                    checkPageBreak(16f)
                    canvas.drawText(currentLine, margin, currentY, bodyPaint)
                    currentY += 14f
                    currentLine = word
                } else {
                    currentLine = testLine
                }
            }
            if (currentLine.isNotEmpty()) {
                checkPageBreak(16f)
                canvas.drawText(currentLine, margin, currentY, bodyPaint)
                currentY += 18f
            }

            // Section Bullets
            for (bullet in section.bullets) {
                checkPageBreak(18f)
                canvas.drawText("• ", margin + 8f, currentY, bulletPaint)

                val bWords = bullet.split(" ")
                var bLine = ""
                val bMaxWidth = pageWidth - (margin * 2) - 20f
                for (w in bWords) {
                    val test = if (bLine.isEmpty()) w else "$bLine $w"
                    if (bodyPaint.measureText(test) > bMaxWidth) {
                        checkPageBreak(16f)
                        canvas.drawText(bLine, margin + 20f, currentY, bodyPaint)
                        currentY += 14f
                        bLine = w
                    } else {
                        bLine = test
                    }
                }
                if (bLine.isNotEmpty()) {
                    checkPageBreak(16f)
                    canvas.drawText(bLine, margin + 20f, currentY, bodyPaint)
                    currentY += 16f
                }
            }

            currentY += 14f
        }

        // Final footer
        canvas.drawText(
            "Pak AI Document Studio • Developed by Muhammad Ali • Page $pageNumber",
            margin,
            pageHeight - 30f,
            footerPaint
        )
        pdfDoc.finishPage(page)

        // Save to cache directory
        val fileName = "PakAI_${docModel.title.replace(Regex("[^a-zA-Z0-9]"), "_").take(25)}_${System.currentTimeMillis()}.pdf"
        val cacheDir = context.cacheDir
        val outFile = File(cacheDir, fileName)

        FileOutputStream(outFile).use { fos ->
            pdfDoc.writeTo(fos)
        }
        pdfDoc.close()

        return outFile
    }
}
