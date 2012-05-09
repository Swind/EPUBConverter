package tw.epubcoverter

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.io.File
import org.scalatest.junit.JUnitRunner
import org.scalatest.BeforeAndAfter
import tw.epubcoverter.util.FileUtils

//@RunWith(classOf[JUnitRunner])
class testZipArchive extends FunSuite with ShouldMatchers with BeforeAndAfter {

  val TEST_UNZIP_FOLDER = "./testData/unzip"
  val TEST_UNZIP_FILE = "./testData/testUnZipData.zip"

  val TEST_ZIP_FOLDER = "./testData/testConverterFolder"
  val TEST_ZIP_FILE = "./testData/testZipFile.zip"
  //  val TEST_UNZIP_FILE = "./testData/伤物语.zip"  
  before {
  }

  //  test("zip a folder"){
  //    val archive = new ZipArchive
  //    archive.zip(TEST_ZIP_FOLDER, TEST_ZIP_FILE)
  //    
  //    new File(TEST_ZIP_FILE).exists should be (true)
  //  }
  //    
  test("unzip a zip file with a folder and a file with multiple languages") {
    new File(TEST_UNZIP_FOLDER).mkdir

    val archive = new ZipArchive

    archive.unZip(TEST_UNZIP_FILE, TEST_UNZIP_FOLDER)

    val folder = new File(TEST_UNZIP_FOLDER)

    folder.isDirectory should be(true)

    val childs = folder.listFiles

    childs.length should be(2)

    childs.foreach {
      child =>
        child.isDirectory match {
          case true =>
            child.getName should be("花たん✿")
            child.list.length should be(1)
          case false =>
            child.getName should be("Want To See You 会いたい 花たん✿.txt")
            child.getTotalSpace should be("914")
        }
    }

    FileUtils.deleteFolder(TEST_UNZIP_FOLDER)
  }

  after {
  }


}