# EasyMoji - Semantic Emoji Search Keyboard

## Overview

EasyMoji is an Android keyboard that provides **semantic emoji search** based on natural language understanding, rather than simple keyword matching.

## Problem Statement

Existing emoji search on mobile keyboards (Google Keyboard, iPhone Keyboard) works reasonably well for **short, keyword-like queries** such as:
- ✅ "green face"
- ✅ "crying"
- ✅ "heart"

However, **real users often describe emojis in natural language**, such as:
- ❌ "someone working late at the office"
- ❌ "a face that looks sick but trying to smile"
- ❌ "kind of annoyed but still polite"
- ❌ "feel like I'm going to throw up"

**Existing keyword-based search does not handle these sentence-like, affective, and vague descriptions well.**

## Our Solution

We propose an **NLP-based semantic emoji search system** that:

1. **Accepts descriptive, sentence-level queries** rather than only short tags
2. **Uses text embeddings** to understand the meaning and emotion behind the query
3. **Retrieves emojis based on semantic similarity**, not exact keyword overlap

### Key Features

- ✅ **Sentence understanding**: Processes full sentences, not just keywords
- ✅ **Emotional nuance**: Captures subtle emotional states and mixed feelings
- ✅ **Context awareness**: Understands situations and actions described in natural language
- ✅ **Name-free search**: Works even when users don't know exact emoji names
- ✅ **Multilingual support**: Handles Korean and English queries

## Technical Approach

### 1. Sentence Analysis
- Extracts situations, emotions, and actions from natural language queries
- Identifies long sentences vs. short keywords for appropriate processing

### 2. Semantic Search Engine
- Uses multiple similarity metrics (Jaccard, Levenshtein, keyword expansion)
- Applies higher weights to situation/emotion matches for long sentences
- Combines keyword matching with semantic understanding

### 3. Query Expansion
- Converts Korean natural language to English keywords
- Expands queries with related terms and synonyms
- Handles typos and variations

## Test Query Types

### Type A: Short Keywords (Baseline Strong)
- "green face", "crying", "heart"
- Both systems should work well

### Type B: Situation/Action Sentences
- "시험 공부하다가 멘탈 나간 사람"
- "밤 늦게까지 일하는 사람"
- "운동 끝나고 땀 흘리는 느낌"

### Type C: Emotion + Nuance
- "짜증나지만 참는 느낌"
- "부끄럽지만 기분은 나쁘지 않은 얼굴"

### Type D: Meta/Humorous
- "그냥 아무 말 하기 싫을 때 쓰는 이모지"
- "대화 끝내고 싶을 때 쓰는 이모지"

## Installation

1. Clone the repository
2. Open in Android Studio
3. Build and install on Android device (API 24+)
4. Enable "EasyMoji Keyboard" in Settings > System > Languages & input
5. Select EasyMoji Keyboard as your input method

## Usage

1. Open any app with a text field
2. Switch to EasyMoji Keyboard
3. Type natural language descriptions:
   - "someone working late at the office" → 💻😫
   - "초록색으로 토하는 얼굴" → 🤢
   - "짜증나지만 참는 느낌" → 😤🙄
4. Tap an emoji to insert it

## Project Structure

- `SemanticSearchEngine.kt`: Main search engine with semantic understanding
- `SentenceAnalyzer.kt`: Analyzes sentences to extract situations, emotions, actions
- `LLMQueryConverter.kt`: Converts natural language to searchable keywords
- `EmojiDatabase.kt`: Emoji data with keywords, emotions, situations, visual features
- `EmojiKeyboardService.kt`: Android IME service implementation

## Future Work

- [ ] LLM API integration (Gemini/OpenAI) for advanced understanding
- [ ] User learning and personalization
- [ ] Quantitative evaluation against baseline
- [ ] Support for more languages

## License

This project is for educational/research purposes.

