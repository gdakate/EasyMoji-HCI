package com.example.easymoji2

class ContextualFilter {

    data class IntentAnalysisResult(
        val requiredKeywords: Set<String> = emptySet(),
        val situations: Set<String> = emptySet(),
        val emotions: Set<String> = emptySet(),
        val intentNuance: String = "",
        val intensity: String? = null,
        val mainEmotion: String? = null,
        val situation: String? = null
    )

    private val STOPWORDS = setOf(
        "a", "an", "the", "on", "in", "at", "to", "for", "of", "and", "or", "but",
        "is", "are", "was", "were", "be", "been", "being",
        "i", "you", "we", "they", "he", "she", "it",
        "this", "that", "these", "those",
        "my", "your", "our", "their", "his", "her",
        "with", "without", "from", "by", "as", "about",
        "not", "need", "ready", "use", "want", "like", "emoji", "emojis"
    )

    fun calculateRelevanceScore(
        emoji: EmojiData,
        intent: IntentAnalysisResult,
        query: String
    ): Double {

        var multiplier = 1.0
        val queryLower = query.lowercase()
        val emojiText = emoji.getAllSearchableText().lowercase()

        val hasStrongContext =
            intent.situations.isNotEmpty() || intent.emotions.isNotEmpty()


        val noiseCategories = setOf("party", "game", "music", "animal", "pet", "food")

        val relevantContext = intent.situations
            .union(intent.emotions)
            .union(intent.requiredKeywords)
            .toSet()

        if (hasStrongContext) {
            val nonRelevantNoise = noiseCategories
                .filter { it !in relevantContext && !queryLower.contains(it) }
                .toSet()

            if (nonRelevantNoise.any { emojiText.contains(it) }) {
                println("[FILTER CUT] query='$query' emoji='${emoji.emoji} ${emoji.description}' nonRelevantNoise=$nonRelevantNoise")
                return 0.0
            }

        } else {
            if (noiseCategories.any { emojiText.contains(it) } && noiseCategories.none {
                    queryLower.contains(
                        it
                    )
                }) {
                multiplier *= 0.75
            }
        }


        if (intent.requiredKeywords.isNotEmpty()) {
            val requiredMatches = intent.requiredKeywords.count { emojiText.contains(it) }
            if (requiredMatches == 0) {
                multiplier *= 0.75
            } else {
                multiplier += requiredMatches * 0.15
            }
        }


        val isSubtle = (intent.intensity == "subtle"
                || queryLower.contains("subtle")
                || queryLower.contains("mild")
                || queryLower.contains("mildly")
                || queryLower.contains("not too")
                || queryLower.contains("a little"))

        if (isSubtle) {
            val strongSignals = listOf(
                "furious", "rage", "screaming", "explosion", "fire", "bomb",
                "very angry", "extremely", "loudly crying"
            )
            if (strongSignals.any { emojiText.contains(it) }) {
                multiplier *= 0.25
            }
        }

        val nuanceWords = intent.intentNuance
            .lowercase()
            .split(Regex("\\s+"))
            .filter { it.length > 2 }
            .take(8)

        val nuanceMatchCount = nuanceWords.count { w ->
            emojiText.contains(w) || emoji.description.lowercase().contains(w)
        }

        if (nuanceMatchCount > 0) {
            multiplier += nuanceMatchCount * 0.2
        }


        val situationMatches = intent.situations.count { emojiText.contains(it) }
        if (situationMatches > 0) multiplier += situationMatches * 0.08

        val emotionMatches = intent.emotions.count { emojiText.contains(it) }
        if (emotionMatches > 0) multiplier += emotionMatches * 0.08

        return minOf(2.0, multiplier.coerceAtLeast(0.05))
    }


    fun analyzeIntent(
        query: String,
        llmAnalysis: LLMQueryConverter.LLMAnalysisResult?
    ): IntentAnalysisResult {

        val queryLower = query.lowercase()
        val inferredEmotions = mutableSetOf<String>()
        val inferredSituations = mutableSetOf<String>()

        if (queryLower.contains("not ready") && queryLower.contains("exam")) {
            inferredEmotions.addAll(listOf("anxious", "stressed", "nervous"))
            inferredSituations.add("study")
        }
        if (queryLower.contains("break")) {
            inferredEmotions.add("tired")
        }

        val requiredFromQuery = queryLower
            .split(Regex("\\s+"))
            .map { it.replace(Regex("[^a-z0-9]"), "") }
            .filter { it.length >= 3 && it !in STOPWORDS }
            .take(5)
            .toSet()

        val requiredFromLLM = llmAnalysis?.expandedKeywords
            ?.lowercase()
            ?.split(Regex("\\s+"))
            ?.map { it.replace(Regex("[^a-z0-9]"), "") }
            ?.filter { it.length >= 3 && it !in STOPWORDS }
            ?.take(5)
            ?.toSet()
            ?: emptySet()

        val requiredKeywords = (requiredFromQuery + requiredFromLLM).take(6).toSet()

        val intensity = when {
            queryLower.contains("subtle")
                    || queryLower.contains("mild")
                    || queryLower.contains("mildly")
                    || queryLower.contains("not too")
                    || queryLower.contains("a little") -> "subtle"

            queryLower.contains("very")
                    || queryLower.contains("extremely")
                    || queryLower.contains("super")
                    || queryLower.contains("really")
                    || queryLower.contains("strong") -> "strong"

            else -> null
        }

        val situations =
            (llmAnalysis?.situations?.toSet() ?: emptySet()) + inferredSituations

        val emotions =
            (llmAnalysis?.emotions?.toSet() ?: emptySet()) + inferredEmotions

        val nuance = llmAnalysis?.intentNuance ?: ""

        return IntentAnalysisResult(
            requiredKeywords = requiredKeywords,
            situations = situations,
            emotions = emotions,
            intentNuance = nuance,
            intensity = intensity,
            mainEmotion = emotions.firstOrNull(),
            situation = situations.firstOrNull()
        )
    }
}