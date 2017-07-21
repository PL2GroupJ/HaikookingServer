package jp.ac.ynu.pl2017.groupj

import jp.ac.ynu.pl2017.groupj.db.Access
import jp.ac.ynu.pl2017.groupj.util.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.ServerSocket
import java.net.Socket
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
        val resources = arrayOf("image/total_wordcloud.png", "image/weekly_wordcloud.png", "image/monthly_wordcloud.png",
                "image/spring_wordcloud.png", "image/summer_wordcloud.png", "image/autumn_wordcloud.png", "image/winter_wordcloud.png",
                "image/newyear_wordcloud.png")
        val byteArrayList = resources.map { javaClass.classLoader.getResourceAsStream(it).use { it.readBytes() } }
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

    // ローカル関数で再帰をする。一日経つごとにに実行
    fun task(dateTime: LocalDateTime = LocalDateTime.now().plusDays(1)) {
        println("next dump is $dateTime")
        dumpDB()
        val timer = Timer()
        timer.schedule(timerTask { task() }, dateTime.toDate() )
    }
    val now = LocalDateTime.now()
    task(now.plusHours(26 - now.hour.toLong()))     // 次の日の午前2時~3時を指定

    println(runPython(args[0]))

    serverSocket.use {
        while (true) {
            val socket = it.accept()
            Server(socket).start()
            println("accept : $socket")
        }
    }
}

fun dumpDB() {
    val access = Access()
    Access.Flag.values().forEach {
        val data = access.loadWordAndCounts(it, 100)
        val text = data.map { (word, count) -> (1..count).map { word } }.flatten().joinToString(separator = ",")
        val fileName = "python/text/" + when (it) {
            Access.Flag.TOTAL -> "total_wordcloud.txt"
            Access.Flag.MONTH -> "monthly_wordcloud.txt"
            Access.Flag.WEEK -> "weekly_wordcloud.txt"
            Access.Flag.SPRING -> "spring_wordcloud.txt"
            Access.Flag.SUMMER -> "summer_wordcloud.txt"
            Access.Flag.AUTUMN -> "autumn_wordcloud.txt"
            Access.Flag.WINTER -> "winter_wordcloud.txt"
            Access.Flag.NEW_YEAR -> "newyear_wordcloud.txt"
        }
        val file = File(fileName)
        if (!file.exists()) file.createNewFile()
        file.writeText(text)
    }
    access.closeConnection()
}

fun runPython(pythonPath: String): Boolean {
    println(pythonPath)
    val process = ProcessBuilder(pythonPath, "python/wc.py").start()
    return process.waitFor() == 0
}