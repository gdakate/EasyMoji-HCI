package com.example.easymoji2

class LLMQueryConverter {

    data class LLMAnalysisResult(
        val expandedKeywords: String,
        val situations: List<String>,
        val emotions: List<String>,
        val intentNuance: String,
        val isComplexQuery: Boolean = false
    )

    fun analyzeAndExpand(query: String): LLMAnalysisResult {
        val lowerQuery = query.lowercase()

        val keywords = mutableSetOf<String>()
        val situations = mutableListOf<String>()
        val emotions = mutableListOf<String>()
        var intentNuance = ""
        var isComplex = false

        when (lowerQuery) {
            "trying to smile but looking sad" -> {
                keywords.addAll(listOf("forced smile", "sad", "trying", "pain", "bittersweet", "tearful"))
                emotions.addAll(listOf("sad", "forced smile"))
                intentNuance = "hiding sadness"
                isComplex = true
            }
            "annoyed but trying to be patient" -> {
                keywords.addAll(listOf("frustration suppressed", "patience", "irritated", "holding-back", "gritting-teeth"))
                emotions.addAll(listOf("annoyed", "patient"))
                intentNuance = "controlled anger or frustration"
                isComplex = true
            }
            "to use when you want to end the conversation" -> {
                keywords.addAll(listOf("silent", "shush", "tired", "done", "finished", "end conversation"))
                situations.add("awkward conversation")
                intentNuance = "ending conversation"
                isComplex = true
            }
            "when you have no thoughts" -> {
                keywords.addAll(listOf("blank space", "empty mind", "daydream", "confused", "dizzy", "no thoughts"))
                situations.add("zoning_out")
                emotions.add("neutral")
                intentNuance = "mental exhaustion or lack of concentration"
                isComplex = true
            }
            "someone working late at night" -> {
                keywords.addAll(listOf("working", "late night", "tired", "study", "coffee", "laptop", "exhausted"))
                situations.add("work")
                emotions.add("tired")
                intentNuance = "dedication or exhaustion due to work"
                isComplex = false
            }
        }

        keywords.addAll(lowerQuery.split(Regex("\\s+")).filter { it.length > 2 })

        val expandedKeywordsString = keywords.joinToString(" ") { normalizeKeyword(it) }

        return LLMAnalysisResult(
            expandedKeywords = expandedKeywordsString,
            situations = situations.distinct(),
            emotions = emotions.distinct(),
            intentNuance = intentNuance.ifBlank { query },
            isComplexQuery = isComplex
        )
    }

    private fun normalizeKeyword(word: String): String {
        return word.replace(Regex("[^a-zA-Z0-9]"), "")
    }

    fun expandQuery(query: String): String {
        return analyzeAndExpand(query).expandedKeywords
    }
}