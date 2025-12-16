# EasyMoji - Semantic Emoji Search
## 중간보고 발표 자료

---

## 1. Introduction

### 문제 정의의 변화

**초기 문제 정의:**
- "Current emoji search requires exact keywords"
- "Even when users type similar words, desired emoji often does not appear"

**실제 조사 결과:**
- 기존 키보드(구글/아이폰)는 **짧은 키워드 검색은 잘 작동**함
  - ✅ "green face", "crying", "heart" → 잘 작동
  - ✅ "초록색 얼굴", "토하는" → 잘 작동

**재정의된 문제:**
- 기존 키보드는 **문장 수준의 자연어 설명에는 약함**
  - ❌ "someone working late at the office"
  - ❌ "a face that looks sick but trying to smile"
  - ❌ "kind of annoyed but still polite"
  - ❌ "호감을 표현하고 싶은데 부담스럽지 않게"

### 핵심 차별화 포인트

**기존 키보드:**
- 단어 기반 태그 검색
- 짧은 쿼리(1-2단어)에 강함
- 문장/상황/감정 조합에는 약함

**우리의 목표:**
- **문장 수준의 의미 이해**
- **상황과 감정 뉘앙스 파악**
- **관련 없는 이모지 자동 필터링**

---

## 2. Solution of the Topic

### 시스템 아키텍처

```
사용자 쿼리 입력
    ↓
[문장 분석기] SentenceAnalyzer
    - 상황 추출 (일, 공부, 운동 등)
    - 감정 추출 (행복, 슬픔, 화남 등)
    - 행동 추출 (일하는, 운동하는 등)
    ↓
[의도 분석기] ContextualFilter
    - 뉘앙스 이해 (subtle, normal, strong)
    - 제외 키워드 추출
    - 필수 키워드 추출
    ↓
[쿼리 변환기] LLMQueryConverter
    - 한국어 → 영어 변환
    - 키워드 확장
    ↓
[시맨틱 검색 엔진] SemanticSearchEngine
    - 다중 유사도 계산 (Jaccard, Levenshtein)
    - 의미 기반 매칭
    - 문맥 기반 필터링
    ↓
관련성 검증 및 점수 계산
    ↓
이모지 추천 결과
```

### 핵심 기술

#### 1. 문장 수준 의미 분석 (SentenceAnalyzer)

**기능:**
- 자연어 문장에서 상황, 감정, 행동을 자동 추출
- 긴 문장 vs 짧은 키워드 구분

**예시:**
```
입력: "밤새 일해서 힘든 사람"
→ 상황: ["work", "night", "late"]
→ 감정: ["tired", "exhausted"]
→ 행동: ["working"]
```

#### 2. 문맥 기반 필터링 (ContextualFilter)

**문제 해결:**
- ❌ "밤새 일해서 힘든 사람" → 하트가 뜨는 문제
- ✅ 관련 없는 이모지 자동 제거

**작동 방식:**
- **제외 키워드**: "부담스럽지 않게" → heart, love 제외
- **필수 키워드**: "힘든 사람" → face, person, tired 필수
- **상황 매칭**: "일해서" → work, office 관련 우선

**예시:**
```
입력: "호감을 표현하고 싶은데 부담스럽지 않게"
→ 뉘앙스: "subtle"
→ 제외: ["heart", "love", "kiss", "romance"]
→ 결과: 😊😉😌 (subtle한 표현만)
```

#### 3. 시맨틱 검색 엔진 (SemanticSearchEngine)

**다중 유사도 계산:**
1. **정확한 키워드 매칭** (가중치: 2.0)
2. **확장된 쿼리 매칭** (가중치: 1.5)
3. **Jaccard 유사도** (가중치: 3.0)
4. **Levenshtein 거리** (오타 허용)
5. **상황/감정 매칭** (가중치: 3.0)
6. **문맥 관련성 점수** (0.0 ~ 1.0)

**최종 점수:**
```
최종 점수 = 의미 점수 × 관련성 점수
```

관련성이 0이면 완전히 제외, 낮으면 점수 감소

#### 4. 한국어 자연어 처리 (LLMQueryConverter)

**기능:**
- 한국어 자연어 → 영어 키워드 변환
- 키워드 확장 및 동의어 처리

**예시:**
```
"초록색으로 토하는 얼굴"
→ "green vomiting face nauseated sick"
```

---

## 3. Key Features

### 3.1 문장 수준 이해

**Type B: 상황/행동 문장**
- "시험 공부하다가 멘탈 나간 사람" → 😫😵📚
- "밤 늦게까지 일하는 사람" → 💻😴🌙
- "운동 끝나고 땀 흘리는 느낌" → 💪😅💦

### 3.2 감정 뉘앙스 이해

**Type C: 감정 + 뉘앙스**
- "짜증나지만 참는 느낌" → 😤🙄
- "부끄럽지만 기분은 나쁘지 않은 얼굴" → 😳😊
- "진짜 화난 건 아닌데 살짝 빡친 표정" → 😒🙄

### 3.3 관련성 필터링

**문제 해결 사례:**

1. **"호감을 표현하고 싶은데 부담스럽지 않게"**
   - 이전: ❤️💕💖 (너무 직접적)
   - 개선: 😊😉😌 (subtle한 표현)
   - 제외 키워드: heart, love, kiss

2. **"밤새 일해서 힘든 사람"**
   - 이전: 💻😫❤️ (하트가 뜨는 문제)
   - 개선: 💻😫😴🌙 (일/피곤 관련만)
   - 필수 키워드: work, tired, exhausted

### 3.4 다국어 지원

- 한국어: "초록색 얼굴", "호감을 표현하고 싶은데"
- 영어: "green face", "someone working late"
- 혼합 검색 지원

---

## 4. Technical Implementation

### 4.1 데이터 구조

```kotlin
data class EmojiData(
    val emoji: String,
    val keywords: List<String>,        
    val emotions: List<String>,       
    val situations: List<String>,     
    val description: String,           
    val visualFeatures: List<String>  
)
```


1. **쿼리 분석**
   ```kotlin
   val analysis = sentenceAnalyzer.analyzeSentence(query)
   val intent = contextualFilter.analyzeIntent(query)
   ```

2. **의미 점수 계산**
   ```kotlin
   val semanticScore = calculateSimilarityScore(query, emoji, analysis)
   ```

3. **관련성 검증**
   ```kotlin
   val relevanceScore = contextualFilter.calculateRelevanceScore(emoji, intent, query)
   ```

4. **최종 점수**
   ```kotlin
   val finalScore = semanticScore * relevanceScore
   ```

### 4.3 핵심 알고리즘

**Jaccard 유사도:**
```kotlin
fun jaccardSimilarity(set1: Set<String>, set2: Set<String>): Double {
    val intersection = set1.intersect(set2).size
    val union = set1.union(set2).size
    return intersection.toDouble() / union.toDouble()
}
```

**Levenshtein 거리 (오타 허용):**
```kotlin
fun levenshteinSimilarity(word1: String, word2: String): Double {
    val distance = levenshteinDistance(word1, word2)
    val maxLen = maxOf(word1.length, word2.length)
    return 1.0 - (distance.toDouble() / maxLen)
}
```

---

## 5. Demo Examples

### Demo 1: 상황 기반 검색

**쿼리:** "밤새 일해서 힘든 사람"

**결과:**
- 💻 (laptop - 일하는 상황)
- 😫 (tired face - 피곤한 얼굴)
- 😴 (sleeping face - 졸린 얼굴)
- 🌙 (moon - 밤)

**필터링:**
- ❌ ❤️ (heart - 관련 없음, 제외됨)
- ❌ 💕 (two hearts - 관련 없음, 제외됨)

### Demo 2: 뉘앙스 이해

**쿼리:** "호감을 표현하고 싶은데 부담스럽지 않게"

**결과:**
- 😊 (smiling face - 부드러운 미소)
- 😉 (winking face - 장난스러운 윙크)
- 😌 (relieved face - 편안한 표정)

**필터링:**
- ❌ ❤️ (heart - 너무 직접적, 제외됨)
- ❌ 💕 (two hearts - 너무 직접적, 제외됨)
- ❌ 😘 (kiss - 너무 직접적, 제외됨)

### Demo 3: 복합 감정

**쿼리:** "짜증나지만 참는 느낌"

**결과:**
- 😤 (face with steam - 참는 표정)
- 🙄 (rolling eyes - 짜증스러운 표정)
- 😑 (expressionless - 무표정)

---

## 6. Current Status

### ✅ 완료된 기능

1. 문장 수준 의미 분석
2. 문맥 기반 필터링
3. 관련성 검증 시스템
4. 한국어 자연어 처리
5. 시맨틱 검색 엔진
6. Android 키보드 통합
7. 실시간 검색 UI

### 🔄 향후 개선 사항

1. **LLM API 통합**
   - Google Gemini API 또는 OpenAI API
   - 더 정교한 자연어 이해

2. **정량적 평가**
   - 기존 키보드와 비교 실험
   - Top-1, Top-3 hit rate 측정
   - 사용자 만족도 조사

3. **개인화 기능**
   - 사용자 학습 및 패턴 분석
   - 개인별 맞춤 추천

---

## 7. Conclusion

### 기여도

1. **문제 재정의**: 기존 키보드의 한계를 명확히 정의
2. **문장 수준 이해**: 키워드 매칭을 넘어선 의미 기반 검색
3. **문맥 필터링**: 관련 없는 이모지 자동 제거
4. **뉘앙스 이해**: subtle한 감정 표현 지원

### 차별화 포인트

- ✅ 문장 전체를 이해하는 검색
- ✅ 상황과 감정 뉘앙스 파악
- ✅ 관련성 기반 자동 필터링
- ✅ 자연스러운 한국어 입력 지원

---

## Appendix: Test Query Types

### Type A: 짧은 키워드 (Baseline)
- "green face", "crying", "heart"
- 기존 키보드도 잘 작동

### Type B: 상황/행동 문장
- "시험 공부하다가 멘탈 나간 사람"
- "밤 늦게까지 일하는 사람"
- **→ 우리 시스템의 강점**

### Type C: 감정 + 뉘앙스
- "짜증나지만 참는 느낌"
- "부끄럽지만 기분은 나쁘지 않은 얼굴"
- **→ 우리 시스템의 강점**

### Type D: 메타/농담
- "그냥 아무 말 하기 싫을 때 쓰는 이모지"
- **→ 우리 시스템의 강점**

