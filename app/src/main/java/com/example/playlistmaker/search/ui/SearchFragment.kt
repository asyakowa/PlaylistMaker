package com.example.playlistmaker.search.ui

import android.content.Context
import android.view.View
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val KEY_CHOSEN_TRACK = "chosen_track"
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var simpleTextWatcher: TextWatcher
    private val searchHistoryAdapter = SearchHistoryAdapter()
    private lateinit var trackAdapter: TrackAdapter

    private var isClickAllowed = true
    private var clickJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAdapter = TrackAdapter()
        initAdapters()
        setupListeners()
        setupObservers()
        viewModel.getHistoryList()
    }

    private fun initAdapters() {
        trackAdapter.onItemClick = { track ->
            if (clickDebounce()) {
                viewModel.prepareTrackForPlaying(track)
                navigateToPlayer(track)
            }
        }

        searchHistoryAdapter.onItemClick = { track ->
            if (clickDebounce()) {
                viewModel.prepareTrackForPlaying(track)
                navigateToPlayer(track)
            }
        }

        binding.recyclerView.adapter = trackAdapter
        binding.recyclerVieww.adapter = searchHistoryAdapter
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
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

            val showHistory = state.searchText.isEmpty() && state.searchHistory.isNotEmpty()
            binding.historyView.isVisible = showHistory
            binding.historyView2.isVisible = showHistory
            binding.clearSearchButton.isVisible = showHistory

            binding.recyclerView.isVisible =
                state.searchResults.isNotEmpty() && state.searchText.isNotBlank()
            binding.updateButton.isVisible = state.isError || state.isEmpty
        }
    }

    private fun setupListeners() {
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.onSearchTextChanged(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val isTextEmpty = binding.inputEditText.text?.isEmpty() == true
            val hasHistory = searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()

            val showHistory = hasFocus && isTextEmpty && hasHistory
            binding.historyView.isVisible = showHistory
            binding.historyView2.isVisible = showHistory
            binding.clearSearchButton.isVisible = showHistory
        }

        binding.clearSearchButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.clearSearchHistory()
            }
        }

        binding.updateButton.setOnClickListener {
            viewModel.searchDebounce(viewModel.uiState.value?.searchText.orEmpty())
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
            clickJob?.cancel()
            clickJob = lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    private fun navigateToPlayer(track: Track) {
        val bundle = Bundle().apply {
            putString(KEY_CHOSEN_TRACK, Gson().toJson(track))
        }
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.inputEditText.removeTextChangedListener(simpleTextWatcher)
        _binding = null
    }
}
