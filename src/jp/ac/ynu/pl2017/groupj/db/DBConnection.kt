package jp.ac.ynu.pl2017.groupj.db

import java.sql.Connection
import java.sql.DriverManager

/**
 * ThreadLocalを使いスレッドごとにコネクションを管理できるようにしたユーティリティクラス。
 */
object DBConnection {
    private val session = ThreadLocal<Connection>()
    private val url = "jdbc:ucanaccess://haikooking.accdb"

    /**
     * accessへのコネクションを取得する。
     * @return accessへのコネクション
     */
    fun getConnection(): Connection {
        var con = session.get()

        // まだこのThreadに存在しなければ、新しくConnectionをオープンする
        if (con == null) {
            // static initializerでDriverの登録をさせるためにロードが必要
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver")
            con = DriverManager.getConnection(url)
            session.set(con)
        }

        return con
    }

    /**
     * コネクションを切る。スレッドの終了時に必ず呼ばなくてはならない。
     */
    fun closeConnection() {
        session.get()?.close()
        session.set(null)
    }
}