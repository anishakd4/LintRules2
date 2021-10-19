package com.example.rules

import com.android.tools.lint.detector.api.*
import java.util.*

class JcenterDetector : Detector(), Detector.GradleScanner {

    override fun checkDslPropertyAssignment(
        context: GradleContext,
        property: String,
        value: String,
        parent: String,
        parentParent: String?,
        valueCookie: Any,
        statementCookie: Any
    ) {
        super.checkDslPropertyAssignment(context, property, value, parent, parentParent, valueCookie, statementCookie)

//        if (property == "jcenter") {
//            val fix = fix()
//                .replace()
//                .text(property)
//                .with("mavenCentral")
//                .name("Replace with mavenCentral()")
//                .autoFix()
//                .build()
//            context.report(ISSUE, statementCookie, context.getLocation(statementCookie), "Anish: Don't use jcenter().", fix)
//        }
        context.report(ISSUE, statementCookie, context.getLocation(statementCookie), "Anish: Don't use jcenter(). $property")
    }

    companion object {
        val ISSUE = Issue.create(
            "JCenter",
            "Marks usage of the jcenter() repository.",
            "JCenter has gotten less and less reliable and it's best to avoid if possible. This check will flag usages of jcenter() in your gradle files.",
            Category.CORRECTNESS,
            PRIORITY,
            Severity.FATAL,
            Implementation(JcenterDetector::class.java, EnumSet.of(Scope.GRADLE_FILE))
        )
    }
}