package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import service.TransformFile
import tools.OpenFile
import tools.OpenFile.Companion.openPsiFileAsFile

class TransformToRecord : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        println(e.dataContext)
        e.dataContext
        if (isPackageAction(e))
            getAllFilesInPackage(e).forEach { psiFile: PsiFile? ->
                TransformFile().replaceClassToRecord(psiFile!!, e.project!!, openPsiFileAsFile(psiFile)!!)
            }
        else if (isFileAction(e)) {
            val file = OpenFile.getFilePath(e)
            val project = e.project!!
            TransformFile().replaceClassToRecord(file, project)
        }
    }

    private fun isFileAction(event: AnActionEvent): Boolean {
        val psiElement = event.getData(CommonDataKeys.PSI_ELEMENT)
        return psiElement == null || psiElement is PsiClass
    }

    private fun isPackageAction(event: AnActionEvent): Boolean {
        val psiElement = event.getData(CommonDataKeys.PSI_ELEMENT)
        return psiElement is PsiDirectory && !isFileAction(event)
    }


    private fun getAllFilesInPackage(event: AnActionEvent): List<PsiFile> {
        val psiElement = event.getData(CommonDataKeys.PSI_ELEMENT) as? PsiDirectory
        val packageDirectory = psiElement ?: return emptyList()

        val files = mutableListOf<PsiFile>()

        recursiveAddFiles(packageDirectory, files)

        return files
    }

    private fun recursiveAddFiles(
        packageDirectory: PsiDirectory,
        files: MutableList<PsiFile>
    ) {
        for (child in packageDirectory.children) {
            if (child is PsiFile) {
                files.add(child)
            } else if (child.children.isNotEmpty())
                recursiveAddFiles(child as PsiDirectory, files)
        }
    }


}