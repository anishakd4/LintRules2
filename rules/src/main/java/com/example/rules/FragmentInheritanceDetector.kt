package com.example.rules

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import java.util.*

class FragmentInheritanceDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? = listOf(UClass::class.java)

    override fun applicableSuperClasses() = FRAGMENT_CLASSES

    override fun visitClass(context: JavaContext, declaration: UClass) {
        super.visitClass(context, declaration)

        declaration.superClass?.let { superClass ->
            if(context.evaluator.getQualifiedName(superClass) in FRAGMENT_CLASSES){
                reportUsage(context, declaration, MESSAGE_FRAGMENT)
            }else if(context.evaluator.getQualifiedName(superClass) !in CORRECT_FRAGMENT_CLASSES){
                reportUsage(context, declaration, CORRECT_BASE_FRAGMENT)
            }
        }
    }

    private fun reportUsage(context: JavaContext, declaration: UClass, message: String) {
        context.report(
            ISSUE,
            declaration,
            context.getNameLocation(declaration),
            message
        )
    }

    companion object {

        const val MESSAGE_FRAGMENT = "All fragments must inherit from BaseFragment not directly from Android Fragment class"
        const val CORRECT_BASE_FRAGMENT = "We already have a base Fragment"

        private val FRAGMENT_CLASSES = listOf(
            "android.support.v4.app.Fragment",
            "androidx.fragment.app.Fragment"
        )

        private val CORRECT_FRAGMENT_CLASSES = listOf(
            "com.example.lintrules2.base.BaseFragment"
        )

        private val IMPLEMENTATION = Implementation(
            FragmentInheritanceDetector::class.java,
            EnumSet.of(Scope.JAVA_FILE)
        )

        val ISSUE = Issue.create(
            id = "UserInterfaceInheritance",
            briefDescription = "Bad inheritance",
            explanation = "Let activities inherit directly from Android's Activity",
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )
    }
}