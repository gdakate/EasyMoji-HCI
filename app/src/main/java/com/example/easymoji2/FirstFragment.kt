package com.example.easymoji2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymoji2.databinding.FragmentFirstBinding
import kotlinx.coroutines.*



class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var emojiAdapter: EmojiAdapter
    private lateinit var searchEngine: SemanticSearchEngine

    private var searchJob: Job? = null
    private val searchScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchEngine = SemanticSearchEngine()

        emojiAdapter = EmojiAdapter(
            emojis = emptyList(),
            onEmojiClick = { emojiData ->
                copyToClipboard(emojiData.emoji)
                Toast.makeText(
                    requireContext(),
                    "Copied: ${emojiData.emoji}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            useKeyboardLayout = false
        )
        binding.emojiRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = emojiAdapter
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()

                val query = s?.toString() ?: ""
                performSearch(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        showAllEmojis()
    }


    private fun performSearch(query: String) {
        searchJob = searchScope.launch {
            delay(300)

            val results = withContext(Dispatchers.Default) {
                if (query.isBlank()) {
                    EmojiDatabase.emojis.take(50)
                } else {
                    searchEngine.search(query, limit = 50)
                }
            }

            withContext(Dispatchers.Main) {
                updateResults(results, query)
            }
        }
    }


    private fun updateResults(results: List<EmojiData>, query: String) {
        emojiAdapter.updateEmojis(results)

        if (query.isBlank()) {
            binding.resultCountText.text = "Showing ${results.size} emojis"
        } else {
            binding.resultCountText.text = "Found ${results.size} emoji(s) for \"$query\""
        }

        if (results.isEmpty() && query.isNotBlank()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.emojiRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.emojiRecyclerView.visibility = View.VISIBLE
        }
    }


    private fun showAllEmojis() {
        val allEmojis = EmojiDatabase.emojis.take(50)
        emojiAdapter.updateEmojis(allEmojis)
        binding.resultCountText.text = "Showing ${allEmojis.size} emojis"
        binding.emptyStateText.visibility = View.GONE
        binding.emojiRecyclerView.visibility = View.VISIBLE
    }


    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE)
            as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Emoji", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        searchScope.cancel()
        _binding = null
    }
}
