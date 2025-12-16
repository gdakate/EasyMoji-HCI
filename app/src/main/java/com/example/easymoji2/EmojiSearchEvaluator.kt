package com.example.easymoji2

import android.util.Log

object EmojiSearchEvaluator {

    private const val TAG = "EmojiEval"

    suspend fun run(searchEngine: SemanticSearchEngine) {
        val testQueries = listOf(
            "going home late from work",
            "not ready for exam",
            "tired after study",
            "working late",
            "need coffee",
            "sleepy night"
        )

        Log.d(TAG, "===== Emoji Search Evaluation START =====")

        for (query in testQueries) {
            val results = searchEngine.search(query, limit = 5)

            Log.d(TAG, "Query: \"$query\"")
            if (results.isEmpty()) {
                Log.d(TAG, "  ❌ No results")
            } else {
                results.forEachIndexed { index, emoji ->
                    Log.d(
                        TAG,
                        "  ${index + 1}. ${emoji.emoji} (${emoji.description}) " +
                                "emotions=${emoji.emotions} situations=${emoji.situations}"
                    )
                }
            }
        }

        Log.d(TAG, "===== Emoji Search Evaluation END =====")
    }
}
