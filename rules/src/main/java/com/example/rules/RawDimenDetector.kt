package com.example.rules

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.android.utils.forEach
import org.w3c.dom.Attr
import org.w3c.dom.Element
import java.util.*
import org.w3c.dom.Node

class RawDimenDetector : ResourceXmlDetector() {

    private var collector = ElementCollectReporter(SdkConstants.TAG_DIMEN)

    //var arr = emptyList<Pair<Node, Location>>()

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return EnumSet.of(ResourceFolderType.LAYOUT, ResourceFolderType.DRAWABLE, ResourceFolderType.VALUES).contains(folderType)
    }

    override fun getApplicableElements(): Collection<String>? {
        return XmlScannerConstants.ALL
    }

    override fun beforeCheckEachProject(context: Context) {
        collector = ElementCollectReporter(SdkConstants.TAG_DIMEN)
    }

    override fun visitElement(context: XmlContext, element: Element) {
        collector.collect(element)

//        val hasLayoutWeight = element.attributes.getNamedItem("android:layout_weight") != null
//        val isParentConstraintLayout =
//            element.hasParent(SdkConstants.CLASS_CONSTRAINT_LAYOUT.oldName()) || element.hasParent(SdkConstants.CLASS_CONSTRAINT_LAYOUT.newName())
//        val isVectorGraphic = "vector" == element.localName || "path" == element.localName

        element.attributes()
            .filterNot { it.hasToolsNamespace() }
            .filterNot {
                it.nodeValue == "0dp" && listOf(
                    SdkConstants.ATTR_MIN_HEIGHT,
                    SdkConstants.ATTR_LAYOUT_MIN_HEIGHT,
                    SdkConstants.ATTR_MIN_WIDTH,
                    SdkConstants.ATTR_LAYOUT_MIN_WIDTH
                ).any { ignorable -> it.localName == ignorable }
            }
            .filter { it.nodeValue.matches("-?[\\d.]+(sp|dp|dip)".toRegex()) }
            .filterNot { context.driver.isSuppressed(context, ISSUE, it) }
            .map { it to context.getValueLocation(it as Attr) }
            .toCollection(collector)

//        arr.forEach { (node, location) ->
//            context.report(ISSUE, location, "Anish Anish")
//        }
    }

    override fun afterCheckEachProject(context: Context) {
        collector.report(ISSUE, context, "Anish Should be using a dimension resource instead.")
    }

    companion object {
        val ISSUE = Issue.create(
            "RawDimen",
            "Flags dimensions that are not defined as resource.",
            "Dimensions should all be defined as dimension resources. This has the benefit that you can easily see all of your dimensions in one file. One benefit is that when designers change the outline across the entire app you only have to adjust it in one place. This check will run on layouts as well as xml drawables.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(RawDimenDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}

class ElementCollectReporter(
    private val attributeToCollect: String,
    private val elementsToReport: MutableList<Pair<Node, Location>> = mutableListOf()
) : MutableCollection<Pair<Node, Location>> by elementsToReport {
    private val items = ArrayList<CollectedElement>()

    fun collect(element: Element) {
        if (attributeToCollect == element.localName) {
            items.add(CollectedElement(element.getAttribute("name"), element.firstChild.nodeValue))
        }
    }

    @Suppress("Detekt.SpreadOperator")
    fun report(issue: Issue, context: Context, message: String) {
        elementsToReport
            .forEach { (node, location) ->
                val fixes = possibleSuggestions(node.nodeValue)
                    .map { LintFix.create().replace().all().with(it).build() }

                val fix = if (fixes.isNotEmpty()) LintFix.create().group(*fixes.toTypedArray()) else null
                context.report(issue, location, message, fix)
            }
    }

    private fun possibleSuggestions(value: String) = items.filter { it.value == value }.map { "@$attributeToCollect/${it.name}" }

    private data class CollectedElement(
        val name: String,
        val value: String
    )
}