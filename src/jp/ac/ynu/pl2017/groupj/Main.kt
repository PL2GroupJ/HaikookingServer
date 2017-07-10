package jp.ac.ynu.pl2017.groupj

import jp.ac.ynu.pl2017.groupj.util.ConnectionCommand
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.ServerSocket
import java.net.Socket

class Server(val socket: Socket?): Thread() {
    private val input : DataInputStream?
    private val output : DataOutputStream?

    init {
        socket!!
        input = DataInputStream(socket.getInputStream())
        output = DataOutputStream(socket.getOutputStream())
    }

    override fun run() {
        loop@ while (true) {
            when (acceptCommand()) {
                ConnectionCommand.HAIKU ->      readHaiku()
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

    private fun readHaiku() {
        println(input!!.readUTF())
    }

    private fun writeAdvice() {
        output!!.writeUTF("this is advice")
    }

    private fun writeNounList() {
        output!!.writeUTF("this/*/is/*/noun/*/list")
    }

    private fun writeImage() {
        val a = File("res/1.png").inputStream().readBytes() // 適当にサンプルの送信
        output!!.writeInt(a.size)
        socket!!.getOutputStream().write(a)
    }

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
