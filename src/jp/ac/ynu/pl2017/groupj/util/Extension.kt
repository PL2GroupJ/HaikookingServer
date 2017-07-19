package jp.ac.ynu.pl2017.groupj.util

/**
 * KotlinのuseをAutoCloseableに対応させたもの。
 */
fun <T: AutoCloseable?, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            this?.close()
        } catch (closeException: Exception) {
        }
        throw e
    } finally {
        if (!closed) {
            this?.close()
        }
    }
}

/**
 * リストの要素を交換する。
 * @param i1 一つ目の要素のインデックス
 * @param i2 二つ目の要素のインデックス
 */
fun <T> MutableList<T>.swap(i1: Int, i2: Int) {
    val tmp  = this[i1]
    this[i1] = this[i2]
    this[i2] = tmp
}

/**
 * [ByteArray]のリストを結合して、１つの[ByteArray]にする。
 * @return 結合されたバイト列
 */
fun List<ByteArray>.concat(): ByteArray {
    val retBytes = ByteArray(this.sumBy { it.size })
    var pos = 0
    this.forEach { bytes ->
        System.arraycopy(bytes, 0, retBytes, pos, bytes.size)
        pos += bytes.size
    }
    return retBytes
}