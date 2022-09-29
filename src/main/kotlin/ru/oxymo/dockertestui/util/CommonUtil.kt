package ru.oxymo.dockertestui.util

object CommonUtil {

    fun getErrorTextFromException(e: Exception) =
        "${e::class.java.simpleName}: ${e.message ?: Constants.EMPTY_STRING}"

}