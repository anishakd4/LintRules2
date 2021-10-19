package com.example.rules

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr
import org.w3c.dom.Element
import java.util.*

class ColorCasingDetector : ResourceXmlDetector() {

    override fun appliesTo(folderType: ResourceFolderType) = true

    override fun getApplicableElements() = ALL

    override fun visitElement(context: XmlContext, element: Element) {
        element.attributes()
            .filter { it.nodeValue.matches(COLOR_REGEX) }
            .filter { it.nodeValue.any { it.isLowerCase() } }
            .forEach {
                val fix = fix()
                    .name("Convert to uppercase")
                    .replace()
                    .text(it.nodeValue)
                    .with(it.nodeValue.toUpperCase(Locale.US))
                    .autoFix()
                    .build()

                context.report(ISSUE, it, context.getValueLocation(it as Attr), "Anish: Should be using uppercase letters", fix)
            }
    }

    companion object {

        val ISSUE = Issue.create(
            "ColorCasing",
            "Raw colors should be defined with uppercase letters.",
            "Colors should have uppercase letters. #FF0099 is valid while #ff0099 isn't since the ff should be written in uppercase.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(ColorCasingDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )

        val COLOR_REGEX = Regex("#[a-fA-F\\d]{3,8}")
    }
}