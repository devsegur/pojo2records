package com.github.devsegur.pojo2records.modifiers

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import com.intellij.refactoring.openapi.impl.JavaRenameRefactoringImpl
import com.intellij.refactoring.rename.RenameUtil
import com.github.devsegur.pojo2records.modifiers.DeleteModifier.Companion.deleteMethod
import org.jetbrains.kotlin.lombok.utils.decapitalize

class RenameModifier {

    companion object {
        fun renameMethod(psiMethod: PsiMethod, project: Project) {
            val newMethodName = psiMethod.name.replace("get", "").decapitalize()
            val renamedMethod = JavaRenameRefactoringImpl(
                project,
                psiMethod.originalElement,
                newMethodName,
                false,
                false
            )
            renamedMethod.setShouldRenameInheritors(true)

            val usages = RenameUtil.findUsages(
                psiMethod,
                newMethodName,
                psiMethod.resolveScope,
                false,
                false,
                mapOf()
            )

            renamedMethod.doRefactoring(usages)
            deleteMethod(project, psiMethod)
        }
    }


}