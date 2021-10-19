package com.example.rules.incorrectviewIddetector

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class IncorrectViewIdDetector : LayoutDetector() {

    val rules = RulesProvider.rulesList

    override fun getApplicableAttributes(): Collection<String>? {
        return listOf(SdkConstants.ATTR_ID)
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.LAYOUT
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (attribute.name != null && attribute.name == "android:id") {
            rules.forEach {
                val id = attribute.value.split("/")[1] // remove @+id/
                val classType = "todo"
                val layoutName = "todo"

                if (!it.isValidId(id, classType, layoutName)) {
                    reportUsage(context, attribute, it.getMessage())
                }
            }
        }
    }

    private fun reportUsage(context: XmlContext, attribute: Attr, message: String) {
        context.report(
            ISSUE,
            attribute,
            context.getLocation(attribute),
            message
        )
    }

    companion object {
        const val MESSAGE = "Lint detector for properly naming view id's"

        val ISSUE = Issue.create(
            id = "IncorrectViewId",
            briefDescription = MESSAGE,
            explanation = MESSAGE,
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.WARNING,
            implementation = Implementation(
                IncorrectViewIdDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }
}

object RulesProvider {
    var rulesList: List<ViewIdRule> = listOf(
        AllCapsViewIdRule()
    )
}

class AllCapsViewIdRule : ViewIdRule {

    override fun isValidId(id: String, viewType: String, layoutName: String) =
        id.toUpperCase() == id


    override fun getMessage() =
        "Your view id's need to be all capital"
}

interface ViewIdRule {
    fun isValidId(id: String, viewType: String, layoutName: String): Boolean
    fun getMessage(): String
}

