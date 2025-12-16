package com.example.easymoji2


data class EmojiData(
    val emoji: String,
    val keywords: List<String>,
    val emotions: List<String>,
    val situations: List<String>,
    val description: String,
    val visualFeatures: List<String> = emptyList()
) {

    fun getAllSearchableText(): String {
        return (keywords + emotions + situations + visualFeatures + listOf(description))
            .joinToString(" ")
            .lowercase()
    }
}

