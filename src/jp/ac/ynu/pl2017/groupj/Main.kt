package jp.ac.ynu.pl2017.groupj

import jp.ac.ynu.pl2017.groupj.db.Access
import jp.ac.ynu.pl2017.groupj.util.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

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
        repeat(System.lineSeparator().length) {
            sb.deleteCharAt(sb.length - 1)
        }
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

    serverSocket.use {
        while (true) {
            val socket = it.accept()
            Server(socket).start()
            println("accept : $socket")
        }
    }
}
