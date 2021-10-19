package com.example.rules

import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class ConstraintLayoutToolsEditorAttributeDetector : LayoutDetector() {

    override fun getApplicableAttributes() = ALL

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val isLayoutEditorAttribute = attribute.localName?.startsWith("layout_editor_") ?: false

        if (isLayoutEditorAttribute && attribute.hasToolsNamespace()) {
            val fix = fix()
                .unset(attribute.namespaceURI, attribute.localName)
                .name("Remove")
                .autoFix()
                .build()

            context.report(
                ISSUE,
                attribute,
                context.getNameLocation(attribute),
                "Don't use ${attribute.name}",
                fix
            )
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "ConstraintLayoutToolsEditorAttribute",
            "Flags tools:layout_editor xml properties.",
            "The tools:layout_editor xml properties are only used for previewing and won't be used in your APK hence they're unnecessary and just add overhead.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(ConstraintLayoutToolsEditorAttributeDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}