package tools

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiFile
import java.io.File

class OpenFile {

    companion object {
        fun openPsiFileAsFile(psiFile: PsiFile): File? {
            val virtualFile = psiFile.virtualFile
            val filePath = virtualFile?.path ?: return null

            return File(filePath)
        }

        fun getFilePath(e: AnActionEvent): File {
            val result = e.dataContext.toString().split("//").last()
            return File(result)
        }
    }
}