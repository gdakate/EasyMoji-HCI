package com.example.easymoji2

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.easymoji2.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var searchEngine: SemanticSearchEngine
    private lateinit var emojiAdapter: EmojiAdapter
    
    private var searchJob: Job? = null
    private val searchScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        searchEngine = SemanticSearchEngine()

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.test_emoji_recycler_view)
        emojiAdapter = EmojiAdapter(emptyList(), { emojiData ->

        }, useKeyboardLayout = true)
        
        recyclerView?.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 4)
            adapter = emojiAdapter
        }

        val searchEditText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.test_search_edit_text)
        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                val query = s?.toString() ?: ""
                performSearch(query)
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            }
        }
        
        searchEditText?.setOnClickListener {
            showKeyboard()
        }

        binding.fab.visibility = View.GONE
        lifecycleScope.launch {
            EmojiSearchEvaluator.run(searchEngine)
        }

    }

    private fun showKeyboard() {
        val searchEditText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.test_search_edit_text)
        searchEditText?.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun performSearch(query: String) {
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.test_emoji_recycler_view)
        val emptyStateText = findViewById<android.widget.TextView>(R.id.test_empty_state)
        val resultCountText = findViewById<android.widget.TextView>(R.id.test_result_count)
        
        if (query.isBlank()) {
            emojiAdapter.updateEmojis(emptyList())
            emptyStateText?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
            resultCountText?.visibility = View.GONE
            return
        }
        
        searchJob = searchScope.launch {
            delay(300)
            
            val results = withContext(Dispatchers.Default) {
                searchEngine.debugSearch(query, limit = 50)
            }
            
            withContext(Dispatchers.Main) {
                if (results.isEmpty()) {
                    emptyStateText?.text = "No emojis found. Try different keywords!"
                    emptyStateText?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                    resultCountText?.visibility = View.GONE
                } else {
                    emptyStateText?.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                    resultCountText?.visibility = View.VISIBLE
                    emojiAdapter.updateEmojis(results)
                    resultCountText?.text = "Found ${results.size} emoji(s)"
                }
            }
        }
    }




}
