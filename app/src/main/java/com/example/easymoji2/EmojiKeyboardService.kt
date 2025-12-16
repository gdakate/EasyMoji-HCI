package com.example.easymoji2

import android.inputmethodservice.InputMethodService
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*


class EmojiKeyboardService : InputMethodService() {

    private lateinit var keyboardView: View
    private lateinit var searchEngine: SemanticSearchEngine
    private lateinit var emojiAdapter: EmojiAdapter
    
    private var searchJob: Job? = null
    private val searchScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        
        searchEngine = SemanticSearchEngine()
        
        val recyclerView = keyboardView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.emoji_recycler_view)
        emojiAdapter = EmojiAdapter(emptyList(), { emojiData ->
            insertEmoji(emojiData.emoji)
        }, useKeyboardLayout = true)
        
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@EmojiKeyboardService, 4)
            adapter = emojiAdapter
        }
        
        val searchEditText = keyboardView.findViewById<android.widget.EditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                val query = s?.toString() ?: ""
                performSearch(query)
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
        
        searchEditText.requestFocus()
        
        val emptyStateText = keyboardView.findViewById<android.widget.TextView>(R.id.empty_state_text)
        emptyStateText.text = "Type to search for emojis..."
        emptyStateText.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        
        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        searchJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchScope.cancel()
    }


    private fun performSearch(query: String) {
        val recyclerView = keyboardView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.emoji_recycler_view)
        val emptyStateText = keyboardView.findViewById<android.widget.TextView>(R.id.empty_state_text)
        val resultCountText = keyboardView.findViewById<android.widget.TextView>(R.id.result_count_text)
        
        if (query.isBlank()) {
            emojiAdapter.updateEmojis(emptyList())
            emptyStateText.text = "Type to search for emojis..."
            emptyStateText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            resultCountText.text = ""
            return
        }
        
        searchJob = searchScope.launch {
            delay(300)
            
            val results = withContext(Dispatchers.Default) {
                searchEngine.search(query, limit = 50)
            }
            
            withContext(Dispatchers.Main) {
                if (results.isEmpty()) {
                    emptyStateText.text = "No emojis found. Try different keywords!"
                    emptyStateText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    resultCountText.text = ""
                } else {
                    emptyStateText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    emojiAdapter.updateEmojis(results)
                    resultCountText.text = "Found ${results.size} emoji(s)"
                }
            }
        }
    }


    private fun insertEmoji(emoji: String) {
        val ic = currentInputConnection
        
        ic?.commitText(emoji, 1)
    }
}

