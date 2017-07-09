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