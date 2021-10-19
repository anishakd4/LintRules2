package com.example.rules

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr
import org.w3c.dom.Element
import java.util.*

class RawColorDetector : ResourceXmlDetector() {

    private var collector = ElementCollectReporter(SdkConstants.ATTR_COLOR)

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return EnumSet.of(ResourceFolderType.LAYOUT, ResourceFolderType.DRAWABLE, ResourceFolderType.VALUES).contains(folderType)
    }

    override fun getApplicableElements() = ALL

    override fun beforeCheckEachProject(context: Context) {
        collector = ElementCollectReporter(SdkConstants.ATTR_COLOR)
    }

    override fun visitElement(context: XmlContext, element: Element) {
        collector.collect(element)

        element.attributes()
            .filterNot { SdkConstants.TAG_VECTOR == element.localName || SdkConstants.ATTR_PATH == element.localName }
            .filterNot { it.hasToolsNamespace() }
            .filter { it.nodeValue.matches("#[a-fA-F\\d]{3,8}".toRegex()) }
            .filterNot { context.driver.isSuppressed(context, ISSUE, it) }
            .map { it to context.getValueLocation(it as Attr) }
            .toCollection(collector)

    }

    override fun afterCheckEachProject(context: Context) {
        collector.report(ISSUE, context, "Anish: Should be using a color resource instead.")
    }

    companion object {

        val ISSUE = Issue.create(
            "RawColor",
            "Flags color that are not defined as resource.",
            "Color value should all be defined as color resources. This has the benefit that you can easily see all of your colors in one file. One benefit is an easier addition to Dark Theme for instance. This check will run on layouts as well as xml drawables.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(RawColorDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}