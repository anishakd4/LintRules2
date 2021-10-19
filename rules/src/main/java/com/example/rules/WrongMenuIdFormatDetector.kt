package com.example.rules

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.util.*

class WrongMenuIdFormatDetector : ResourceXmlDetector() {

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return EnumSet.of(ResourceFolderType.MENU).contains(folderType)
    }

    override fun getApplicableAttributes(): Collection<String>? {
        return listOf(SdkConstants.ATTR_ID)
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (attribute.name != null && attribute.name == "android:id") {
            if (!stripIdPrefix(attribute.value).isLowerCamelCase()) {
                val fix = fix().replace()
                    .name("Convert to lowerCamelCase")
                    .text(attribute.value)
                    .with(attribute.value.idToLowerCamelCase())
                    .autoFix()
                    .build()

                context.report(ISSUE, attribute, context.getValueLocation(attribute), "Id is not in lowerCamelCaseFormat", fix)
            }
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "WrongMenuIdFormat",
            "Flag menu ids that are not in lowerCamelCase Format.",
            "Menu ids should be in lowerCamelCase format. This has the benefit of saving an unnecessary underscore and also just looks nicer.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(WrongMenuIdFormatDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }

}