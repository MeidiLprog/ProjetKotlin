package com.example.amioupas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnswerAdapter(
    private val answers: MutableList<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    inner class AnswerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val answerText: TextView = view.findViewById(R.id.answerText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_answer, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val answer = answers[position]
        holder.answerText.text = answer
        holder.answerText.setOnClickListener { onClick(answer) }
    }

    override fun getItemCount(): Int = answers.size

    fun updateAnswers(newAnswers: List<String>) {
        answers.clear()
        answers.addAll(newAnswers)
        notifyDataSetChanged()
    }
}
