package com.example.newsify.ui

import android.util.SparseBooleanArray

object Constants {

    fun convertToLowercase(input: String): String {
        val chars = input.toCharArray()
        for (i in chars.indices) {
            val c = chars[i]
            if (c in 'A'..'Z') {
                chars[i] = (c + 32).toChar()
            }
        }
        return String(chars)
    }

    val clickedArticle : HashMap<String,Boolean> = HashMap()
    val selectedItems = mutableListOf<Article>()

}