package com.example.rules

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

class UnusedMergeAttributesDetector : LayoutDetector() {

    override fun getApplicableElements() = listOf(SdkConstants.VIEW_MERGE)

    override fun visitElement(context: XmlContext, element: Element) {
        val hasParentTag = element.getAttributeNS(SdkConstants.TOOLS_URI, SdkConstants.ATTR_PARENT_TAG).isNotEmpty()

        if (hasParentTag) {
            element.attributes()
                .filterNot { it.hasToolsNamespace() }
                .filterNot { it.prefix == "xmlns" }
                .forEach {
                    val fix = fix().name("Change to tools").composite(
                        fix().set(SdkConstants.TOOLS_URI, it.localName, it.nodeValue).build(),
                        fix().unset(it.namespaceURI, it.localName).build()
                    ).autoFix()

                    context.report(ISSUE, it, context.getLocation(it), "Attribute won't be used", fix)
                }
        }
    }

    companion object {

        val ISSUE = Issue.create(
            "UnusedMergeAttributes",
            "Flags android and app attributes that are used on a <merge> attribute for custom Views.",
            "Adding android, app and other attributes to <merge> won't be used by the system for custom views and hence can lead to errors.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(UnusedMergeAttributesDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}