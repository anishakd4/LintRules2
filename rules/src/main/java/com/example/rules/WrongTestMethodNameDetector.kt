package com.example.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UAnnotated
import org.jetbrains.uast.UMethod
import java.util.*

class WrongTestMethodNameDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes() = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = WrongTestMethodNameVisitor(context)

    class WrongTestMethodNameVisitor(private val context: JavaContext) : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            context.evaluator.getAllAnnotations(node as UAnnotated, true)
                .mapNotNull { it.qualifiedName?.split(".")?.lastOrNull() }
                .filter { it == "Test" }
                .filter { !node.name.startsWith("test", ignoreCase = true) }
                .forEach { _ ->
                    val fix = LintFix.create()
                        .name("Add test prefix")
                        .replace()
                        .text(node.name)
                        .with("test${node.name}")
                        .autoFix()
                        .build()
                    context.report(ISSUE, node, context.getNameLocation(node), "Anish: Test method starts with test.", fix)
                }
        }
    }


    companion object {

        val ISSUE = Issue.create(
            "WrongTestMethodName",
            "Flags test methods that start with test.",
            "The @Test annotation already states that this is a test hence the test prefix is not necessary.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(
                WrongTestMethodNameDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES),
                EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES)
            )
        )
    }
}