package com.example.newsify.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsify.databinding.ItemArticlePreviewBinding
import com.example.newsify.ui.Article
import com.example.newsify.ui.Constants
import com.example.newsify.ui.Constants.selectedItems


class NewsAdapter(listener: OnNewsItemClickedListener) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var newsListener: OnNewsItemClickedListener

    init {
        this.newsListener = listener
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class NewsViewHolder(
        val binding: ItemArticlePreviewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {

        val binding = ItemArticlePreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsArticle = differ.currentList[position]

        holder.binding.root.setOnClickListener {
            newsListener.onNewsClicked(newsArticle, holder.binding, position)
        }

        if (newsArticle.urlToImage != null) {
            holder.itemView.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .load(newsArticle.urlToImage)
                .into(holder.binding.ivArticleImage)
            holder.binding.tvTitle.text = newsArticle.title
            holder.binding.tvDescription.text = newsArticle.description
            holder.binding.tvSource.text = newsArticle.source.name
            holder.binding.tvPublishedAt.text = newsArticle.publishedAt
        } else {
            holder.itemView.visibility = View.GONE
            val params = holder.itemView.layoutParams
            params.height = 0
            params.width = 0
            holder.itemView.layoutParams = params
        }
    }

    override fun getItemId(position: Int): Long {
        Log.i("getItemId", position.toString())
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        Log.i("getItemViewType", position.toString())
        return position
    }
    
    interface OnNewsItemClickedListener {
        fun onNewsClicked(article: Article, binding: ItemArticlePreviewBinding, position: Int)
    }
}