package com.example.rules

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr
import java.util.*

class MatchingMenuIdDetector : ResourceXmlDetector() {

    override fun appliesTo(folderType: ResourceFolderType) = EnumSet.of(ResourceFolderType.MENU).contains(folderType)

    override fun getApplicableAttributes() = listOf(SdkConstants.ATTR_ID)

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val id = stripIdPrefix(attribute.value)
        val fixer = MatchingIdFixer(context, id)

        if (fixer.needsFix()) {
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
            "MatchingMenuId",
            "Flags menu ids that don't match with the file name.",
            "When the layout file is named menu_home all of the containing ids should be prefixed with menuHome to avoid ambiguity between different menu files across different menu items.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(MatchingMenuIdDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}