package com.example.rules

import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Location
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

val MESSAGE_PREFIX = "ANISH LINT REPORTS: "

fun fireIssue(issue: Issue, node: UCallExpression, location: Location, message: String, quickfixData: LintFix? = null, context: JavaContext) {
    if (quickfixData == null) {
        context.report(issue, node, location, MESSAGE_PREFIX + message)
    } else {
        context.report(issue, node, location, MESSAGE_PREFIX + message, quickfixData)
    }

}