package com.example.rules

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.asJava.builder.toLightClassOrigin
import org.jetbrains.uast.UCallExpression

class ToastMakeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String>? {
        super.getApplicableMethodNames()
        return listOf("makeText")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)

        //method.containingClass?.qualifiedName gives package name of the method
        //method.name gives method name
        if (context.evaluator.isMemberInClass(method, "android.widget.Toast")) {
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

        val MESSAGE = "android.widget.Toast usage is forbidden."

        val ISSUE: Issue = Issue
            .create(
                id = "ToastMakeDetector",
                briefDescription = "The android Toast should not be used",
                explanation = """
                For follwing code standards in our repo we should not use the Toast.MakeText. We should use the
                AmazingToast instead.
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = Implementation(
                    ToastMakeDetector::class.java,
                    Scope.JAVA_FILE_SCOPE
                )
            )
    }

}
