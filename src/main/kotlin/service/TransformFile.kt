package service

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import io.ktor.util.reflect.*
import modifiers.ClassModifier.Companion.convertToRecord
import modifiers.DeleteModifier.Companion.deleteFields
import modifiers.DeleteModifier.Companion.deleteMethod
import modifiers.RenameModifier.Companion.renameMethod
import java.io.File


class TransformFile {

    fun replaceClassToRecord(psiFile: PsiFile, project: Project, file: File) {
        if (psiFile.instanceOf(PsiJavaFile::class)) {
            val psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass::class.java)
            psiClass!!.methods.forEach { psiMethod: PsiMethod? ->
                when {
                    psiMethod?.name?.startsWith("set") == true -> deleteMethod(project, psiMethod)
                    psiMethod?.name?.matches(Regex("equals|hashCode")) == true -> deleteMethod(project, psiMethod)

                    psiMethod?.name?.startsWith("get") == true -> renameMethod(psiMethod, project)

                    else -> println(psiMethod.toString())
                }
            }
            val fieldNames = psiClass.fields.map { "${it.type.presentableText} ${it.name}" }
            deleteFields(project, psiClass)

            convertToRecord(psiClass.text, file, fieldNames)
        }
    }


    fun replaceClassToRecord(file: File, project: Project) {
        val virtualFile: VirtualFile? = LocalFileSystem.getInstance().findFileByNioFile(file.toPath())

        val psiManager = PsiManager.getInstance(project)
        replaceClassToRecord(psiManager.findFile(virtualFile!!) as PsiJavaFile, project, file)
    }

}