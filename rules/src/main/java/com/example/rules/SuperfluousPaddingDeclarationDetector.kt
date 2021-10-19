package com.example.rules

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element
import java.util.*

class SuperfluousPaddingDeclarationDetector : LayoutDetector() {

    override fun getApplicableElements() = ALL

    val applicableSuperfluousAttributes = Arrays.asList(
        SdkConstants.ATTR_PADDING_TOP,
        SdkConstants.ATTR_PADDING_BOTTOM,
        SdkConstants.ATTR_PADDING_START,
        SdkConstants.ATTR_PADDING_END
    )

    override fun visitElement(context: XmlContext, element: Element) {
        val attributes = (0 until element.attributes.length)
            .map { element.attributes.item(it) }
            .filterNot { it.hasToolsNamespace() }
            .filter { applicableSuperfluousAttributes.contains(it.localName) }
            .map { it.nodeValue }
            .toList()

        if (attributes.size == applicableSuperfluousAttributes.size && HashSet<String>(attributes).size == 1) {
            context.report(ISSUE, element, context.getLocation(element), "Anish: Should be using padding instead.")
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "SuperfluousPaddingDeclaration",
            "Flags padding declarations that can be simplified.",
            "Instead of using start-, end-, bottom- and top paddings, padding can be used.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(SuperfluousPaddingDeclarationDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}