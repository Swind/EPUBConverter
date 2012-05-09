package tw.epubcoverter

import java.io.File

import com.weiglewilczek.slf4s.Logging

import tw.epubcoverter.encode.zhcode
import tw.epubcoverter.util.FileUtils.createFile
import tw.epubcoverter.util.FileUtils.createFolder
import tw.epubcoverter.util.FileUtils.deleteFolder
import tw.epubcoverter.util.FileUtils.fileTraveler

object Converter extends Logging{
  val converter = new zhcode
  val UTF8T = 7;
  val UTF8S = 8;
  val ALL_TYPE = 24;
  val BIG5 = 4;

  val sourceFolder = "./Source"
  val resultFolder = "./Result"
  val unzipTmpFolder = "./tmpFolder/unzip"
  val converterTmpFolder = "./tmpFolder/converter"

  val zipArchive = new ZipArchive

  def main(args: Array[String]) {
    convert(new File(sourceFolder), new File(resultFolder))
  }

  def convert(sourceFolder: File, targetFolder: File) = {
    val unzipFolderFile = createFolder(unzipTmpFolder)
    val converterFolderFile = createFolder(converterTmpFolder)
    val resultFolderFile = createFolder(resultFolder)

    fileTraveler(sourceFolder.getAbsolutePath) {
      file =>
        if (file.isFile) {
          logger.info("Convert "+file.getName)
          val unzipTmpFolderFile = zipArchive.unZip(file.getAbsolutePath, createFile(unzipFolderFile, file.getName).getAbsolutePath)
          val converterTmpFolderFile = converter.convertFile(unzipTmpFolderFile.getAbsolutePath, createFile(converterFolderFile, file.getName).getAbsolutePath, UTF8S, UTF8T)
          zipArchive.zip(converterTmpFolderFile.getAbsolutePath, createFile(resultFolderFile, convertFileNameOnWindows(file.getName)).getAbsolutePath)
        }
    }
    
    deleteFolder(unzipTmpFolder)
    deleteFolder(converterTmpFolder)
  }
  
  def convertFileNameOnWindows(fileName:String)={
    converter.convertString(fileName, UTF8S, UTF8T)
  }
}
