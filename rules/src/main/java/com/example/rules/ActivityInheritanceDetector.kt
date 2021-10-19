package com.example.rules

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import java.util.*

class ActivityInheritanceDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? = listOf(UClass::class.java)

    override fun applicableSuperClasses() = ACTIVITY_CLASSES

    override fun visitClass(context: JavaContext, declaration: UClass) {
        super.visitClass(context, declaration)

        //declaration.name gives class name
        //context.evaluator.getQualifiedName(superClass) gives parent class
        //superClass might be giving all the super classes
        declaration.superClass?.let { superClass ->
            if (context.evaluator.getQualifiedName(superClass) in ACTIVITY_CLASSES) {
                reportUsage(context, declaration, MESSAGE_ACTIVITY)
            }else if(context.evaluator.getQualifiedName(superClass) !in CORRECT_ACTIVITY_CLASSES){
                reportUsage(context, declaration, CORRECT_BASE_ACTIVITY)
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

        const val MESSAGE_ACTIVITY = "All activities must inherit from BaseActivity not directly from Android Activity class"
        const val CORRECT_BASE_ACTIVITY = "We Already have a Base Activity"

        private val ACTIVITY_CLASSES = listOf(
            "android.support.v7.app.AppCompatActivity",
            "androidx.appcompat.app.AppCompatActivity"
        )

        private val CORRECT_ACTIVITY_CLASSES = listOf(
            "com.example.lintrules2.base.BaseActivity"
        )


        private val IMPLEMENTATION = Implementation(
            ActivityInheritanceDetector::class.java,
            EnumSet.of(Scope.JAVA_FILE)
        )

        val ISSUE = Issue.create(
            id = "UserInterfaceInheritance",
            briefDescription = "Bad inheritance",
            explanation = "Let activities inherit directly from Android's Activity",
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.FATAL,
            implementation = IMPLEMENTATION
        )
    }
}