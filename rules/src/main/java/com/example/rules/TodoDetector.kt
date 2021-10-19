package com.example.rules

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.w3c.dom.Document
import java.util.*
import java.util.regex.Pattern

private const val COMMENT = "TODO:"
private val pattern = Pattern.compile("[\\t]*$COMMENT.*")

class TodoDetector : Detector(), Detector.UastScanner, Detector.GradleScanner, Detector.OtherFileScanner, Detector.XmlScanner {

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun visitDocument(context: XmlContext, document: Document) {
        // Needs to be overridden but we we'll do the work in afterCheckFile.
    }

    override fun afterCheckFile(context: Context) {
        val source = context.getContents().toString()
        val matcher = pattern.matcher(source)

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            val location = Location.create(context.file, source, start, end)
            context.report(ISSUE, location, "Anish: Contains todo.")
        }
    }

    companion object {

        val ISSUE = Issue.create(
            "Todo",
            "Marks todos in any given file.",
            "Marks todo in any given file since they should be resolved.",
            Category.CORRECTNESS, PRIORITY, Severity.FATAL,
            Implementation(
                TodoDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE, Scope.GRADLE_FILE, Scope.PROGUARD_FILE, Scope.MANIFEST, Scope.RESOURCE_FILE),
                EnumSet.of(
                    Scope.JAVA_FILE, Scope.GRADLE_FILE, Scope.PROGUARD_FILE, Scope.MANIFEST, Scope.RESOURCE_FILE
                )
            )
        )

    }
}