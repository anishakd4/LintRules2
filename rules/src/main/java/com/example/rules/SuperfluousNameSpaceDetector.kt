package com.example.rules

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

private val possibleUris = setOf(SdkConstants.ANDROID_URI, SdkConstants.TOOLS_URI, SdkConstants.AUTO_URI, SdkConstants.AAPT_URI)

class SuperfluousNameSpaceDetector : LayoutDetector() {

    override fun getApplicableElements() = ALL

    override fun visitElement(context: XmlContext, element: Element) {
        if (element.parentNode.parentNode != null) {
            element.attributes()
                .filter { attribute -> possibleUris.any { attribute.toString().contains(it) } }
                .forEach {
                    val fix = fix()
                        .name("Remove namespace")
                        .replace()
                        .range(context.getLocation(it))
                        .all()
                        .build()

                    context.report(
                        ISSUE,
                        it,
                        context.getLocation(it),
                        "This name space is already declared and hence not needed.",
                        fix
                    )
                }
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "SuperfluousNameSpace",
            "Flags namespaces that are already declared.",
            "Re-declaring a namespace is unnecessary and hence can be just removed.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(SuperfluousNameSpaceDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}