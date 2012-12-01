package tw.epubcoverter

import net.lingala.zip4j.core.ZipFile
import scala.collection.JavaConversions._
import java.io.InputStream
import net.lingala.zip4j.model.FileHeader

object ZipFileHandler {
  def main(args: Array[String]) {
    val zipFileHandler = new ZipFileHandler("./testData/testZipFile.zip")
    if (zipFileHandler.isValidZipFile)
      zipFileHandler.travelAllFiles(zipFileHandler.defaultHandler(zipFileHandler.testContentHandler)_)
    else
      println("zip file is not vaild")
  }
}

class ZipFileHandler(path: String) {

  val BUFF_SIZE = 4096
  lazy val buffer = new Array[Byte](BUFF_SIZE)

  val zipFile = new ZipFile(path)

  val fileHeaderList = zipFile.getFileHeaders.asInstanceOf[java.util.List[FileHeader]]

  def isValidZipFile = zipFile.isValidZipFile

  def travelAllFiles(handler: (String, Long, InputStream) => Boolean) = {
    //Travel all file in the zip file and pass the inputstream to handler.
    fileHeaderList foreach (
      fileHeader =>
        handler(fileHeader.getFileName, fileHeader.getUncompressedSize, zipFile.getInputStream(fileHeader)))
  }

  def defaultHandler(contentHandler: (String, String) => (String))(fileName: String, size: Long, inputStream: InputStream) = {
    //Read form inputStream to a binary array
    val binaryBuffer = new Array[Byte](size.toInt)
    val binaryContent = inputStream.read(binaryBuffer)

    contentHandler(fileName, new String(binaryBuffer, "utf-8"))

    true
  }

  def testContentHandler(fileName: String, content: String): String = {
    if (fileName.endsWith("html")) {
      content
    } else
      content
  }
}