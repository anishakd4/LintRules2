package com.example.rules

import com.android.tools.lint.detector.api.*
import org.w3c.dom.Document

class MissingXmlHeaderDetector : ResourceXmlDetector() {

    override fun visitDocument(context: XmlContext, document: Document) {
        val content = context.client.readFile(context.file)

        if (!content.startsWith("<?xml")) {
            val fix = fix()
                .replace()
                .name("Add xml header")
                .text(content.toString())
                .with("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n$content")
                .autoFix()
                .build()

            context.report(
                ISSUE,
                document,
                Location.create(context.file, content, 0, content.length),
                "Missing an xml header.",
                fix
            )
        }
    }


    companion object {
        val ISSUE = Issue.create(
            "MissingXmlHeader",
            "Flags xml files that don't have a header.",
            "An xml file should always have the xml header to declare that it is an xml file despite the file ending.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(MissingXmlHeaderDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
        )
    }
}