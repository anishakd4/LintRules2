package com.example.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.uast.USwitchExpression
import org.jetbrains.uast.kotlin.KotlinUBlockExpression
import java.util.*

class ExhaustiveWhenDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes() = listOf(
        USwitchExpression::class.java
    )

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {

            override fun visitSwitchExpression(node: USwitchExpression) {
                if (node.sourcePsi is KtWhenExpression) {
                    if (node.uastParent is KotlinUBlockExpression) {
                        context.report(
                            ISSUE,
                            node,
                            context.getNameLocation(node),
                            ERROR_MESSAGE
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val MESSAGE = "Lint detector for exhaustive when"
        const val ERROR_MESSAGE = "Do not use when as expression Anish Anish"

        val ISSUE = Issue.create(
            id = "ExhaustiveWhen",
            briefDescription = ExhaustiveWhenDetector.MESSAGE,
            explanation = ExhaustiveWhenDetector.MESSAGE,
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(
                ExhaustiveWhenDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE))
        )
    }
}