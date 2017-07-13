package jp.ac.ynu.pl2017.groupj

import jp.ac.ynu.pl2017.groupj.util.ConnectionCommand
import jp.ac.ynu.pl2017.groupj.util.Season
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

class Server(val socket: Socket?): Thread() {
    private val input : DataInputStream?
    private val output : DataOutputStream?
    private val separator = ":*:"

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
                ConnectionCommand.IMAGE ->      writeImage()
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
        println(input!!.readUTF())
    }

    /**
     * 季語を送信する。
     */
    fun writeSeason() {
        // とりあえず新年を送信
        output!!.writeUTF(Season.NEW_YEAR.name)
        println(Season.NEW_YEAR)
        println(Season.NEW_YEAR.name)
    }

    /**
     * アドバイスを送信する。
     */
    private fun writeAdvice() {
        output!!.writeUTF("this is advice")
    }

    /**
     * 名詞のリストを送信する。名詞はseparatorで区切って送信する。
     */
    private fun writeNounList() {
        output!!.writeUTF("this${separator}is${separator}noun${separator}list")
    }

    /**
     * 画像を送信する。WordCloudの画像送信に利用。
     */
    private fun writeImage() {
        val a = javaClass.classLoader.getResourceAsStream("1.png").readBytes() // 適当にサンプルの送信
        output!!.writeInt(a.size)
        socket!!.getOutputStream().write(a)
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
        }
    }
}
