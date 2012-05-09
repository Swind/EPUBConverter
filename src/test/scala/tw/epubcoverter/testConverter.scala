package tw.epubcoverter

import java.io.File

import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

//@RunWith(classOf[JUnitRunner])
class testConverter extends FunSuite with ShouldMatchers{

  val TEST_DATA_FOLDER = "./testData/testConverterFolder"
  val TEST_DATA_TARGET = "./testData/testConverterTarget"
  
  test("Test convert GB(auto detect) to UTF8T"){
   
    new File(TEST_DATA_TARGET).mkdir
    
    Converter.convert(new File(TEST_DATA_FOLDER), new File(TEST_DATA_TARGET))
    
  }
	
}