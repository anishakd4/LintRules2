package com.example.rules

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

private val unsupportedAttributes = mapOf(
    SdkConstants.RELATIVE_LAYOUT to SdkConstants.ATTR_ORIENTATION,
    SdkConstants.SCROLL_VIEW to SdkConstants.ATTR_ORIENTATION
)

class UnsupportedLayoutAttributeDetector : LayoutDetector() {

    override fun getApplicableAttributes() = ALL

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        unsupportedAttributes
            .filter { attribute.hasOwner(it.key) }
            .filter { attribute.localName == it.value }
            .forEach { (value, key) ->
                val fix = fix().name("Remove unnecessary attribute").unset(attribute.namespaceURI, key).autoFix().build()
                context.report(ISSUE, context.getLocation(attribute), "Anish: $key is not allowed in $value", fix)
            }
    }

    companion object {
        val ISSUE = Issue.create(
            "UnsupportedLayoutAttribute",
            "Marks layout attributes which are not supported.",
            "Some layout attributes are not supported. Your app will still compile but it makes no sense to have them around. This can happen when refactoring a LinearLayout to a ScrollView. The orientation is no longer needed and can be removed.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(UnsupportedLayoutAttributeDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}