package com.example.rules

import com.android.tools.lint.detector.api.*
import org.w3c.dom.Document

class WrongLayoutNameDetector : LayoutDetector() {

    override fun visitDocument(context: XmlContext, document: Document) {
        val modified = allowedPrefixes.map {
            val resourcePrefix = context.project.resourcePrefix()
                .forceUnderscoreIfNeeded()

            if (resourcePrefix != it) resourcePrefix + it else it
        }

        val doesNotStartWithPrefix = modified.none { context.file.name.startsWith(it) }
        val notEquals = modified.map {
            it.dropLast(1) // Drop the trailing underscore.
        }.none { context.file.name == "$it.xml" }

        if (doesNotStartWithPrefix && notEquals) {
            context.report(
                ISSUE,
                document,
                context.getLocation(document),
                "Layout does not start with one of the following prefixes: ${modified.joinToString()}"
            )
        }
    }

    companion object {

        private val allowedPrefixes =
            listOf("activity_", "view_", "fragment_", "dialog_", "bottom_sheet_", "adapter_item_", "divider_", "space_", "popup_window_")

        val ISSUE = Issue.create(
            "WrongLayoutName",
            "Layout names should be prefixed accordingly.",
            "The layout file name should be prefixed with one of the following: ${allowedPrefixes.joinToString()}. This will improve consistency in your code base as well as enforce a certain structure.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(WrongLayoutNameDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )

    }

}

private fun String.forceUnderscoreIfNeeded() = if (isNotEmpty() && !endsWith("_")) plus("_") else this

fun Project.resourcePrefix() = if (isGradleProject) computeResourcePrefix(gradleProjectModel).orEmpty() else ""