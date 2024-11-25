package hk.com.nmg.notificationobserver

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.Writer

interface FileWriterWrapperProtocol {
    fun writeToFile(fileName: String, data: String)
}

class FileWriterWrapper : FileWriterWrapperProtocol {
    private val TAG: String = "FileWriterWrapper"

    override fun writeToFile(fileName: String, data: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        val fos: FileOutputStream

        try {
            fos = FileOutputStream(file, true)

            try {
                val writer: Writer = FileWriter(fos.fd)
                writer.write(data)
                writer.close()
                Log.d(TAG, "writeCSV: ${file.absolutePath}")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fos.fd.sync()
                fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
