package com.example.rules

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.getContainingUClass
import org.jetbrains.uast.tryResolveNamed
import java.util.*


class LayoutFileNameMatchesClassDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("setContentView")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)

        val resourcePrefix = context.project.resourcePrefix()
        val firstParameter = node.valueArguments.getOrNull(0)

        val isNoLayoutReference = firstParameter?.asSourceString()?.startsWith("R.layout") == false

        val layoutFileName = firstParameter
            ?.tryResolveNamed()
            ?.name

        val className = node.getContainingUClass()
            ?.name

        if (isNoLayoutReference || layoutFileName == null || className == null) {
            return
        }

        val expectedLayoutFileName = className.toSnakeCase()
            .replace(resourcePrefix, "")
            .run {
                val array = split("_")
                resourcePrefix + array.last() + "_" + array.subList(0, array.size - 1).joinToString(separator = "_")
            }

        val isExactMatch = layoutFileName + "_" == expectedLayoutFileName

        if (layoutFileName != expectedLayoutFileName && !isExactMatch) {
            val fix = fix()
                .replace()
                .text(layoutFileName)
                .with(expectedLayoutFileName)
                .autoFix()
                .build()

            context.report(
                ISSUE, node, context.getLocation(node.valueArguments.first()), "Parameter should be named R.layout" +
                        ".$expectedLayoutFileName", fix
            )
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "LayoutFileNameMatchesClass",
            "Checks that the layout file matches the class name.",
            "Layout file names should always match the name of the class. FooActivity should have a layout file named activity_foo hence.",
            Category.CORRECTNESS, PRIORITY, Severity.FATAL,
            Implementation(LayoutFileNameMatchesClassDetector::class.java, EnumSet.of(Scope.JAVA_FILE))
        )
    }
}