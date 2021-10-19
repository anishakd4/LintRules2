package com.example.rules

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element
import java.util.*

class SuperfluousMarginDeclarationDetector : LayoutDetector() {

    override fun getApplicableElements() = ALL

    val applicableSuperfluousAttributes = Arrays.asList(
        SdkConstants.ATTR_LAYOUT_MARGIN_TOP,
        SdkConstants.ATTR_LAYOUT_MARGIN_BOTTOM,
        SdkConstants.ATTR_LAYOUT_MARGIN_START,
        SdkConstants.ATTR_LAYOUT_MARGIN_END
    )

    override fun visitElement(context: XmlContext, element: Element) {
        val attributes = (0 until element.attributes.length)
            .map { element.attributes.item(it) }
            .filterNot { it.hasToolsNamespace() }
            .filter { applicableSuperfluousAttributes.contains(it.localName) }
            .map { it.nodeValue }
            .toList()

        if (attributes.size == applicableSuperfluousAttributes.size && HashSet<String>(attributes).size == 1) {
            context.report(ISSUE, element, context.getLocation(element), "Anish: Should be using layout_margin instead.")
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "SuperfluousMarginDeclaration",
            "Flags margin declarations that can be simplified.",
            "Instead of using start-, end-, bottom- and top margins, layout_margin can be used.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(SuperfluousMarginDeclarationDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}