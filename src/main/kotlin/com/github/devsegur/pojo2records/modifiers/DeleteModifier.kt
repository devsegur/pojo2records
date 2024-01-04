package com.github.devsegur.pojo2records.modifiers

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod

class DeleteModifier {

    companion object {
        fun deleteMethod(project: Project, psiMethod: PsiMethod) {
            WriteCommandAction.runWriteCommandAction(project) {
                psiMethod.delete()
            }
        }

        fun deleteFields(project: Project, psiClass: PsiClass) {
            WriteCommandAction.runWriteCommandAction(project) {
                psiClass.fields.forEach { psiField: PsiField? ->
                    psiField?.delete()
                }
            }
        }

    }

}