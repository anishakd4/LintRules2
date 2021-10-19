package com.example.rules

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class MatchingViewIdDetector : LayoutDetector() {

    override fun getApplicableAttributes() = listOf(SdkConstants.ATTR_ID)

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val id = stripIdPrefix(attribute.value)
        val fixer = MatchingIdFixer(context, id)
        val isAndroidId = attribute.value.startsWith("@android:id/")

        if (fixer.needsFix() && !isAndroidId) {
            val fix = fix()
                .replace()
                .text(id)
                .with(fixer.fixedId())
                .autoFix()
                .build()

            context.report(
                ISSUE,
                attribute,
                context.getValueLocation(attribute),
                "Id should start with: ${fixer.expectedPrefix}",
                fix
            )
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "MatchingViewId",
            "Flags view ids that don't match with the file name.",
            "When the layout file is named activity_home all of the containing ids should be prefixed with activityHome to avoid ambiguity between different layout files across different views.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(MatchingViewIdDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }

}

class MatchingIdFixer(context: XmlContext, private val id: String) {
    private val layoutName = context.file.name.replace(".xml", "")
    val expectedPrefix = layoutName.toLowerCamelCase()

    fun needsFix() = !id.startsWith(expectedPrefix)

    fun fixedId(): String {
        return if (id.startsWith(expectedPrefix, ignoreCase = true)) {
            expectedPrefix + id.substring(expectedPrefix.length)
        } else {
            //noinspection AndroidLintDefaultLocale - https://issuetracker.google.com/issues/133465551
            expectedPrefix + id.capitalize()
        }
    }
}
