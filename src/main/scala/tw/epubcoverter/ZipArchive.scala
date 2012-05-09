package tw.epubcoverter

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import scala.collection.JavaConversions.enumerationAsScalaIterator

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream

import com.weiglewilczek.slf4s.Logging

import tw.epubcoverter.util.FileUtils

class ZipArchive extends Logging{

  val BUFSIZE = 4096
  val buffer = new Array[Byte](BUFSIZE)
  val InputToOutput = FileUtils.InputToOutput(buffer)_
  /**
   * **********************************************************
   *
   *  Zip
   *
   * **********************************************************
   */
  def zip(sourceFolder: String, targetFile: String) = {
    val targetZipStream = getZipOutputStream(targetFile)
    targetZipStream.setEncoding("UTF8")
    val absolutePath = new File(sourceFolder).getAbsolutePath
    
    FileUtils.fileTraveler(sourceFolder) {
      file =>
        if (!file.isDirectory) {
            
//            logger.debug(file.getName)
            val entry = new ZipArchiveEntry(file.getAbsolutePath.replace(absolutePath+"\\", ""))
//            logger.debug(entry.getName)
            
            val inputStream = getBufferedInputStream(file)
            
            entry.setSize(file.length)
            targetZipStream.putArchiveEntry(entry)
            
            InputToOutput(inputStream, targetZipStream)
            
            inputStream.close
            targetZipStream.closeArchiveEntry
        }
    }
    
    targetZipStream.flush
    targetZipStream.close
  }

  def getBufferedInputStream(file: File) = new BufferedInputStream(new FileInputStream(file))

  def getZipOutputStream(filePath: String) = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))
  
  /**
   * **********************************************************
   *
   * unZip
   *
   * **********************************************************
   */

  def unZip(source: String, targetFolder: String) = {
    val targetFolderFile = new File(targetFolder)
    
    FileIsExist(source) {
      val zipFile = new ZipFile(source)
      
      targetFolderFile.mkdirs

      unzipAllFile(zipFile.entries.toList, getZipEntryInputStream(zipFile)_, targetFolderFile)
    }
    
    targetFolderFile
  }

  def FileIsExist(path: String)(expr: => Any) = {
    if (new File(path).exists)
      expr
  }

  /*---------------------------------------------------------------------------------
     * curry method , this methond can get the inputstream of a zip entry from zipFile
     *---------------------------------------------------------------------------------*/
  def getZipEntryInputStream(zipFile: ZipFile)(entry: ZipEntry) = zipFile.getInputStream(entry)

  def unzipAllFile(entryList: List[ZipEntry], getInputStream: (ZipEntry) => InputStream, targetFolder: File): Boolean = {

    entryList match {
      case entry :: entries =>

        if (entry.isDirectory)
          new File(targetFolder, entry.getName).mkdirs
        else
        {
          val (input,output) = InputToOutput(getInputStream(entry), new FileOutputStream(new File(targetFolder, entry.getName)))
          
          input.close
          output.close
        }

        unzipAllFile(entries, getInputStream, targetFolder)

      case _ =>
        true
    }
  }

 

}