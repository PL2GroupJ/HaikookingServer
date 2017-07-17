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