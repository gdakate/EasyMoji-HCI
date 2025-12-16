# Problem Statement - EasyMoji Semantic Search

## Current State of Emoji Search

Existing emoji search on mobile keyboards (Google Keyboard, iPhone Keyboard) works **reasonably well for short, keyword-like queries** such as:
- ✅ "green face"
- ✅ "crying"
- ✅ "heart"
- ✅ "party"

This is because each emoji is annotated with a **fixed set of tags** based on Unicode/CLDR standards, and the search matches user input against these tags.

## The Gap

However, **real users often do not remember the exact emoji name or tag**. Instead, they describe what they want in **natural language**, such as:

- ❌ "someone working late at the office"
- ❌ "a face that looks sick but trying to smile"
- ❌ "kind of annoyed but still polite"
- ❌ "feel like I'm going to throw up"
- ❌ "working hard on my laptop"

**Existing keyword-based search does not handle these sentence-like, affective, and vague descriptions well.**

## Problem Categories

### 1. Sentence-level descriptions
- "시험 공부하다가 멘탈 나간 사람"
- "밤 늦게까지 일하는 사람"
- "운동 끝나고 땀 흘리는 느낌"

### 2. Vague emotional nuance
- "짜증나지만 참는 느낌"
- "부끄럽지만 기분은 나쁘지 않은 얼굴"
- "진짜 화난 건 아닌데 살짝 빡친 표정"

### 3. Mixed conditions (situation + emotion)
- "친구랑 싸우고 삐진 상태"
- "열심히 일하는데 피곤해보이는 사람"

### 4. Meta/humorous descriptions
- "그냥 아무 말 하기 싫을 때 쓰는 이모지"
- "대화 끝내고 싶을 때 쓰는 이모지"

## Our Solution

We propose an **NLP-based semantic emoji search system** that:

1. **Accepts descriptive, sentence-level queries** rather than only short tags
2. **Uses text embeddings** to understand the meaning and emotion behind the query
3. **Retrieves emojis based on semantic similarity**, not exact keyword overlap

### Key Differentiators

- ✅ **Sentence understanding**: Processes full sentences, not just keywords
- ✅ **Emotional nuance**: Captures subtle emotional states and mixed feelings
- ✅ **Context awareness**: Understands situations and actions described in natural language
- ✅ **Name-free search**: Works even when users don't know exact emoji names

## Expected Benefits

This approach enables more intuitive, name-free emoji search and is especially beneficial for:

- Ambiguous or mixed emotions
- Long or conversational queries
- Slang, typos, and cross-lingual input
- Natural language descriptions

## Evaluation Strategy

### Query Types for Testing

**Type A: Short keyword queries (baseline strong zone)**
- "초록색 얼굴"
- "토하는"
- "하트"
- → Both systems should work well here

**Type B: Situation/action sentences**
- "시험 공부하다가 멘탈 나간 사람"
- "밤 늦게까지 일하는 사람"
- "운동 끝나고 땀 흘리는 느낌"

**Type C: Emotion + nuance combinations**
- "짜증나지만 참는 느낌"
- "부끄럽지만 기분은 나쁘지 않은 얼굴"

**Type D: Meta/humorous descriptions**
- "그냥 아무 말 하기 싫을 때 쓰는 이모지"
- "대화 끝내고 싶을 때 쓰는 이모지"

**Success Metric**: Our semantic search should achieve higher hit rates and user satisfaction for Type B, C, and D queries compared to keyword-based baseline.

