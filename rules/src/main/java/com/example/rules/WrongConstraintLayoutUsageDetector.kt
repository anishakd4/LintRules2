package com.example.rules

import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

class WrongConstraintLayoutUsageDetector : LayoutDetector() {

    override fun getApplicableElements() = ALL

    override fun visitElement(context: XmlContext, element: Element) {
        val attributes = element.attributes

        for (i in 0 until attributes.length) {
            val item = attributes.item(i)
            val localName = item.localName

            if (localName != null) {
                val properLocalName = localName.replace("Left", "Start")
                    .replace("Right", "End")

                val isConstraint = localName.contains("layout_constraint")
                val hasLeft = localName.contains("Left")
                val hasRight = localName.contains("Right")

                val isAnIssue = isConstraint && (hasLeft || hasRight)

                if (isAnIssue) {
                    val fix = fix()
                        .name("Fix it")
                        .replace()
                        .text(localName)
                        .with(properLocalName)
                        .autoFix()
                        .build()

                    context.report(
                        ISSUE,
                        item,
                        context.getNameLocation(item),
                        "This attribute won't work with RTL. Please use $properLocalName instead.",
                        fix
                    )
                }
            }
        }
    }

    companion object {

        val ISSUE = Issue.create(
            "WrongConstraintLayoutUsage",
            "Marks a wrong usage of the Constraint Layout.",
            "Instead of using left & right constraints start & end should be used.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(WrongConstraintLayoutUsageDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}