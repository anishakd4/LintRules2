package com.example.rules

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class DefaultLayoutAttributeDetector : LayoutDetector() {

    override fun getApplicableAttributes() = listOf(SdkConstants.ATTR_TEXT_STYLE)

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if ("normal" == attribute.value) {
            val fix = fix()
                .unset(attribute.namespaceURI, attribute.localName)
                .name("Remove")
                .autoFix()
                .build()

            context.report(
                ISSUE, attribute, context.getValueLocation(attribute),
                "Anish: This is the default and hence you don't need to specify it.", fix
            )
        }
    }

    companion object {

        val ISSUE = Issue.create(
            "DefaultLayoutAttribute",
            "Flags default layout values.",
            "Flags default layout values that are not needed. One for instance is the textStyle=\"normal\" that can be just removed.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(DefaultLayoutAttributeDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}