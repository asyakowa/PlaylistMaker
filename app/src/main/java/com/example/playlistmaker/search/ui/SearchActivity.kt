package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.AudiopleerActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
<<<<<<< HEAD
        const val KEY_CHOSEN_TRACK = "chosen_track"

    }
=======
        private const val KEY_CHOSEN_TRACK = "KEY_CHOSEN_TRACK"
    }

>>>>>>> b69ea5a (17 cпринт)

private val viewModel by viewModel<SearchViewModel>()
    private lateinit var binding: ActivitySearchBinding
    private lateinit var simpleTextWatcher: TextWatcher

    private val searchHistoryAdapter = SearchHistoryAdapter()
    private lateinit var trackAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        trackAdapter=TrackAdapter  ()
        initAdapters()
        setupListeners()
        setupObservers()

        viewModel.getHistoryList()
    }

    private fun initAdapters() {

      trackAdapter.onItemClick = { track ->

        if (clickDebounce()) {
            viewModel.prepareTrackForPlaying(track)
            val intent = Intent(this, AudiopleerActivity::class.java)
            intent.putExtra(KEY_CHOSEN_TRACK, Gson().toJson(track))
            startActivity(intent)
        }
        }

        binding.recyclerView.adapter = trackAdapter
        binding.recyclerVieww.adapter = searchHistoryAdapter

        searchHistoryAdapter.onItemClick = { track ->
            if (clickDebounce()) {
                viewModel.prepareTrackForPlaying(track)
                startActivity(Intent(this, AudiopleerActivity::class.java).apply {
                    putExtra(KEY_CHOSEN_TRACK, Gson().toJson(track))
                })
            }
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->

            binding.progressBar.isVisible = state.isLoading
            binding.ErrorSearch2.isVisible = state.isError
            binding.ErrorSearch.isVisible = state.isEmpty


            trackAdapter.tracks.clear()
            trackAdapter.tracks.addAll(state.searchResults)
            trackAdapter.notifyDataSetChanged()


            searchHistoryAdapter.searchHistoryTrackList.clear()
            searchHistoryAdapter.searchHistoryTrackList.addAll(state.searchHistory)
            searchHistoryAdapter.notifyDataSetChanged()


            if (binding.inputEditText.text.toString() != state.searchText) {
                binding.inputEditText.setText(state.searchText)
                binding.inputEditText.setSelection(state.searchText.length)
            }


            binding.historyView.isVisible = state.searchText.isEmpty()
                    && state.searchHistory.isNotEmpty()

            binding.historyView2.isVisible = binding.historyView.isVisible
            binding.clearSearchButton.isVisible = binding.historyView.isVisible
            binding.recyclerView.isVisible =
                state.searchResults.isNotEmpty() && state.searchText.isNotBlank()
            binding.updateButton.isVisible = state.isError || state.isEmpty
            if (binding.inputEditText.text.toString() != state.searchText) {
                binding.inputEditText.setText(state.searchText)
                binding.inputEditText.setSelection(state.searchText.length)
            }
        }
    }

    private fun setupListeners() {

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                viewModel.onSearchTextChanged(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val isTextEmpty = binding.inputEditText.text?.isEmpty() == true
            val hasHistory = searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()

            binding.historyView.isVisible = hasFocus && isTextEmpty && hasHistory
            binding.historyView2.isVisible = binding.historyView.isVisible
            binding.clearSearchButton.isVisible = binding.historyView.isVisible
        }

        binding.backbutton.setOnClickListener {
            finish()
        }

        binding.clearSearchButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.clearSearchHistory()
            }
        }

        binding.updateButton.setOnClickListener {
            viewModel.searchDebounce(viewModel.uiState.value?.searchText ?: "")
            hideKeyboard()
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            hideKeyboard()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.inputEditText.removeTextChangedListener(simpleTextWatcher)
    }
}
