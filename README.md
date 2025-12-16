# EasyMoji: Semantic Emoji Search Keyboard

An Android keyboard that enables natural language emoji search through semantic understanding and contextual filtering.

## Project Overview

EasyMoji is a semantic emoji search keyboard for Android that processes descriptive queries like "someone working late at night" or "annoyed but trying to be patient" to find relevant emojis. Unlike traditional keyword-based search, EasyMoji understands emotions, situations, and visual characteristics through multi-signal similarity scoring.

**Key Features:**
- Natural language query processing
- Semantic search with emotion and situation understanding
- Contextual filtering with intensity detection
- Real-time search with 300ms debouncing
- System-wide Android keyboard integration

## Code Structure

code path : app/src/main/java/com/example/easymoji2

```
easymoji2/
├── ContextualFilter.kt          # Intent detection & relevance filtering
├── EmojiAdapter.kt              # RecyclerView adapter for emoji display
├── EmojiData.kt                 # Emoji data model
├── EmojiDatabase.kt             # Emoji repository (100+ emojis)
├── EmojiKeyboardService.kt      # Android keyboard implementation
├── EmojiSearchEvaluator.kt      # Automated testing framework
├── FirstFragment.kt             # Demo fragment with search
├── LLMQueryConverter.kt         # Query expansion (mock LLM)
├── MainActivity.kt              # Testing interface
├── SecondFragment.kt            # Navigation fragment
└── SemanticSearchEngine.kt      # Core search engine
```

## Core Components

### Data Layer

**EmojiData.kt**
- Data model representing individual emojis
- Fields:
  - `emoji`: The emoji character (String)
  - `keywords`: List of searchable keywords
  - `emotions`: List of emotional states (e.g., "happy", "sad")
  - `situations`: List of usage contexts (e.g., "work", "celebration")
  - `description`: Natural language description
  - `visualFeatures`: Visual characteristics (colors, shapes, patterns)
- Key method: `getAllSearchableText()` - Concatenates all metadata into lowercase string for efficient searching

**EmojiDatabase.kt**
- Singleton object containing emoji repository
- Contains 100+ emojis with comprehensive metadata
- Each emoji includes:
  - Keywords for exact matching
  - Emotions for affective search
  - Situations for contextual search
  - Visual features for appearance-based search (e.g., "green face", "pink heart")
- Production systems would use JSON files or databases; hardcoded for proof-of-concept

### Analysis & Filtering Layer

**LLMQueryConverter.kt**
- Mock LLM interface for query expansion
- Simulates language model analysis through hardcoded patterns
- Key function: `analyzeAndExpand(query)` returns:
  - `expandedKeywords`: Additional related terms
  - `situations`: Inferred contextual scenarios
  - `emotions`: Detected emotional states
  - `intentNuance`: Nuanced interpretation of query
  - `isComplexQuery`: Boolean flag for complex queries
- Handles specific queries like "trying to smile but looking sad" or "someone working late at night"
- Normalizes keywords by removing special characters
- Production version would integrate actual LLM API

**ContextualFilter.kt**
- Intent detection and relevance scoring system
- Data class: `IntentAnalysisResult` containing:
  - Required keywords extracted from query
  - Detected situations and emotions
  - Intent nuance and intensity level
- Key functions:
  1. `analyzeIntent(query, llmAnalysis)`:
     - Extracts required keywords (≥3 chars, excluding stopwords)
     - Detects intensity indicators ("subtle", "very", "extremely")
     - Handles special cases (e.g., "not ready for exam" → anxiety/stress)
     - Combines query analysis with LLM results
  2. `calculateRelevanceScore(emoji, intent, query)`:
     - Returns relevance multiplier in range [0.05, 2.0]
     - Applies hard filtering (0.0) for irrelevant noise categories when strong context exists
     - Penalizes strong emotions for "subtle" queries (multiplier × 0.25)
     - Rewards required keyword matches (+0.15 per match)
     - Bonuses for situation/emotion alignment (+0.08 each)
     - Nuance word matching (+0.2 per word)

### Search Engine

**SemanticSearchEngine.kt**
- Core search orchestration with multi-signal scoring
- **Emotion Expansion Dictionary**: Maps 30+ core emotions/situations to hundreds of related terms
  - Example: "happy" → ["joy", "cheerful", "pleased", "delighted", "smiling", ...]
  - Example: "work" → ["job", "office", "deadline", "typing", "computer", ...]
- **Tokenization**: Removes stopwords, filters short tokens, normalizes to lowercase
- **Query Expansion**: Expands queries using emotion dictionary (exact + partial matching for 4+ char words)
- **Multi-Signal Similarity Scoring**:
  - Exact keyword matches (weight: 1.0)
  - Expanded keyword matches (weight: 1.0)
  - Jaccard similarity of word sets (weight: 1.0)
  - Levenshtein fuzzy matching for typos (weight: 0.8, threshold: 75%)
  - Visual feature alignment (weight: 2.5) - High weight for appearance descriptions
  - Emotion overlap bonus (weight: 1.2)
  - Situation overlap bonus (weight: 2.0)
  - Description containment bonus (+1.0)
- **LLM Integration Bonuses** (when available):
  - Situation matches (weight: 3.0)
  - Emotion matches (weight: 2.5)
  - Expanded keyword matches (weight: 2.0)
  - Intent nuance alignment (+1.5)
- Key methods:
  - `search(query, limit)`: Main search function with contextual filtering
  - `debugSearch(query, limit, debugTopK)`: Search with detailed logging
  - `quickSearch(query, limit)`: Simplified keyword-only search

### UI Layer

**EmojiAdapter.kt**
- RecyclerView adapter for displaying emoji results
- Supports two layout modes:
  - Keyboard layout: Grid display with first keyword only
  - Demo layout: List display with all keywords
- Handles emoji click events via lambda callback
- `updateEmojis()`: Updates displayed emoji list and notifies adapter

**EmojiKeyboardService.kt**
- Extends `InputMethodService` to implement system-wide keyboard
- Lifecycle methods:
  - `onCreateInputView()`: Inflates keyboard layout, initializes search engine and UI
  - `onStartInputView()`: Called when keyboard is shown
  - `onFinishInputView()`: Cancels pending searches when keyboard hidden
  - `onDestroy()`: Cleans up coroutine scope
- Features:
  - Search field with `TextWatcher` for real-time query processing
  - 300ms debouncing via coroutine delay
  - Background search on `Dispatchers.Default` (non-UI thread)
  - GridLayoutManager with 4 columns
  - Empty state handling with user-friendly messages
  - Result count display
- `insertEmoji()`: Uses `InputConnection.commitText()` to insert emoji into active text field
- Works across all Android apps without special integration

**MainActivity.kt**
- Testing and demonstration interface
- Features:
  - In-app emoji search for testing without keyboard setup
  - GridLayoutManager for emoji display
  - Keyboard visibility control
  - Uses `debugSearch()` for detailed logging
  - Automatically runs `EmojiSearchEvaluator` on startup
- Note: FAB (floating action button) hidden for cleaner interface

**FirstFragment.kt**
- Demo fragment with full search functionality
- Features:
  - LinearLayoutManager for vertical list display
  - Clipboard integration - clicking emoji copies to clipboard
  - Toast notifications for user feedback
  - Shows all emojis (up to 50) on initial load
  - Search with 300ms debouncing
  - Result count and empty state handling
- Demonstrates non-keyboard usage of search engine

**SecondFragment.kt**
- Simple navigation fragment
- Part of Android Navigation Component architecture
- Minimal implementation for navigation demonstration

### Evaluation

**EmojiSearchEvaluator.kt**
- Automated testing framework
- Runs predefined queries and logs results to Android Logcat
- Test queries include:
  - "going home late from work"
  - "not ready for exam"
  - "tired after study"
  - "working late"
  - "need coffee"
  - "sleepy night"
- Logs top 5 results per query with emoji descriptions, emotions, and situations
- Tag: "EmojiEval" for filtering Logcat output
- Executed automatically in MainActivity via `lifecycleScope.launch()`

## Architecture Diagram

```
User Query ("someone working late at night")
    ↓
[LLMQueryConverter]
    ├─→ Expand keywords: ["working", "late", "night", "tired", "laptop"]
    ├─→ Detect situation: ["work"]
    └─→ Detect emotion: ["tired"]
    ↓
[ContextualFilter.analyzeIntent]
    ├─→ Extract required keywords (≥3 chars, no stopwords)
    ├─→ Detect intensity: subtle/strong/none
    └─→ Combine LLM + query signals → IntentAnalysisResult
    ↓
[SemanticSearchEngine.calculateSimilarityScore]
    ├─→ Exact keyword matches (weight: 1.0)
    ├─→ Query expansion via emotion dictionary
    ├─→ Jaccard similarity (weight: 1.0)
    ├─→ Levenshtein fuzzy matching (weight: 0.8)
    ├─→ Visual feature matching (weight: 2.5)
    ├─→ Emotion/situation overlap bonuses
    └─→ Semantic score per emoji
    ↓
[ContextualFilter.calculateRelevanceScore]
    ├─→ Noise filtering (party/music/animal when irrelevant)
    ├─→ Required keyword matching (bonus/penalty)
    ├─→ Intensity-based filtering (suppress strong emotions for "subtle")
    ├─→ Nuance matching bonus
    └─→ Relevance multiplier [0.05, 2.0]
    ↓
Final Score = Semantic Score × Relevance Multiplier
    ↓
Filter by threshold (1.0) → Sort descending → Top N results
    ↓
Display: 😫 (tired face), 😴 (sleeping face), 💻 (laptop)
```

## Key Algorithms

### 1. Multi-Signal Similarity Scoring

The search engine combines multiple signals with tuned weights to compute semantic similarity:

```kotlin
score = exactMatches * 1.0 +
        expandedMatches * 1.0 +
        jaccardSimilarity * 1.0 +
        fuzzyMatches * 0.8 +
        visualFeatureMatches * 2.5 +
        emotionOverlap * 1.2 +
        situationOverlap * 2.0 +
        descriptionBonus
```

- **Exact matches**: Direct keyword overlap between query and emoji metadata
- **Expanded matches**: Matches after query expansion via emotion dictionary
- **Jaccard similarity**: Set-based similarity (intersection over union)
- **Fuzzy matching**: Levenshtein distance for typo tolerance (>75% similar)
- **Visual features**: High weight (2.5) for appearance-based queries
- **Emotion/situation**: Contextual alignment bonuses

### 2. Contextual Filtering

Two-stage filtering based on context strength:

**Strong Context** (situations or emotions detected):
- Hard filtering: Removes emojis from irrelevant noise categories (score = 0.0)
- Noise categories: party, game, music, animal, pet, food
- Only filters if category appears in emoji but NOT in query/context

**Weak Context** (no clear situation/emotion):
- Soft filtering: Applies gentle penalty (multiplier × 0.75)
- Prevents aggressive filtering when intent unclear

### 3. Intensity Detection

Adjusts results based on emotional intensity indicators:

**Subtle/Mild Queries**:
- Keywords: "subtle", "mild", "mildly", "not too", "a little"
- Action: Heavily penalizes strong emotion emojis (multiplier × 0.25)
- Suppressed terms: "furious", "rage", "screaming", "explosion", "fire"

**Strong Queries**:
- Keywords: "very", "extremely", "super", "really", "strong"
- Action: Would boost strong emotions (not currently implemented with bonus)

### 4. Stopword Filtering

Removes common English stopwords to focus on meaningful terms:
- Articles: "a", "an", "the"
- Prepositions: "in", "at", "to", "for", "of", "with"
- Auxiliary verbs: "is", "are", "was", "were", "be"
- Pronouns: "i", "you", "we", "they", "he", "she", "it"
- Domain-specific: "emoji", "emojis", "need", "want", "like"

## Installation & Setup

### Prerequisites
- Android Studio (Arctic Fox or later)
- Android device or emulator (API 21+ / Android 5.0+)
- Kotlin support

### Build Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/easymoji.git
   cd easymoji
   ```

2. Open project in Android Studio:
   - File → Open → Select project directory
   - Wait for Gradle sync to complete

3. Build and install:
   - Build → Make Project
   - Run → Run 'app'
   - Select target device

### Keyboard Setup
1. **Enable the keyboard**:
   - Go to: Settings → System → Languages & input → Virtual keyboard → Manage keyboards
   - Toggle on "EasyMoji Keyboard"

2. **Select the keyboard**:
   - Open any app with text input (Messages, Notes, etc.)
   - Tap a text field
   - Tap keyboard icon in navigation bar
   - Select "EasyMoji"

3. **Start searching**:
   - Type descriptive queries in the search field
   - Select emojis from results grid
   - Selected emoji inserts into text field

### Testing Without Keyboard Setup
The app includes a demo interface for testing search functionality without enabling the system keyboard:
- Launch the app
- Use the search field in MainActivity
- Results display in grid layout
- View Logcat (tag: "EmojiEval") for automated evaluation results

## Usage Examples

### Simple Keyword Queries
```
Query: "happy"
Results: 😊 😄 😃 😁 😆

Query: "green face"
Results: 🤢 🤮 (visual feature matching)

Query: "heart"
Results: ❤️ 💕 💖 💗 💓
```

### Situational Queries
```
Query: "someone working late at night"
Results: 😫 😴 💻 (combines work + tired + laptop)

Query: "feeling sweaty after exercise"
Results: 🥵 😓 💪 (exercise + hot + tired)

Query: "need coffee"
Results: ☕ 😴 😫 (coffee + tired)
```

### Emotional Nuance Queries
```
Query: "annoyed but trying to be patient"
Results: 😤 😑 (controlled frustration, no rage/fury)

Query: "not really angry but slightly annoyed"
Results: 😐 😑 🙄 (mild emotions, strong anger filtered)

Query: "trying to smile but looking sad"
Results: 🙂 😔 (conflicting emotions)
```

### Abstract Queries
```
Query: "when you want to end the conversation"
Results: 😴 😐 (tired/neutral → disengagement)

Query: "when you have no thoughts"
Results: 😵 🤔 (confused/dizzy)
```

## Performance

- **Query processing time**: <100ms on mid-range Android devices
- **Result limit**: 50 emojis maximum per query
- **Debouncing delay**: 300ms (prevents redundant searches during typing)
- **Score threshold**: 1.0 (filters low-relevance results)
- **Background processing**: All search on `Dispatchers.Default` (non-UI thread)
- **App size**: <5MB including all metadata and dictionaries
- **Memory usage**: Minimal (in-memory emoji database, no heavy models)

## Technical Stack

- **Language**: Kotlin 1.8+
- **Platform**: Android (API 21+ / Android 5.0+)
- **Concurrency**: Kotlin Coroutines
- **UI Framework**: 
  - RecyclerView for list/grid display
  - Material Design components
  - ViewBinding for type-safe view access
- **Architecture**: 
  - MVVM-like separation of concerns
  - Modular component design
  - Service-based keyboard implementation
- **Key Libraries**:
  - `kotlinx.coroutines` for async operations
  - Android InputMethodService for keyboard
  - RecyclerView for emoji display
  - Material Design for UI components

## Testing

### Automated Evaluation
The app includes `EmojiSearchEvaluator` that runs automatically on startup:
```kotlin
lifecycleScope.launch {
    EmojiSearchEvaluator.run(searchEngine)
}
```

View results in Android Logcat:
```bash
# Filter by tag
adb logcat -s EmojiEval

# Expected output format
EmojiEval: Query: "working late"
EmojiEval:   1. 😫 (Tired face) emotions=[tired, exhausted] situations=[long day, hard work]
EmojiEval:   2. 😴 (Sleeping face) emotions=[tired, sleepy] situations=[bedtime, late night]
```

### Manual Testing
Use MainActivity's search interface:
1. Launch app
2. Type queries in search field
3. Observe results in grid layout
4. Check result count display
5. View debug logs in Logcat

### Test Query Categories
- **Type A (Baseline)**: Short keywords - "happy", "sad", "party"
- **Type B (Situational)**: Context-based - "working late", "after exercise"
- **Type C (Nuanced)**: Complex emotions - "annoyed but patient"
- **Type D (Abstract)**: Meta-level - "want to end conversation"

## Known Limitations

1. **Visual Feature Over-weighting**: Weight of 2.5 sometimes prioritizes object emojis (laptop, coffee) over emotional emojis in work-related queries

2. **Contradictory Emotions**: Queries like "embarrassed but not in a bad way" difficult to model since emoji metadata rarely encodes such nuanced combinations

3. **Abstract Query Dependency**: Meta-level expressions rely heavily on explicit heuristic mappings; unmapped abstract concepts may fail

4. **Metadata Coverage**: Emojis with sparse metadata rank poorly even when contextually relevant

5. **No Personalization**: No learning from user behavior or query history

6. **Hardcoded Dictionary**: Emotion expansion dictionary requires manual maintenance; may reflect creator biases

7. **Limited Emoji Database**: Current implementation includes 100+ emojis; production would need all 3,600+ Unicode emojis

8. **English-Only**: Limited Korean support through pattern matching; no true multilingual capability

## Future Enhancements

### Near-Term Improvements
1. **Scoring Weight Optimization**: Reduce visual feature weight from 2.5 to balance object/emotion signals
2. **Expanded Emoji Database**: Add remaining Unicode emojis with comprehensive metadata
3. **User Preference Storage**: Save frequently used emojis per query
4. **Query History**: Track and suggest recently used queries

### Long-Term Research Directions
1. **LLM Integration**: Replace mock LLM with actual language model (Claude, GPT) for intent analysis
   - On-device models (e.g., MediaPipe, TensorFlow Lite) for privacy
   - Or cloud API with rate limiting and caching

2. **Learned Embeddings**: Replace handcrafted emotion dictionary with word embeddings
   - Train emoji embeddings from usage data
   - Use cosine similarity in embedding space
   - Enable semantic relationships beyond explicit mappings

3. **Multilingual Support**: Extend beyond English
   - Per-language emotion dictionaries
   - Or multilingual embedding models (mBERT, XLM-RoBERTa)
   - Support for language mixing in queries

4. **User Personalization**: Implement on-device learning
   - Track emoji selections per query
   - Adjust ranking based on personal preferences
   - Privacy-preserving (no cloud sync)

5. **Contextual Awareness**: Use system context for better results
   - Time of day (morning → coffee, night → sleep)
   - Location (home → relax, office → work)
   - App context (messaging → casual, email → formal)

6. **Rigorous Evaluation**: Conduct user studies
   - Measure search success rate vs. baseline keyboards
   - Track time-to-emoji metrics
   - Collect user satisfaction surveys
   - A/B testing against Gboard, SwiftKey

## Project Structure Rationale

### Why This Architecture?
- **Modularity**: Each component (analysis, filtering, search) is independently testable and replaceable
- **Separation of Concerns**: Data, logic, and UI clearly separated
- **Extensibility**: Easy to add new signals, filters, or emoji sources
- **Performance**: Background processing and debouncing ensure responsive UI
- **Testability**: Mock LLM interface and evaluator enable systematic testing

### Design Decisions
1. **Hardcoded Emoji Database**: Simplifies proof-of-concept; production would use JSON/database
2. **In-Memory Search**: Fast enough for 100+ emojis; larger databases may need indexing
3. **Rule-Based Filtering**: Sufficient for common cases; LLM would handle edge cases
4. **Manual Dictionary**: Provides control and interpretability; embeddings more scalable

## Contributing

This is an academic project for HCI coursework. For questions or feedback:
- Email: yongeun.cho@stonybrook.edu
- Course: CSE 518 - Human-Computer Interaction
- Institution: Stony Brook University

