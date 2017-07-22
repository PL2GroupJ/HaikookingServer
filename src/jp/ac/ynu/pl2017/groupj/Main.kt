package jp.ac.ynu.pl2017.groupj

import jp.ac.ynu.pl2017.groupj.db.Access
import jp.ac.ynu.pl2017.groupj.util.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask

class Server(val socket: Socket?): Thread() {
    private val input: DataInputStream?
    private val output: DataOutputStream?
    private val separator = ":*:"
    private lateinit var nounList: MutableList<String>
    private lateinit var season: Season
    private lateinit var advice: String

    init {
        socket!!
        input = DataInputStream(socket.getInputStream())
        output = DataOutputStream(socket.getOutputStream())
    }

    override fun run() {
        loop@ while (true) {
            when (acceptCommand()) {
                ConnectionCommand.HAIKU ->      readHaiku()
                ConnectionCommand.SEASON ->     writeSeason()
                ConnectionCommand.ADVICE ->     writeAdvice()
                ConnectionCommand.NOUN ->       writeNounList()
                ConnectionCommand.IMAGE ->      writeImages()
                ConnectionCommand.DISCONNECT -> break@loop
            }
        }

        input?.close()
        output?.close()
        socket?.close()
    }

    /**
     * 俳句を受信する。
     */
    private fun readHaiku() {
        val haiku = input!!.readUTF()
        val analyze = MAnalyze()
        nounList = analyze.mAnalyze(haiku.split(System.lineSeparator()).toTypedArray())
                .split(separator).toMutableList()
        val sb = StringBuilder()
        if (analyze.skillFlags[0] || analyze.skillFlags[1])
            sb.append(analyze.advice[0], System.lineSeparator())
        analyze.skillFlags.drop(2).forEachIndexed { i, flag ->
            if (flag)
                sb.append(analyze.advice[i + 1], System.lineSeparator())
        }
        if (sb.isNotEmpty())
            repeat(System.lineSeparator().length) { sb.deleteCharAt(sb.length - 1) }
        advice = sb.toString()
        val access = Access()
        val seasonWord = nounList.firstOrNull { access.loadSeason(it) != Season.DEFAULT }
        if (seasonWord == null)
            season = Season.DEFAULT
        else {
            val index = nounList.indexOf(seasonWord)
            nounList.swap(0, index)
            season = access.loadSeason(seasonWord)
            access.addUseCount(seasonWord, 1)
        }
        access.closeConnection()
    }

    /**
     * 季語を送信する。
     */
    fun writeSeason() = output!!.writeUTF(season.name)

    /**
     * アドバイスを送信する。
     */
    private fun writeAdvice() = output!!.writeUTF(advice)

    /**
     * 名詞のリストを送信する。名詞はseparatorで区切って送信する。
     */
    private fun writeNounList() = output!!.writeUTF(nounList.joinToString(separator = separator))

    /**
     * 複数の画像のバイト列を送信する。WordCloudの画像送信に利用。
     */
    private fun writeImages() {
        val resources = arrayOf("python/image/total_wordcloud.png", "python/image/weekly_wordcloud.png", "python/image/monthly_wordcloud.png",
                "python/image/spring_wordcloud.png", "python/image/summer_wordcloud.png", "python/image/autumn_wordcloud.png", "python/image/winter_wordcloud.png",
                "python/image/newyear_wordcloud.png")
        val byteArrayList = resources.map { File(it).inputStream().use { it.readBytes() } }
        val data = byteArrayList.concat()                       // 結合してから一度に送信
        byteArrayList.forEach { output!!.writeInt(it.size) }    // それぞれのバイト列のサイズをクライアントに通知
        output!!.write(data)
    }

    // 処理分岐のコマンド受信
    private fun acceptCommand() = ConnectionCommand.valueOf(input!!.readUTF())
}

fun main(args: Array<String>) {
    val port = 9999
    val serverSocket = ServerSocket(port)

    println("start server")

    // ローカル関数で再帰をする。一日経つごとにに実行
    fun task(dateTime: LocalDateTime = LocalDateTime.now().plusDays(1)) {
        // 引数でPythonのパスを指定できるように
        if (args.isNotEmpty()) runPython(args[0]) else runPython()
        println("next generation is $dateTime")
        Timer().schedule(timerTask { task() }, dateTime.toDate() )
    }
    val now = LocalDateTime.now()
    task(now.plusHours(26 - now.hour.toLong()))     // 次の日の午前2時~3時を指定

    serverSocket.use {
        while (true) {
            val socket = it.accept()
            Server(socket).start()
            println("accept : $socket")
        }
    }
}

fun runPython(pythonPath: String = "python"): Boolean {
    // WordCloudのためのデータ取得
    val access = Access()
    val sb = StringBuilder()
    Access.Flag.values().forEach {
        val data = access.loadWordAndCounts(it, 100)
        val text = data.map { (word, count) -> (1..count).map { word } }.flatten().joinToString(separator = ",")
        sb.append(text, ":*:")
    }
    access.closeConnection()

    // WindowsではShift-JISにしないとWordCloudが上手く動いてくれない
    val encoding = if(Platform.isWindows) Charset.forName("Shift-JIS") else StandardCharsets.UTF_8
    val process = ProcessBuilder(pythonPath, "wc.py").run {
        // pythonのプログラムは相対パスでファイルを指定しているので、カレントディレクトリを移動する必要がある
        directory(File("python"))
        start()
    }

    OutputStreamWriter(process.outputStream, encoding).buffered().use { it.write(sb.toString()) }
    process.inputStream.reader().use { print("Python : ${it.readText()}") }
    process.errorStream.reader().use { print(it.readText()) }

    return process.waitFor() == 0
}