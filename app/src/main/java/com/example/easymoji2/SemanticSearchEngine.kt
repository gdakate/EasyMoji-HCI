package com.example.easymoji2

class SemanticSearchEngine {

    private val llmConverter = LLMQueryConverter()
    private val contextualFilter = ContextualFilter()
    private val DEBUG = true



    private val emotionExpansion = mapOf(
        "happy" to listOf("joy", "joyful", "glad", "cheerful", "pleased", "delighted", "excited", "ecstatic", "elation", "bliss", "glee", "merry", "smiley", "laughing", "content", "grinning", "smiling", "giggle", "relief", "satisfied"),
        "love" to listOf("adore", "affection", "caring", "romance", "romantic", "loving", "fond", "passion", "crush", "heart", "valentine", "smooch", "kiss", "infatuation", "devotion", "hug"),
        "sad" to listOf("unhappy", "sorrowful", "melancholy", "depressed", "down", "blue", "gloomy", "grief", "despair", "misery", "crying", "tearful", "upset", "pensive", "lonely", "broken-heart"),
        "cry" to listOf("weep", "sob", "tears", "crying", "sad", "bawling", "wail", "teardrop", "distraught"),
        "angry" to listOf("mad", "furious", "rage", "irritated", "annoyed", "frustrated", "upset", "livid", "enraged", "fury", "pissed", "grumpy", "scowl", "hostile", "resentment", "steam"),
        "tired" to listOf("exhausted", "weary", "sleepy", "drowsy", "fatigued", "worn out", "drained", "spent", "nap", "slumber", "yawn", "rest", "zzz", "bed", "overworked"),
        "scared" to listOf("fear", "frightened", "terrified", "anxious", "horror", "panic", "spooked", "monster", "ghost", "creepy", "shock", "tremble", "shiver"),
        "anxious" to listOf("worried", "nervous", "stressed", "uneasy", "tension", "apprehension", "sweat", "fret", "stressful", "concern", "doubt"),
        "surprised" to listOf("shocked", "amazed", "astonished", "stunned", "startled", "awe", "wonder", "jaw-dropped", "gasp", "unexpected", "wow", "oops"),
        "proud" to listOf("honor", "achievement", "success", "victory", "triumph", "medal", "champion", "celebrate", "boast", "praise"),
        "confused" to listOf("puzzled", "bewildered", "perplexed", "doubt", "unclear", "huh", "head-scratching", "dizzy", "uncomprehending"),

        "work" to listOf("job", "office", "business", "professional", "career", "project", "meeting", "deadline", "late", "presentation", "report", "hustle", "coworker", "remote", "typing", "computer", "desk", "email", "document", "file", "briefcase", "labour"),
        "study" to listOf("learn", "education", "school", "homework", "reading", "exam", "test", "quiz", "student", "teacher", "class", "university", "college", "book", "lecture", "revise", "library", "notes", "grade", "research", "knowledge"),
        "travel" to listOf("trip", "journey", "vacation", "adventure", "flight", "plane", "car", "train", "roadtrip", "explore", "sightseeing", "tourist", "luggage", "map", "destination", "passport", "hotel", "beach", "mountain", "commute"),
        "home" to listOf("house", "family", "relax", "chill", "couch", "bed", "living room", "kitchen", "cleaning", "renovation", "movie", "tv", "room", "apartment", "door", "window", "roof"),
        "sport" to listOf("game", "play", "athletic", "exercise", "fitness", "workout", "gym", "ball", "team", "win", "lose", "match", "compete", "run", "cycle", "yoga", "swimming", "score", "trophy", "medal", "stadium"),
        "food" to listOf("meal", "eat", "dining", "cuisine", "dish", "cooking", "dinner", "lunch", "breakfast", "snack", "hungry", "chef", "restaurant", "delicious", "tasty", "drink", "coffee", "tea", "pizza", "burger", "fruit", "vegetable", "dessert", "baking", "sweet"),
        "music" to listOf("song", "tune", "melody", "sound", "audio", "sing", "dance", "band", "concert", "headphones", "speaker", "volume", "pop", "rock", "jazz", "instrument", "microphone", "rhythm"),
        "party" to listOf("celebration", "festival", "fun", "birthday", "anniversary", "event", "drink", "dance", "music", "gathering", "alcohol", "cheers", "disco", "wedding", "christmas", "holiday", "balloons", "gifts", "fireworks"),
        "talking" to listOf("chat", "speak", "communicate", "voice", "call", "conversation", "whisper", "shout", "message", "reply"),
        "thinking" to listOf("brain", "idea", "mind", "ponder", "question", "imagine", "smart", "clever", "logic", "philosophy"),
        "shopping" to listOf("buy", "store", "mall", "sale", "discount", "cart", "bag", "purchase", "checkout", "retail", "fashion", "clothes"),
        "giving" to listOf("gift", "present", "sharing", "donation", "offer", "helping", "charity"),

        "weather" to listOf("sunny", "rain", "snow", "cloud", "storm", "wind", "cold", "hot", "winter", "summer", "forecast", "umbrella", "sunshine", "blizzard", "temperature", "lightning", "flood", "hail"),
        "time" to listOf("clock", "watch", "hour", "minute", "second", "late", "early", "past", "future", "calendar", "day", "week", "month", "deadline", "schedule", "tomorrow", "yesterday"),
        "animal" to listOf("pet", "dog", "cat", "bird", "fish", "mammal", "reptile", "insect", "wildlife", "zoo", "nature", "domestic", "lion", "tiger", "bear", "mouse", "horse"),
        "technology" to listOf("phone", "computer", "internet", "website", "social media", "app", "message", "typing", "coding", "software", "update", "online", "chat", "email", "wifi", "keyboard", "mouse", "camera", "photo", "video", "data"),
        "money" to listOf("cash", "dollar", "euro", "wealth", "finance", "bank", "invest", "buy", "sell", "shopping", "pay", "credit", "wallet", "save", "stock", "loan", "tax", "coin"),
        "light" to listOf("bright", "sun", "lamp", "glow", "shine", "day", "bulb", "lantern", "candle"),
        "dark" to listOf("night", "shadow", "dim", "black", "moon", "gothic", "midnight"),
        "tool" to listOf("hammer", "wrench", "saw", "screw", "nail", "workman", "fix", "repair", "build"),
        "vehicle" to listOf("car", "bus", "truck", "bike", "motorcycle", "ship", "boat", "plane", "train", "transport", "drive", "ride", "wheel"),

        "good" to listOf("great", "nice", "excellent", "fine", "okay", "ok", "awesome", "fantastic", "wonderful", "amazing", "cool", "super", "perfect", "terrific", "positive", "approval"),
        "bad" to listOf("terrible", "awful", "horrible", "poor", "not good", "worst", "sucks", "disappointing", "failure", "negative", "reject", "fail"),
        "up" to listOf("above", "high", "raise", "increase", "top", "ascend"),
        "down" to listOf("below", "low", "lower", "decrease", "bottom", "descend"),
        "fast" to listOf("quick", "speed", "rapid", "soon", "hurry", "accelerate"),
        "slow" to listOf("turtle", "snail", "delay", "wait", "leisurely", "gradual"),
        "hot" to listOf("warm", "heat", "fire", "sun", "summer", "spicy"),
        "cold" to listOf("chill", "ice", "winter", "freeze", "snow", "frost"),

        "glad" to listOf("happy", "joyful", "pleased", "delighted", "relieved", "thankful"),
        "adore" to listOf("love", "worship", "cherish", "treasure", "admire"),
        "unhappy" to listOf("sad", "miserable", "depressed", "down", "melancholy"),
        "furious" to listOf("angry", "rage", "enraged", "livid", "wrath"),
        "shocked" to listOf("surprised", "stunned", "amazed", "astonished", "aghast"),
        "exhausted" to listOf("tired", "weary", "drained", "spent", "overworked"),
        "hungry" to listOf("starving", "famished", "craving", "appetite", "noms", "foodie"),
        "nice" to listOf("good", "great", "fine", "pleasant", "lovely", "kind", "friendly"),
        "wonderful" to listOf("great", "amazing", "fantastic", "marvelous"),
        "fantastic" to listOf("amazing", "wonderful", "great", "incredible"),
        "meal" to listOf("food", "eat", "dining", "dinner", "lunch", "breakfast", "snack"),
        "game" to listOf("play", "gaming", "sport", "entertainment", "fun", "challenge", "board game", "video game"),
    )
    private val STOPWORDS = setOf(
        "a","an","the","on","in","at","to","for","of","and","or","but",
        "is","are","was","were","be","been","being",
        "i","you","we","they","he","she","it",
        "this","that","these","those",
        "my","your","our","their","his","her",
        "with","without","from","by","as","about"
    )


    private fun tokenize(text: String): List<String> =
        text.lowercase()
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .filter { it.length >= 2 }
            .filter { it !in STOPWORDS } // ✅ STOPWORDS 사용

    private fun expandQuery(query: String): Set<String> {
        val words = tokenize(query)
        val expanded = mutableSetOf<String>()

        for (w in words) {
            expanded.add(w)

            emotionExpansion[w]?.forEach { expanded.add(it) }

            if (w.length >= 4) {
                emotionExpansion.keys.forEach { key ->
                    if (key.contains(w) || w.contains(key)) {
                        emotionExpansion[key]?.forEach { expanded.add(it) }
                    }
                }
            }
        }
        return expanded
    }


    private fun jaccardSimilarity(set1: Set<String>, set2: Set<String>): Double {
        if (set1.isEmpty() && set2.isEmpty()) return 1.0
        if (set1.isEmpty() || set2.isEmpty()) return 0.0
        val intersection = set1.intersect(set2).size
        val union = set1.union(set2).size
        return intersection.toDouble() / union.toDouble()
    }

    private fun textToWordSet(text: String): Set<String> {
        return text.lowercase()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .toSet()
    }

    private fun levenshteinSimilarity(word1: String, word2: String): Double {
        val distance = levenshteinDistance(word1.lowercase(), word2.lowercase())
        val maxLen = maxOf(word1.length, word2.length)
        if (maxLen == 0) return 1.0
        return 1.0 - (distance.toDouble() / maxLen)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    private fun calculateSimilarityScore(
        query: String,
        emojiData: EmojiData,
        llmAnalysis: LLMQueryConverter.LLMAnalysisResult? = null
    ): Double {

        val queryLower = query.lowercase()
        val emojiText = emojiData.getAllSearchableText()
        val emojiWordSet = textToWordSet(emojiText)

        val queryWordSet = tokenize(query).toSet()

        val expandedQueryWordSet = expandQuery(query)

        var score = 0.0

        if (llmAnalysis != null) {
            val llmSituations = llmAnalysis.situations.toSet()
            val llmEmotions = llmAnalysis.emotions.toSet()
            val llmExpandedWordSet = textToWordSet(llmAnalysis.expandedKeywords)

            val situationMatches = llmSituations.intersect(emojiData.situations.toSet()).size
            score += situationMatches * 3.0

            val emotionMatches = llmEmotions.intersect(emojiData.emotions.toSet()).size
            score += emotionMatches * 2.5

            val extractedMatches = llmExpandedWordSet.intersect(emojiWordSet).size
            score += extractedMatches * 2.0

            if (llmAnalysis.intentNuance.lowercase()
                    .split("\\s+".toRegex())
                    .any { it.isNotBlank() && emojiData.description.lowercase().contains(it) }
            ) {
                score += 1.5
            }

            llmSituations.forEach { situation ->
                if (emojiWordSet.contains(situation) || emojiData.description.lowercase().contains(situation)) {
                    score += 1.0
                }
            }
            llmEmotions.forEach { emotion ->
                if (emojiWordSet.contains(emotion) || emojiData.description.lowercase().contains(emotion)) {
                    score += 0.8
                }
            }
        }

        val exactMatches = queryWordSet.intersect(emojiWordSet).size
        score += exactMatches * 1.0

        val expandedMatches = expandedQueryWordSet.intersect(emojiWordSet).size
        score += expandedMatches * 1.0

        score += jaccardSimilarity(queryWordSet, emojiWordSet) * 1.0
        score += jaccardSimilarity(expandedQueryWordSet, emojiWordSet) * 1.0

        queryWordSet.forEach { q ->
            val best = emojiWordSet.maxOf { e ->
                when {
                    e.contains(q) || q.contains(e) -> 1.0
                    else -> levenshteinSimilarity(q, e)
                }
            }
            if (best > 0.75) score += best * 0.8
        }

        score += queryWordSet.intersect(emojiData.emotions.toSet()).size * 1.2
        score += queryWordSet.intersect(emojiData.situations.toSet()).size * 2.0

        if (emojiData.description.lowercase().contains(queryLower)) score += 1.0

        score += queryWordSet.intersect(emojiData.visualFeatures.toSet()).size * 2.5

        return score
    }

    suspend fun search(query: String, limit: Int = 20): List<EmojiData> {
        if (query.isBlank()) return EmojiDatabase.emojis.take(limit)

        val llmAnalysis = llmConverter.analyzeAndExpand(query)
        val intent = contextualFilter.analyzeIntent(query, llmAnalysis)

        val MIN_RELEVANCE_THRESHOLD = 1.0

        val scoredEmojis: List<Pair<EmojiData, Double>> =
            EmojiDatabase.emojis.map { emoji ->
                val semanticScore = calculateSimilarityScore(query, emoji, llmAnalysis)

                val relevanceMultiplier = contextualFilter.calculateRelevanceScore(emoji, intent, query)

                val finalScore = semanticScore * relevanceMultiplier
                emoji to finalScore
            }

        return scoredEmojis
            .filter { it.second > MIN_RELEVANCE_THRESHOLD }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    fun quickSearch(query: String, limit: Int = 20): List<EmojiData> {
        if (query.isBlank()) return EmojiDatabase.emojis.take(limit)

        val queryLower = query.lowercase()
        val queryWords = queryLower.split("\\s+".toRegex()).filter { it.isNotBlank() }

        return EmojiDatabase.emojis
            .map { emoji ->
                val searchableText = emoji.getAllSearchableText().lowercase()
                val matchCount = queryWords.count { word ->
                    searchableText.contains(word) ||
                            emoji.keywords.any { it.lowercase().contains(word) || word.contains(it.lowercase()) }
                }
                emoji to matchCount.toDouble()
            }
            .filter { it.second > 0.0 }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    suspend fun debugSearch(query: String, limit: Int = 20, debugTopK: Int = 10): List<EmojiData> {
        if (query.isBlank()) return EmojiDatabase.emojis.take(limit)

        val llmAnalysis = llmConverter.analyzeAndExpand(query)
        val intent = contextualFilter.analyzeIntent(query, llmAnalysis)

        if (DEBUG) {
            println("========== DEBUG SEARCH ==========")
            println("Query: $query")
            println("LLM situations: ${llmAnalysis.situations}")
            println("LLM emotions: ${llmAnalysis.emotions}")
            println("LLM intentNuance: ${llmAnalysis.intentNuance}")
            println("LLM expandedKeywords: ${llmAnalysis.expandedKeywords}")
            println("Intent.requiredKeywords: ${intent.requiredKeywords}")
            println("Intent.situations: ${intent.situations}")
            println("Intent.emotions: ${intent.emotions}")
            println("Intent.intensity: ${intent.intensity}")
            println("==================================")
        }

        val MIN_RELEVANCE_THRESHOLD = 1.0
        val scored: List<Triple<EmojiData, Double, String>> = EmojiDatabase.emojis.map { emoji ->
            val semanticScore = calculateSimilarityScore(query, emoji, llmAnalysis)
            val multiplier = contextualFilter.calculateRelevanceScore(emoji, intent, query)
            val finalScore = semanticScore * multiplier

            val reason = buildString {
                append("semantic=").append(String.format("%.2f", semanticScore))
                append(", mult=").append(String.format("%.2f", multiplier))
                append(", final=").append(String.format("%.2f", finalScore))
                append(" | em=").append(emoji.emotions)
                append(" | sit=").append(emoji.situations)
            }

            Triple(emoji, finalScore, reason)
        }

        if (DEBUG) {
            val top = scored.sortedByDescending { it.second }.take(debugTopK)
            println("---- TOP $debugTopK (before threshold) ----")
            top.forEachIndexed { idx, t ->
                println("${idx + 1}. ${t.first.emoji} ${t.first.description} -> ${t.third}")
            }

            val passed = scored.count { it.second > MIN_RELEVANCE_THRESHOLD }
            println("Passed threshold($MIN_RELEVANCE_THRESHOLD): $passed / ${scored.size}")
        }

        return scored
            .filter { it.second > MIN_RELEVANCE_THRESHOLD }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

}
