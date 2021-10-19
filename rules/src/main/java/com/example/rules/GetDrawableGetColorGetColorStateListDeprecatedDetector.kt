package com.example.rules

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

class GetDrawableGetColorGetColorStateListDeprecatedDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("getColor", "getDrawable", "getColorStateList")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)

        val isInResources = context.evaluator.isMemberInClass(method, "android.content.res.Resources")
        val methodName = node.methodName

        if ("getDrawable" == methodName && isInResources) {
            reportUsage(ISSUE_RESOURCES_GET_DRAWABLE, context, node, "Anish Calling deprecated getDrawable.")
        }

        if ("getColor" == methodName && isInResources) {
            reportUsage(ISSUE_RESOURCES_GET_COLOR, context, node, "Anish Calling deprecated getColor.")
        }

        if ("getColorStateList" == methodName && isInResources) {
            reportUsage(ISSUE_RESOURCES_GET_COLOR_STATE_LIST, context, node, "Anish Calling deprecated getColorStateList.")
        }
    }

    private fun reportUsage(issue: Issue, context: JavaContext, node: UCallExpression, message: String) {
        context.report(
            issue,
            node,
            context.getNameLocation(node),
            message
        )
    }

    companion object {
        val ISSUE_RESOURCES_GET_DRAWABLE = Issue.create(
            "ResourcesGetDrawableCall",
            "Marks usage of deprecated getDrawable() on Resources.",
            "Instead of getDrawable(), ContextCompat or the method with the Theme Overload should be used instead.",
            Category.MESSAGES,
            PRIORITY,
            Severity.FATAL,
            Implementation(
                GetDrawableGetColorGetColorStateListDeprecatedDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE)
            )
        )

        val ISSUE_RESOURCES_GET_COLOR = Issue.create(
            "ResourcesGetColorCall",
            "Marks usage of deprecated getColor() on Resources.",
            "Instead of getColor(), ContextCompat or the method with the Theme Overload should be used instead.",
            Category.MESSAGES,
            PRIORITY,
            Severity.FATAL,
            Implementation(
                GetDrawableGetColorGetColorStateListDeprecatedDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE)
            )
        )

        val ISSUE_RESOURCES_GET_COLOR_STATE_LIST = Issue.create(
            "ResourcesGetColorStateListCall",
            "Marks usage of deprecated getColorStateList() on Resources.",
            "Instead of getColorStateList(), ContextCompat or the method with the Theme Overload should be used instead.",
            Category.MESSAGES,
            PRIORITY,
            Severity.FATAL,
            Implementation(
                GetDrawableGetColorGetColorStateListDeprecatedDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE)
            )
        )
    }
}