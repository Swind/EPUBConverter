EPUBConverter
===============

這只是一個非常簡單的小工具，用 Scala 撰寫而成，因此執行環境需要使用 JVM。
我只有測試過 JDK 7 的環境，理論上 JDK 6 應該也沒有問題。
如果有人有測試到有問題的話（默...）我也不知道有沒有時間改 Orz。

轉換方式
---------------

轉換方式感謝 [六度數位空間 - 如何將 ePub 電子書繁體化 (簡體化)](http://jeremy.ssinrc.org/?p=327)的分享。
其實 epub 的格式是以 zip 格式壓縮，所以這個程式的執行流程是

1. 將 epub 檔解壓縮到暫存資料夾
2. 將 暫存資料夾裡面的所有檔案從 UTF-8 簡體 轉成 UTF-8 繁體
3. 將資料夾壓縮，並且順便將檔名轉成繁體

簡體轉繁體的程式碼是使用 [zhcode](http://www.mandarintools.com/zhcode.html)

整個步驟就只有這樣而已。

使用方式
---------------

整個程式的資料夾結構如下

~~~~~~
/EPUBConverter
  |---Source
  |---Result
  |---Converter.bat
  |---Converter.jar
  |---hcutf8.txt

~~~~~~

hcutf8.txt 是個對應表，他列出了簡體字跟繁體字的對應。所以如果想自行更改轉換字的話，可以修改這個檔案（應該可以）。
要使用只要執行 Converter.bat 就會自動將 Source 資料夾底下**所有**的檔案，轉換到 Result 資料夾底下，就這樣而已了。


