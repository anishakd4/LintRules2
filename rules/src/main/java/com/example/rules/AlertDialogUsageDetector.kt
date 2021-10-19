package com.example.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiType
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UVariable
import java.util.*

class AlertDialogUsageDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes() = listOf<Class<out UElement>>(UVariable::class.java, UClass::class.java)

    override fun createUastHandler(context: JavaContext) = AlertDialogUsageHandler(context)

    class AlertDialogUsageHandler(
        private val context: JavaContext
    ) : UElementHandler() {
        override fun visitVariable(node: UVariable) = process(node.type, node)

        override fun visitClass(node: UClass) = node.uastSuperTypes.forEach { process(it.type, it) }

        private fun process(type: PsiType, node: UElement) {
            if (context.evaluator.typeMatches(type, FQDN_ANDROID_ALERT_DIALOG)) {
                context.report(ISSUE, node, context.getLocation(node), "Anish: Should not be using android.app.AlertDialog.")
            }
        }
    }

    companion object {

        private const val FQDN_ANDROID_ALERT_DIALOG = "android.app.AlertDialog"

        val ISSUE = Issue.create(
            "AlertDialogUsage",
            "Use the support library AlertDialog instead of android.app.AlertDialog.",
            "Support library AlertDialog is much more powerful and plays better together with the new theming / styling than the AlertDialog built into the framework.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(AlertDialogUsageDetector::class.java, EnumSet.of(Scope.JAVA_FILE))
        )
    }
}