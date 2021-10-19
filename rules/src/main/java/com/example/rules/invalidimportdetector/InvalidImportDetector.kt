package com.example.rules.invalidimportdetector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement
import org.jetbrains.uast.getContainingUFile
import java.util.*

class InvalidImportDetector: Detector(), Detector.UastScanner {

    val rules = RulesProvider.rulesList

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UImportStatement::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitImportStatement(node: UImportStatement) {
                node.importReference?.let { import ->
                    rules.forEach { rule ->
                        val visitingPackageName = import.getContainingUFile()?.packageName
                        val visitingClassName = context.file.nameWithoutExtension
                        val importedClass = import.asRenderString()
                        visitingPackageName?.let {
                            if (!rule.isAllowedImport(
                                    visitingPackageName,
                                    visitingClassName,
                                    importedClass)) {

                                context.report(
                                    ISSUE, node,
                                    context.getLocation(import),
                                    rule.getMessage())
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MESSAGE = "Lint detector for detecting invalid imports"

        val ISSUE = Issue.create(
            id = "IncorrectImportDetector",
            briefDescription = InvalidImportDetector.MESSAGE,
            explanation = InvalidImportDetector.MESSAGE,
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                InvalidImportDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE))
        )
    }
}

object RulesProvider {
    var rulesList : List<InvalidImportRule> = listOf(
        InvalidDomainToPresentationDependencyRule(),
        InvalidDomainToDataDependencyRule(),
        InvalidFeatureImportRule(),
        InvalidEspressoImportRule()
    )
}

interface InvalidImportRule {

    // Return true if allowed import, false if violation
    fun isAllowedImport(
        visitingPackage : String,
        visitingClassName : String,
        importStatement: String) : Boolean

    // Message to show in case of not allowed import
    fun getMessage(): String
}

class InvalidDomainToDataDependencyRule : InvalidImportRule {

    override fun isAllowedImport(
        visitingPackage: String,
        visitingClassName: String,
        importStatement: String
    ) : Boolean =
        !(isDomainPackage(visitingPackage)
                && isDataPackage(importStatement))

    override fun getMessage() =
        "Domain classes should not import from data package"

    private fun isDomainPackage(packageName : String) =
        packageName.contains(".domain.")
                || packageName.endsWith("domain")

    private fun isDataPackage(importStatement : String) =
        importStatement.contains(".data.")
                || importStatement.endsWith("data")
}

class InvalidDomainToPresentationDependencyRule : InvalidImportRule {

    override fun isAllowedImport(
        visitingPackage: String,
        visitingClassName: String,
        importStatement: String
    ) : Boolean =
        !(isDomainPackage(visitingPackage)
                && isPresentationPackageImport(importStatement))

    override fun getMessage() =
        "Domain classes should not import from presentation package"

    private fun isDomainPackage(packageName : String) =
        packageName.contains(".domain.")
                || packageName.endsWith("domain")

    private fun isPresentationPackageImport(importStatement : String) =
        importStatement.contains(".presentation.")
                || importStatement.endsWith("presentation")
}

class InvalidEspressoImportRule :
    InvalidImportRule {

    override fun isAllowedImport(visitingPackage : String, visitingClassName : String, importedClass: String): Boolean {
        if (visitingClassName.endsWith("Test") || visitingClassName.endsWith("Journey")) {
            if (importedClass.startsWith("androidx.test.espresso")) {
                return false
            }
        }
        return true
    }

    override fun getMessage() = "Please don't use any Espresso code in your test classes"
}

class InvalidFeatureImportRule : InvalidImportRule {
    override fun isAllowedImport(
        visitingPackage: String,
        visitingClassName: String,
        importedClass: String
    ): Boolean {
        return !(isFeaturePackage(visitingPackage) &&
                isFeaturePackage(importedClass) &&
                isDifferentFeaturePackage(visitingPackage, importedClass))
    }

    override fun getMessage() = "Please don't reference other features"

    private fun isFeaturePackage(packageName: String): Boolean =
        // please do something more sophisticated here than I did
        packageName.contains(".features.")

    private fun isDifferentFeaturePackage(packageName1: String, packageName2: String) =
        // please do something more sophisticated here than I did
        packageName1.split(".features.")[1].split(".")[0] !=
                packageName2.split(".features.")[1].split(".")[0]

}