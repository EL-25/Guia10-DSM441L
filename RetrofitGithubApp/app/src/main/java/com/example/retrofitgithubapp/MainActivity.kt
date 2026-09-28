package com.example.retrofitgithubapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.retrofitgithubapp.databinding.ActivityMainBinding
import com.example.retrofitgithubapp.ui.adapter.RepositoryAdapter
import com.example.retrofitgithubapp.ui.viewmodel.MainViewModel
import com.example.retrofitgithubapp.ui.viewmodel.RepoUiState

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var repoAdapter: RepositoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupRecyclerView()
        setupSearchInput()
        setupQuickSuggestions()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBarLayout.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0
            )
            binding.rvRepositories.setPadding(
                0,
                binding.rvRepositories.paddingTop,
                0,
                systemBars.bottom + 16
            )
            insets
        }
    }

    private fun setupRecyclerView() {
        repoAdapter = RepositoryAdapter { repo ->
            openUrl(repo.htmlUrl)
        }
        binding.rvRepositories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = repoAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchInput() {
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.etUsername.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.btnRetry.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun setupQuickSuggestions() {
        binding.chipMojombo.setOnClickListener { searchFor("mojombo") }
        binding.chipOctocat.setOnClickListener { searchFor("octocat") }
        binding.chipGoogle.setOnClickListener { searchFor("google") }
        binding.chipTorvalds.setOnClickListener { searchFor("torvalds") }
    }

    private fun searchFor(username: String) {
        binding.etUsername.setText(username)
        binding.etUsername.setSelection(username.length)
        performSearch()
    }

    private fun performSearch() {
        hideKeyboard()
        val username = binding.etUsername.text?.toString().orEmpty().trim()
        if (username.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_username), Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.searchRepositories(username)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.brand_primary)
        binding.swipeRefresh.setOnRefreshListener {
            val user = viewModel.currentUsername
            if (user.isNotEmpty()) {
                viewModel.searchRepositories(user)
            } else {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is RepoUiState.Idle -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.layoutInitialState.visibility = View.VISIBLE
                    binding.layoutLoadingState.visibility = View.GONE
                    binding.layoutEmptyState.visibility = View.GONE
                    binding.layoutErrorState.visibility = View.GONE
                    binding.rvRepositories.visibility = View.GONE
                    binding.cardUserProfile.visibility = View.GONE
                }

                is RepoUiState.Loading -> {
                    binding.layoutInitialState.visibility = View.GONE
                    binding.layoutEmptyState.visibility = View.GONE
                    binding.layoutErrorState.visibility = View.GONE

                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.layoutLoadingState.visibility = View.VISIBLE
                        binding.rvRepositories.visibility = View.GONE
                    }
                    binding.tvLoadingMessage.text = getString(R.string.loading_text, state.username)
                }

                is RepoUiState.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.layoutInitialState.visibility = View.GONE
                    binding.layoutLoadingState.visibility = View.GONE
                    binding.layoutEmptyState.visibility = View.GONE
                    binding.layoutErrorState.visibility = View.GONE

                    binding.rvRepositories.visibility = View.VISIBLE
                    repoAdapter.submitList(state.repositories)

                    // Display user profile card
                    val firstRepo = state.repositories.firstOrNull()
                    val avatarUrl = firstRepo?.owner?.avatarUrl
                    val userProfileUrl = firstRepo?.owner?.htmlUrl ?: "https://github.com/${state.username}"

                    binding.cardUserProfile.visibility = View.VISIBLE
                    binding.tvProfileUsername.text = "@${state.username}"
                    binding.tvReposCount.text = getString(R.string.repos_count_format, state.repositories.size)

                    binding.ivUserAvatar.load(avatarUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_person)
                        error(R.drawable.ic_person)
                    }

                    binding.cardUserProfile.setOnClickListener {
                        openUrl(userProfileUrl)
                    }
                    binding.btnOpenUserProfile.setOnClickListener {
                        openUrl(userProfileUrl)
                    }

                    binding.rvRepositories.scrollToPosition(0)
                }

                is RepoUiState.Empty -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.layoutInitialState.visibility = View.GONE
                    binding.layoutLoadingState.visibility = View.GONE
                    binding.layoutErrorState.visibility = View.GONE
                    binding.rvRepositories.visibility = View.GONE
                    binding.cardUserProfile.visibility = View.GONE

                    binding.layoutEmptyState.visibility = View.VISIBLE
                    binding.tvEmptySubtitle.text = getString(R.string.empty_subtitle, state.username)
                }

                is RepoUiState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.layoutInitialState.visibility = View.GONE
                    binding.layoutLoadingState.visibility = View.GONE
                    binding.layoutEmptyState.visibility = View.GONE
                    binding.rvRepositories.visibility = View.GONE
                    binding.cardUserProfile.visibility = View.GONE

                    binding.layoutErrorState.visibility = View.VISIBLE
                    binding.tvErrorMessage.text = state.message
                }
            }
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir el navegador", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: binding.root
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}