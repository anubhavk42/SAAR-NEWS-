package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.data.DigestItem
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    fun generateDigestPdf(
        context: Context,
        dateString: String,
        items: List<DigestItem>
    ): File? {
        val pdfFile = File(context.cacheDir, "digest_${dateString.replace("-", "_")}.pdf")
        if (pdfFile.exists()) {
            pdfFile.delete()
        }

        val document = PdfDocument()

        // Page sizes: standard A4 is 595 x 842 points
        val pageWidth = 595
        val pageHeight = 842
        var pageCount = 1

        // 1. CREATE TITLE PAGE
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount++).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint()
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = Color.parseColor("#2B2B2B")
        }

        // Draw background
        paint.color = Color.parseColor("#F7F5F0")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

        // Title
        textPaint.color = Color.parseColor("#0F6B66")
        textPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textPaint.textSize = 42f
        canvas.drawText("DIGEST", 60f, 250f, textPaint)

        // Accent line
        paint.color = Color.parseColor("#C98A3E")
        paint.strokeWidth = 4f
        canvas.drawLine(60f, 270f, 180f, 270f, paint)

        // Subtitle
        textPaint.color = Color.parseColor("#6B6B6B")
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 16f
        canvas.drawText("Your Daily 5-Minute Calm News Read", 60f, 310f, textPaint)

        // Date
        textPaint.color = Color.parseColor("#2B2B2B")
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 18f
        canvas.drawText("Date: $dateString", 60f, 360f, textPaint)

        // Footer info
        textPaint.color = Color.parseColor("#9B9B9B")
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        textPaint.textSize = 11f
        canvas.drawText("Generated offline on native Android. No subscriptions, no ads.", 60f, 750f, textPaint)

        document.finishPage(page)

        // 2. CREATE CONTENT PAGES
        // We will layout each item. If the item's text runs past the bottom, we start a new page.
        var currentPage = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount++).create())
        var currentCanvas = currentPage.canvas
        var yPosition = 60f
        val margin = 50f
        val contentWidth = pageWidth - (margin * 2)

        // Draw content page background
        paint.color = Color.parseColor("#FFFFFF")
        currentCanvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

        fun checkNewPage(neededHeight: Float) {
            if (yPosition + neededHeight > pageHeight - 60f) {
                document.finishPage(currentPage)
                currentPage = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount++).create())
                currentCanvas = currentPage.canvas
                // Draw white background for new page
                paint.color = Color.parseColor("#FFFFFF")
                currentCanvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)
                yPosition = 60f
            }
        }

        for (item in items) {
            // Headline height check and render
            val headlinePaint = TextPaint().apply {
                isAntiAlias = true
                color = Color.parseColor("#0F6B66")
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 18f
            }
            val headlineLayout = StaticLayout.Builder.obtain(
                item.headline, 0, item.headline.length, headlinePaint, contentWidth.toInt()
            ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .build()

            val neededHeadlineHeight = headlineLayout.height + 30f
            checkNewPage(neededHeadlineHeight)

            // Draw Category Badge
            val badgePaint = Paint().apply {
                color = Color.parseColor("#F0F7F6")
                style = Paint.Style.FILL
            }
            val badgeTextPaint = TextPaint().apply {
                isAntiAlias = true
                color = Color.parseColor("#0F6B66")
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 9f
            }
            currentCanvas.drawRoundRect(
                margin, yPosition, margin + 80f, yPosition + 18f, 4f, 4f, badgePaint
            )
            currentCanvas.drawText(
                item.category.name.uppercase(), margin + 6f, yPosition + 13f, badgeTextPaint
            )

            yPosition += 28f

            // Draw Headline
            currentCanvas.save()
            currentCanvas.translate(margin, yPosition)
            headlineLayout.draw(currentCanvas)
            currentCanvas.restore()
            yPosition += headlineLayout.height + 15f

            // Draw each of the 4 sections
            val sections = listOf(
                "CONTEXT" to item.contextText,
                "KEY POINTS" to item.keyPointsText,
                "WHY IT MATTERS" to item.whyItMattersText,
                "EXAM ANGLE" to item.examAngleText
            )

            for ((title, content) in sections) {
                val sectionTitlePaint = TextPaint().apply {
                    isAntiAlias = true
                    color = Color.parseColor("#C98A3E")
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textSize = 11f
                }
                val bodyPaint = TextPaint().apply {
                    isAntiAlias = true
                    color = Color.parseColor("#2B2B2B")
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textSize = 10f
                }

                val contentLayout = StaticLayout.Builder.obtain(
                    content, 0, content.length, bodyPaint, contentWidth.toInt()
                ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1.3f)
                    .build()

                val sectionHeight = 15f + contentLayout.height + 15f
                checkNewPage(sectionHeight)

                // Draw section title
                currentCanvas.drawText(title, margin, yPosition, sectionTitlePaint)
                yPosition += 15f

                // Draw section content
                currentCanvas.save()
                currentCanvas.translate(margin, yPosition)
                contentLayout.draw(currentCanvas)
                currentCanvas.restore()

                yPosition += contentLayout.height + 15f
            }

            // Draw separator line between articles
            checkNewPage(20f)
            paint.color = Color.parseColor("#E5E2DC")
            paint.strokeWidth = 1f
            currentCanvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, paint)
            yPosition += 25f
        }

        document.finishPage(currentPage)

        try {
            val fos = FileOutputStream(pdfFile)
            document.writeTo(fos)
            fos.close()
            document.close()
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            return null
        }
    }

    fun generateWeeklyDigestPdf(
        context: Context,
        items: List<DigestItem>
    ): File? {
        val pdfFile = File(context.cacheDir, "weekly_digest_dossier.pdf")
        if (pdfFile.exists()) {
            pdfFile.delete()
        }

        val document = PdfDocument()

        val pageWidth = 595
        val pageHeight = 842
        var pageCount = 1

        // 1. CREATE PREMIUM WEEKLY TITLE PAGE
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount++).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint()
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FAF6EB")
        }

        // Draw deep pine green background for premium weekly edition
        paint.color = Color.parseColor("#1B3B32")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

        // Premium Gold Accent Accentuated Top border
        paint.color = Color.parseColor("#D4A373")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 15f, paint)

        // Title
        textPaint.color = Color.parseColor("#FAF6EB")
        textPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textPaint.textSize = 38f
        canvas.drawText("WEEKLY DOSSIER", 60f, 250f, textPaint)

        // Gold Accent line
        paint.color = Color.parseColor("#D4A373")
        paint.strokeWidth = 5f
        canvas.drawLine(60f, 275f, 220f, 275f, paint)

        // Subtitle
        textPaint.color = Color.parseColor("#E4D5B8")
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 15f
        canvas.drawText("Curated Executive Policy & Economics Digest", 60f, 315f, textPaint)

        // Description paragraph
        val descPaint = TextPaint().apply {
            isAntiAlias = true
            color = Color.parseColor("#C6D5D0")
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 11f
        }
        val descText = "A high-fidelity compilation of the week's most critical policy, legislative, and macroeconomic briefings. Optimized for elite reading comfort and executive intellectual utility."
        val descLayout = StaticLayout.Builder.obtain(
            descText, 0, descText.length, descPaint, (pageWidth - 120)
        ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.3f)
            .build()

        canvas.save()
        canvas.translate(60f, 360f)
        descLayout.draw(canvas)
        canvas.restore()

        // Meta info
        textPaint.color = Color.parseColor("#D4A373")
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 13f
        canvas.drawText("Edition: Weekly Analytical Briefings", 60f, 520f, textPaint)
        canvas.drawText("Volume: ${items.size} Core Briefings Compiled", 60f, 545f, textPaint)

        // Footer info
        textPaint.color = Color.parseColor("#98A8A0")
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        textPaint.textSize = 10f
        canvas.drawText("Privately compiled and rendered on device locally.", 60f, 750f, textPaint)

        document.finishPage(page)

        // 2. CREATE CONTENT PAGES
        var currentPage = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount++).create())
        var currentCanvas = currentPage.canvas
        var yPosition = 60f
        val margin = 50f
        val contentWidth = pageWidth - (margin * 2)

        // Warm Paper/Cream colored pages for the weekly content
        paint.color = Color.parseColor("#F5EEDC")
        currentCanvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

        fun checkNewPage(neededHeight: Float) {
            if (yPosition + neededHeight > pageHeight - 60f) {
                document.finishPage(currentPage)
                currentPage = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount++).create())
                currentCanvas = currentPage.canvas
                // Draw warm paper background for new page
                paint.color = Color.parseColor("#F5EEDC")
                currentCanvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)
                yPosition = 60f
            }
        }

        for (item in items) {
            // Headline height check and render
            val headlinePaint = TextPaint().apply {
                isAntiAlias = true
                color = Color.parseColor("#704214") // Sepia Primary
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 17f
            }
            val headlineLayout = StaticLayout.Builder.obtain(
                item.headline, 0, item.headline.length, headlinePaint, contentWidth.toInt()
            ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .build()

            val neededHeadlineHeight = headlineLayout.height + 30f
            checkNewPage(neededHeadlineHeight)

            // Draw Category Badge
            val badgePaint = Paint().apply {
                color = Color.parseColor("#FAF5E8") // Warm surface
                style = Paint.Style.FILL
            }
            val badgeTextPaint = TextPaint().apply {
                isAntiAlias = true
                color = Color.parseColor("#704214")
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 9f
            }
            currentCanvas.drawRoundRect(
                margin, yPosition, margin + 80f, yPosition + 18f, 4f, 4f, badgePaint
            )
            currentCanvas.drawText(
                item.category.name.uppercase(), margin + 6f, yPosition + 13f, badgeTextPaint
            )

            yPosition += 28f

            // Draw Headline
            currentCanvas.save()
            currentCanvas.translate(margin, yPosition)
            headlineLayout.draw(currentCanvas)
            currentCanvas.restore()
            yPosition += headlineLayout.height + 15f

            // Draw each of the 4 sections
            val sections = listOf(
                "CONTEXT" to item.contextText,
                "KEY POINTS" to item.keyPointsText,
                "WHY IT MATTERS" to item.whyItMattersText,
                "EXAM ANGLE" to item.examAngleText
            )

            for ((title, content) in sections) {
                val sectionTitlePaint = TextPaint().apply {
                    isAntiAlias = true
                    color = Color.parseColor("#B38B6D")
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textSize = 10f
                }
                val bodyPaint = TextPaint().apply {
                    isAntiAlias = true
                    color = Color.parseColor("#433422") // Sepia text
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textSize = 10f
                }

                val contentLayout = StaticLayout.Builder.obtain(
                    content, 0, content.length, bodyPaint, contentWidth.toInt()
                ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1.3f)
                    .build()

                val sectionHeight = 15f + contentLayout.height + 15f
                checkNewPage(sectionHeight)

                // Draw section title
                currentCanvas.drawText(title, margin, yPosition, sectionTitlePaint)
                yPosition += 15f

                // Draw section content
                currentCanvas.save()
                currentCanvas.translate(margin, yPosition)
                contentLayout.draw(currentCanvas)
                currentCanvas.restore()

                yPosition += contentLayout.height + 15f
            }

            // Draw separator line between articles
            checkNewPage(20f)
            paint.color = Color.parseColor("#E4D5B8")
            paint.strokeWidth = 1f
            currentCanvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, paint)
            yPosition += 25f
        }

        document.finishPage(currentPage)

        try {
            val fos = FileOutputStream(pdfFile)
            document.writeTo(fos)
            fos.close()
            document.close()
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            return null
        }
    }
}
