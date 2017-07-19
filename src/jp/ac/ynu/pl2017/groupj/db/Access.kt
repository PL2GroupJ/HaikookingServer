package jp.ac.ynu.pl2017.groupj.db

import jp.ac.ynu.pl2017.groupj.util.Season
import jp.ac.ynu.pl2017.groupj.util.use
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Accessとのデータのやり取りを仲介するクラス。DBへのアクセスには必ずこのクラスを通す必要がある。
 * 必ずスレッドの終了時にcloseConnection()メソッドでコネクションを切断しなければならない。
 */
class Access {

    /**
     * 季語と使用回数のリストの取得に使用するフラグ。
     */
    enum class Flag { TOTAL, WEEK, MONTH, SPRING, SUMMER, AUTUMN, WINTER, NEW_YEAR }

    /**
     * seasonテーブルのIDを調べる。季語かどうかは-1が返るかで判断する。
     * @param word 判別対象の語
     * @return DBのseasonテーブルのID。存在しないなら-1を返す。
     */
    fun loadSeasonId(word: String): Int {
        val sql = "SELECT season_id FROM detail WHERE word = ${word.q()}"
        var id = -1
        DBConnection.getConnection().createStatement().use { statement ->
            statement.executeQuery(sql).use { result ->
                if (result.next()) id = result.getInt("season_id")
            }
        }
        return id
    }

    /**
     * 季語の季節を調べる。
     * @param word 判別対象の語
     * @return [Season]。存在しないなら[Season.DEFAULT]
     */
    fun loadSeason(word: String): Season {
        val id = loadSeasonId(word)
        val sql = "SELECT season FROM season WHERE id = $id"
        var season = Season.DEFAULT
        DBConnection.getConnection().createStatement().use { statement ->
            statement.executeQuery(sql).use { result ->
                if (result.next()) season = when(result.getInt("season")) {
                    0 -> Season.SPRING
                    1 -> Season.SUMMER
                    2 -> Season.AUTUMN
                    3 -> Season.WINTER
                    4 -> Season.NEW_YEAR
                    else -> Season.DEFAULT
                }
            }
        }
        return season
    }

    /**
     * 指定されたFlagの示す、季語と使用回数のペアのリストを返す。
     * @flag 対象の季語に対応するフラグ
     * @count 要求するペアの個数
     */
    fun loadWordAndCounts(flag: Flag, count: Int): List<Pair<String, Int>> {
        when (flag) {
            Flag.TOTAL, Flag.SPRING, Flag.SUMMER, Flag.AUTUMN, Flag.WINTER, Flag.NEW_YEAR -> return loadSeason(flag, count)
            Flag.MONTH, Flag.WEEK                                                        -> return loadRecent(flag, count)
            else -> error("不正なflagが使用されました")
        }
    }

    /**
     * 季語の使用回数を増加させる。
     * @param word 使った季語
     * @param diff 使った回数の増分
     * @return 処理が正常に終了したらtrue
     */
    fun addUseCount(word: String, diff: Int): Boolean {
        val seasonId = loadSeasonId(word)
        if (seasonId == -1) return false

        val dt = LocalDateTime.now()
        val f = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        val update = "UPDATE season SET use_count = use_count + $diff WHERE id = $seasonId"
        val insert = "INSERT INTO used (season_id, time) VALUES ($seasonId, ${dt.format(f).s()})"
        DBConnection.getConnection().createStatement().use { statement ->
            statement.executeUpdate(update) // 累計の更新
            statement.executeUpdate(insert) // 使用履歴の追加
        }
        return true
    }

    /**
     * 利用しているスレッドのDBへのコネクションを切断する。
     */
    fun closeConnection() {
        DBConnection.closeConnection()
    }

    // 季節か累計の場合
    private fun loadSeason(flag: Flag, count: Int): List<Pair<String, Int>> {
        val sql = "SELECT * FROM season" + when (flag) {
            Flag.TOTAL    -> ""
            Flag.SPRING   -> " WHERE season = 0"
            Flag.SUMMER   -> " WHERE season = 1"
            Flag.AUTUMN   -> " WHERE season = 2"
            Flag.WINTER   -> " WHERE season = 3"
            Flag.NEW_YEAR -> " WHERE season = 4"
            else -> error("flagの指定が間違っています")
        } + " ORDER BY use_count DESC LIMIT 0, $count"

        val list = mutableListOf<Pair<String, Int>>()
        DBConnection.getConnection().createStatement().use { statement ->
            statement.executeQuery(sql).use { result ->
                while (result.next()) {
                    list.add(result.getString("word") to result.getInt("use_count"))
                }
            }
        }
        return list
    }

    // 一週間前、一ヶ月前の場合
    private fun loadRecent(flag: Flag, count: Int): List<Pair<String, Int>> {
        var dt = LocalDateTime.now()
        val f = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        when (flag) {
            Flag.WEEK ->  dt = dt.minusWeeks(1)
            Flag.MONTH -> dt = dt.minusMonths(1)
            else -> error("flagの指定が間違っています")
        }
        val sql = "SELECT season_id, COUNT(*) as c FROM used WHERE time > ${dt.format(f).s()} GROUP BY season_id ORDER BY c DESC LIMIT 0, $count"
        val list = mutableListOf<Pair<Int, Int>>()
        DBConnection.getConnection().createStatement().use { statement ->
            statement.executeQuery(sql).use { result ->
                while (result.next()) {
                    list.add(result.getInt("season_id") to result.getInt("c"))
                }
            }
        }
        val map = loadWordMap(list.map { it.first }.toIntArray())
        return list.map { (id, count) -> map[id]!! to count }
    }

    private fun loadWordMap(ids: IntArray): Map<Int, String> {
        val idString = ids.map { it.toString() }.joinToString(separator = ",")
        val sql = "SELECT id, word FROM season WHERE id IN ($idString)"
        val map = mutableMapOf<Int, String>()
        DBConnection.getConnection().createStatement().use { statement ->
            statement.executeQuery(sql).use { result ->
                while (result.next()) {
                    map.put(result.getInt("id"), result.getString("word"))
                }
            }
        }
        return map
    }

    // 文字列を''で囲った文字列に変換。java -> 'java'
    private fun String.q(): String = "'$this'"

    // 文字列を##で囲った文字列に変換。java -> #java#
    private fun String.s(): String = "#$this#"
}