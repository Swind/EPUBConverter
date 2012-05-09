package tw.epubcoverter.util

import java.io.File
import java.io.InputStream
import java.io.OutputStream

object FileUtils {

  /**
   * ***********************************************
   *
   * Travel all file in the folder
   *
   * ***********************************************
   */
  def fileTraveler[T](filePath: String)(expr: (File) => Unit): Unit = traveler(List { new File(filePath) })(expr)
  def traveler[T](fileList: List[File] {})(expr: (File) => Unit): Unit = {
    fileList match {
      case file :: files =>

        expr(file)

        if (file.isDirectory)
          traveler(file.listFiles.toList ::: files)(expr)
        else
          traveler(files)(expr)

      case _ =>

    }
  }

  /***************************************************
   *
   * Remove a folder
   *
   * *************************************************
   */
  def deleteFolder(folderPath: String): Boolean = deleteFolder(List { new File(folderPath) })
  def deleteFolder(fileList: List[File]): Boolean = {
    fileList match {
      case file :: files =>
        if (file.isDirectory && file.list.length != 0)
          deleteFolder(file.listFiles.toList ::: fileList)
        else
        {
          file.delete
          deleteFolder(files)
        }
      case _ =>
        true
    }

  }
 
  /*******************************************************
   * 
   * Open a folder and create it
   * 
   *******************************************************/
  def createFolder(path:String) = {
    val folderFile = new File(path)
    folderFile.mkdirs
    folderFile
  }
  
  def createFile(parent:File,name:String) = new File(parent,name)
  
   /*=============================================================
   * 
   * Read InputStream and write the data to OutputStream
   * the recursive method is writeToFile and bufferReader
   *
   *=============================================================*/
  def InputToOutput(buffer:Array[Byte])(fis: InputStream, fos: OutputStream) = {

      /*--------------------------------------------------------------
	   * curry a method , the method can read data from InputStream
	   *--------------------------------------------------------------*/
      def bufferReader(fis: InputStream)(buffer: Array[Byte]) = (fis.read(buffer), buffer)

      /*--------------------------------------------------------------
	   * Write the data in the buffer to outputstream
	   ---------------------------------------------------------------*/
      def writeToFile(reader: (Array[Byte]) => Tuple2[Int, Array[Byte]], fos: OutputStream): Boolean = {
        val (length, data) = reader(buffer)
        if (length >= 0) {
          fos.write(data, 0, length)
          writeToFile(reader, fos)
        } else
          true
      }

      writeToFile(bufferReader(fis)_, fos)
      
      (fis,fos)
  }
}