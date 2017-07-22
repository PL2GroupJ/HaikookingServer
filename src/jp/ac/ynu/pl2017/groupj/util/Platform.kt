package jp.ac.ynu.pl2017.groupj.util

object Platform {
    val os = System.getProperty("os.name").toLowerCase()
    val isLinux = os.startsWith("linux")
    val isMac = os.startsWith("mac")
    val isWindows = os.startsWith("windows")
}