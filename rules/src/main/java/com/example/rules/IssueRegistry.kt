package com.example.rules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.example.rules.classexistencedetector.ClassExistenceDetector
import com.example.rules.incorrectviewIddetector.IncorrectViewIdDetector
import com.example.rules.invalidimportdetector.InvalidImportDetector

internal const val PRIORITY = 10

class IssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

    override val issues: List<Issue>
        get() = listOf(
//            AndroidLogDetector.ISSUE,
            ToastMakeDetector.ISSUE
//            ActivityInheritanceDetector.ISSUE,
//            FragmentInheritanceDetector.ISSUE,
//            IncorrectViewIdDetector.ISSUE,
//            InvalidImportDetector.ISSUE,
//            ClassExistenceDetector.ISSUE,
//            ExhaustiveWhenDetector.ISSUE,
//            GetDrawableGetColorGetColorStateListDeprecatedDetector.ISSUE_RESOURCES_GET_COLOR,
//            GetDrawableGetColorGetColorStateListDeprecatedDetector.ISSUE_RESOURCES_GET_COLOR_STATE_LIST,
//            GetDrawableGetColorGetColorStateListDeprecatedDetector.ISSUE_RESOURCES_GET_DRAWABLE,
//            WrongMenuIdFormatDetector.ISSUE,
//            RawDimenDetector.ISSUE,
//            RawColorDetector.ISSUE,
//            SuperfluousMarginDeclarationDetector.ISSUE,
//            SuperfluousPaddingDeclarationDetector.ISSUE,
//            ShouldUseStaticImportDetector.ISSUE,
//            MatchingViewIdDetector.ISSUE,
//            MatchingMenuIdDetector.ISSUE,
//            JcenterDetector.ISSUE,
//            InvalidStringDetector.ISSUE,
//            DefaultLayoutAttributeDetector.ISSUE,
//            TodoDetector.ISSUE,
//            WrongConstraintLayoutUsageDetector.ISSUE,
//            MissingXmlHeaderDetector.ISSUE,
//            WrongTestMethodNameDetector.ISSUE,
//            WrongLayoutNameDetector.ISSUE,
//            ConstraintLayoutToolsEditorAttributeDetector.ISSUE,
//            SuperfluousNameSpaceDetector.ISSUE,
//            XmlSpacingDetector.ISSUE,
//            NamingPatternDetector.ISSUE,
//            UnusedMergeAttributesDetector.ISSUE,
//            LayoutFileNameMatchesClassDetector.ISSUE,
//            UnsupportedLayoutAttributeDetector.ISSUE,
//            ColorCasingDetector.ISSUE,
//            AlertDialogUsageDetector.ISSUE
        )
}