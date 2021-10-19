package com.example.rules.classexistencedetector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import java.util.*

class ClassExistenceDetector : Detector(), Detector.UastScanner {

    val rules = RulesProvider.rulesList

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                node.qualifiedName?.let { qualifiedName ->
                    node.name?.let { name ->

                        rules.forEach { classExistenceRule ->
                            val packageName = qualifiedName.dropLast(name.length + 1)
                            val classesShouldExist = classExistenceRule.classShouldExist(packageName, name)
                            classesShouldExist.forEach { classThatShouldExist ->
                                if (context.evaluator.findClass(classThatShouldExist) == null) {
                                    ISSUE.defaultSeverity
                                    context.report(
                                        ISSUE, node,
                                        context.getNameLocation(node),
                                        classExistenceRule.getMessage()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MESSAGE = "Lint detector for the existence of classes"

        val ISSUE = Issue.create(
            id = "MissingRobot",
            briefDescription = ClassExistenceDetector.MESSAGE,
            explanation = ClassExistenceDetector.MESSAGE,
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(
                ClassExistenceDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE)
            )
        )
    }
}

object RulesProvider {
    var rulesList: List<ClassExistenceRule> = listOf(
        ViewModelExistenceRule()
//        FragmentTestExistenceRule(),
//        RobotExistenceRule()
    )
}

class FragmentTestExistenceRule : ClassExistenceRule {
    override fun classShouldExist(packageName: String, className: String): List<String> {
        if (className.endsWith("Fragment")) {
            return listOf("${packageName}.${className}Test")
        }
        return emptyList()
    }

    override fun getMessage() = "This fragment doesn't have a test or it is not having the correct (package) name!"

}

class RobotExistenceRule : ClassExistenceRule {
    override fun classShouldExist(packageName: String, className: String): List<String> {
        if (className.endsWith("Fragment")) {
            return listOf("${packageName}.${className.dropLast("Fragment".length)}Robot")
        }
        return emptyList()
    }

    override fun getMessage() = "Robot class is missing for this fragment!"

}

class ViewModelExistenceRule : ClassExistenceRule {
    override fun classShouldExist(packageName: String, className: String): List<String> {
        if (className.endsWith("Fragment")) {
            return listOf("${packageName}.${className.dropLast("Fragment".length)}ViewModel")
        }
        return emptyList()
    }

    override fun getMessage() = "ViewModel class is missing for this fragment!"

}

interface ClassExistenceRule {

    fun classShouldExist(packageName: String, className: String): List<String>
    fun getMessage(): String
}