package com.example.retrofitgithubapp.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitgithubapp.R
import com.example.retrofitgithubapp.data.model.Repository
import com.example.retrofitgithubapp.databinding.ItemRepositoryBinding
import java.util.Locale

/**
 * RecyclerView adapter for displaying GitHub repositories.
 */
class RepositoryAdapter(
    private val onRepoClick: (Repository) -> Unit
) : ListAdapter<Repository, RepositoryAdapter.RepoViewHolder>(RepoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = ItemRepositoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RepoViewHolder(
        private val binding: ItemRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repository) {
            val context = binding.root.context

            binding.tvRepoName.text = repo.name
            binding.tvFullName.text = repo.fullName

            // Description
            if (repo.description.isNullOrBlank()) {
                binding.tvDescription.text = context.getString(R.string.no_description)
                binding.tvDescription.setTextColor(ContextCompat.getColor(context, R.color.text_muted))
            } else {
                binding.tvDescription.text = repo.description
                binding.tvDescription.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }

            // Visibility / Fork badge
            if (repo.isFork) {
                binding.tvVisibilityBadge.text = context.getString(R.string.fork_badge)
                binding.tvVisibilityBadge.setBackgroundResource(R.drawable.bg_badge_fork)
                binding.tvVisibilityBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_fork_text))
            } else {
                binding.tvVisibilityBadge.text = context.getString(R.string.public_badge)
                binding.tvVisibilityBadge.setBackgroundResource(R.drawable.bg_badge_public)
                binding.tvVisibilityBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_public_text))
            }

            // Language & Language color dot
            val language = repo.language
            if (!language.isNullOrBlank()) {
                binding.layoutLanguage.visibility = View.VISIBLE
                binding.tvLanguage.text = language
                val colorHex = getLanguageColor(language)
                val dotDrawable = binding.viewLanguageColor.background as? GradientDrawable
                    ?: ContextCompat.getDrawable(context, R.drawable.shape_circle)?.mutate() as? GradientDrawable
                dotDrawable?.setColor(Color.parseColor(colorHex))
                binding.viewLanguageColor.background = dotDrawable
            } else {
                binding.layoutLanguage.visibility = View.GONE
            }

            // Stars & Forks
            binding.tvStars.text = formatCount(repo.stargazersCount)
            binding.tvForks.text = formatCount(repo.forksCount)

            // Click events
            binding.root.setOnClickListener {
                onRepoClick(repo)
            }
            binding.btnOpenWeb.setOnClickListener {
                onRepoClick(repo)
            }
        }

        private fun formatCount(count: Int): String {
            return when {
                count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
                count >= 1_000 -> String.format(Locale.US, "%.1fk", count / 1_000.0)
                else -> count.toString()
            }
        }

        private fun getLanguageColor(language: String): String {
            return when (language.lowercase(Locale.ROOT)) {
                "kotlin" -> "#7F52FF"
                "java" -> "#B07219"
                "python" -> "#3572A5"
                "javascript" -> "#F1E05A"
                "typescript" -> "#3178C6"
                "ruby" -> "#701516"
                "c" -> "#555555"
                "c++" -> "#F34B7D"
                "c#" -> "#178600"
                "go" -> "#00ADD8"
                "rust" -> "#DEA584"
                "swift" -> "#F05138"
                "dart" -> "#00B4AB"
                "html" -> "#E34C26"
                "css" -> "#563D7C"
                "php" -> "#4F5D95"
                "shell" -> "#89E051"
                else -> "#6366F1"
            }
        }
    }

    object RepoDiffCallback : DiffUtil.ItemCallback<Repository>() {
        override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
            return oldItem == newItem
        }
    }
}
