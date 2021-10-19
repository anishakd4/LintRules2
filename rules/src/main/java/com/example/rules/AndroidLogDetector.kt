package com.example.rules

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class AndroidLogDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String>? {
        super.getApplicableMethodNames()
        return listOf("tag", "format", "v", "d", "i", "w", "e", "wtf")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)

        //method.containingClass?.qualifiedName gives package name of the method
        //method.name gives method name
        if (context.evaluator.isMemberInClass(method, "android.util.Log")) {
            reportUsage(context, node)
        }
    }

    private fun reportUsage(context: JavaContext, node: UCallExpression) {
        fireIssue(
            ISSUE,
            node,
            context.getCallLocation(call = node, includeReceiver = true, includeArguments = true),
            MESSAGE,
            null,
            context
        )
    }

    companion object {

        val MESSAGE = "android.util.Log usage is forbidden."

        val ISSUE: Issue = Issue
            .create(
                id = "AndroidLogDetector",
                briefDescription = "The android Log should not be used",
                explanation = """
                For amazing showcasing purposes we should not use the Android Log. We should the
                AmazingLog instead.
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 10,
                severity = Severity.FATAL,
                androidSpecific = true,
                implementation = Implementation(
                    AndroidLogDetector::class.java,
                    Scope.JAVA_FILE_SCOPE
                )
            )
    }

}
