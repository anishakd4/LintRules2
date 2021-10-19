package com.example.rules

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.util.*

class InvalidStringDetector : ResourceXmlDetector() {

    override fun appliesTo(folderType: ResourceFolderType) = EnumSet.of(ResourceFolderType.VALUES).contains(folderType)

    override fun getApplicableElements() = Arrays.asList(SdkConstants.TAG_STRING, SdkConstants.TAG_STRING_ARRAY, SdkConstants.TAG_PLURALS)

    override fun visitElement(context: XmlContext, element: Element) {
        element.children()
            .forEach { child ->
                val isStringResource = child.isTextNode() && SdkConstants.TAG_STRING == element.localName
                val isStringArrayOrPlurals =
                    child.isElementNode() && (SdkConstants.TAG_STRING_ARRAY == element.localName || SdkConstants.TAG_PLURALS == element.localName)

                if (isStringResource) {
                    checkText(context, element, child.nodeValue)
                } else if (isStringArrayOrPlurals) {
                    child.children()
                        .filter { it.isTextNode() }
                        .forEach { checkText(context, child, it.nodeValue) }
                }
            }
    }

    private fun checkText(context: XmlContext, element: Node, text: String) {
        val message = when {
            text.contains("\n") -> "Anish: Text contains new line."
            text.length != text.trim().length -> "Anish: Text contains trailing whitespace."
            else -> null
        }

        message?.let {
            val fix = fix().replace().name("Fix it").text(text).with(text.trim()).autoFix().build()
            context.report(ISSUE, element, context.getLocation(element), it, fix)
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "InvalidString",
            "Marks invalid translation strings.",
            "A translation string is invalid if it contains new lines instead of the escaped \\n or if it contains trailing whitespace.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(InvalidStringDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}