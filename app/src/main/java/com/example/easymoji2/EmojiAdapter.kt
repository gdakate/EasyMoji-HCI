package com.example.easymoji2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmojiAdapter(
    private var emojis: List<EmojiData> = emptyList(),
    private val onEmojiClick: (EmojiData) -> Unit,
    private val useKeyboardLayout: Boolean = false
): RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {
    
    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiText: TextView = itemView.findViewById(R.id.emoji_text)
        val emojiKeywords: TextView? = itemView.findViewById(R.id.emoji_keywords)
        
        fun bind(emojiData: EmojiData) {
            emojiText.text = emojiData.emoji
            if (useKeyboardLayout) {
                emojiKeywords?.text = emojiData.keywords.firstOrNull() ?: ""
            } else {
                emojiKeywords?.text = emojiData.keywords.joinToString(", ")
            }
            
            itemView.setOnClickListener {
                onEmojiClick(emojiData)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val layoutId = if (useKeyboardLayout) {
            R.layout.item_emoji_keyboard
        } else {
            R.layout.item_emoji
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return EmojiViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojis[position])
    }
    
    override fun getItemCount(): Int = emojis.size
    
    fun updateEmojis(newEmojis: List<EmojiData>) {
        emojis = newEmojis
        notifyDataSetChanged()
    }
}

