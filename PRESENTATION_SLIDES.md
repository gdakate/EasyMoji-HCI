# EasyMoji - 중간보고 발표 슬라이드

---

## Slide 1: Introduction - 문제 정의의 변화

### 초기 문제 정의
❌ "Current emoji search requires exact keywords"
❌ "Similar words don't work"

### 실제 조사 결과
✅ 기존 키보드는 **짧은 키워드는 잘 작동**
- "green face" → ✅
- "crying" → ✅
- "heart" → ✅

### 재정의된 문제
❌ **문장 수준의 자연어 설명에는 약함**
- "someone working late at the office"
- "호감을 표현하고 싶은데 부담스럽지 않게"
- "밤새 일해서 힘든 사람"

---

## Slide 2: Solution - 핵심 차별화

### 기존 키보드
- 단어 기반 태그 검색
- 짧은 쿼리(1-2단어)에 강함
- 문장/상황/감정 조합에는 약함

### 우리의 솔루션
✅ **문장 수준의 의미 이해**
✅ **상황과 감정 뉘앙스 파악**
✅ **관련 없는 이모지 자동 필터링**

---

## Slide 3: System Architecture

```
사용자 쿼리
    ↓
[문장 분석] → 상황, 감정, 행동 추출
    ↓
[의도 분석] → 뉘앙스, 제외/필수 키워드
    ↓
[시맨틱 검색] → 의미 기반 매칭
    ↓
[관련성 필터링] → 관련 없는 이모지 제거
    ↓
이모지 추천 결과
```

---

## Slide 4: 핵심 기술 1 - 문장 분석

**SentenceAnalyzer**
- 자연어 문장에서 상황, 감정, 행동 자동 추출

**예시:**
```
"밤새 일해서 힘든 사람"
→ 상황: work, night, late
→ 감정: tired, exhausted
→ 행동: working
```

---

## Slide 5: 핵심 기술 2 - 문맥 필터링

**ContextualFilter**
- 관련 없는 이모지 자동 제거

**문제 해결:**
- ❌ "밤새 일해서 힘든 사람" → 하트가 뜨는 문제
- ✅ 관련 없는 이모지 제거

**작동 방식:**
- 제외 키워드: "부담스럽지 않게" → heart, love 제외
- 필수 키워드: "힘든 사람" → face, person, tired 필수

---

## Slide 6: 핵심 기술 3 - 시맨틱 검색

**SemanticSearchEngine**
- 다중 유사도 계산
  1. 정확한 키워드 매칭
  2. Jaccard 유사도
  3. Levenshtein 거리 (오타 허용)
  4. 상황/감정 매칭
  5. 문맥 관련성 점수

**최종 점수:**
```
최종 점수 = 의미 점수 × 관련성 점수
```

---

## Slide 7: Demo 1 - 상황 기반 검색

**쿼리:** "밤새 일해서 힘든 사람"

**결과:**
- 💻 (laptop)
- 😫 (tired face)
- 😴 (sleeping face)
- 🌙 (moon)

**필터링:**
- ❌ ❤️ (heart - 제외됨)
- ❌ 💕 (two hearts - 제외됨)

---

## Slide 8: Demo 2 - 뉘앙스 이해

**쿼리:** "호감을 표현하고 싶은데 부담스럽지 않게"

**결과:**
- 😊 (smiling face)
- 😉 (winking face)
- 😌 (relieved face)

**필터링:**
- ❌ ❤️ (heart - 너무 직접적)
- ❌ 💕 (two hearts - 너무 직접적)
- ❌ 😘 (kiss - 너무 직접적)

---

## Slide 9: Demo 3 - 복합 감정

**쿼리:** "짜증나지만 참는 느낌"

**결과:**
- 😤 (face with steam)
- 🙄 (rolling eyes)
- 😑 (expressionless)

---

## Slide 10: Current Status

### ✅ 완료된 기능
1. 문장 수준 의미 분석
2. 문맥 기반 필터링
3. 관련성 검증 시스템
4. 한국어 자연어 처리
5. 시맨틱 검색 엔진
6. Android 키보드 통합

### 🔄 향후 개선
1. LLM API 통합 (Gemini/OpenAI)
2. 정량적 평가
3. 개인화 기능

---

## Slide 11: Conclusion

### 기여도
1. 문제 재정의: 기존 키보드의 한계 명확화
2. 문장 수준 이해: 의미 기반 검색
3. 문맥 필터링: 관련 없는 이모지 제거
4. 뉘앙스 이해: subtle한 감정 표현 지원

### 차별화 포인트
✅ 문장 전체를 이해하는 검색
✅ 상황과 감정 뉘앙스 파악
✅ 관련성 기반 자동 필터링

