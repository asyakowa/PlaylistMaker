package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmakersearch.domain.api.TracksInteractor
import com.google.android.material.textview.MaterialTextView
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.content.Intent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.*
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlin.collections.ArrayList
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.AudiopleerActivity
import com.example.playlistmaker.player.ui.KEY_CHOSEN_TRACK
import com.example.playlistmaker.search.domain.models.TrackState
import com.google.gson.Gson
import kotlinx.coroutines.launch
class SearchActivity :
    AppCompatActivity() {
    companion object {
        private const val SEARCH_TEXT_KEY = "search_text"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private var searchString: String = ""

    private val viewModel by viewModels<SearchViewModel>()
    { SearchViewModel.getViewModelFactory() }


    private lateinit var binding: ActivitySearchBinding
    private lateinit var simpleTextWatcher: TextWatcher
    private val searchHistoryAdapter = SearchHistoryAdapter()
    private lateinit var trackAdapter: TrackAdapter
    private var isClickAllowed = true
    private lateinit var progressBar: ProgressBar
    private val handler = Handler(Looper.getMainLooper())


    private lateinit var searchedHistoryTracks: MaterialTextView
    private lateinit var searchedHistoryTracksClearBtn: Button
    private var searchResults = ArrayList<Track>()
    private lateinit var tracksInteractor: TracksInteractor

    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private val tracks = mutableListOf<Track>()
    private var historyTracks = mutableListOf<Track>()

    private lateinit var searchHistoryRepository: SearchHistoryRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gson = Gson()
        val sharedPreferences = getSharedPreferences("HISTORY_KEY", MODE_PRIVATE)
        searchHistoryRepository = SearchHistoryRepositoryImpl(sharedPreferences, gson)


        trackAdapter = TrackAdapter(this, searchHistoryRepository) { trackJson ->
            if (clickDebounce()) {
                val intent = Intent(this, AudiopleerActivity::class.java)
                intent.putExtra(KEY_CHOSEN_TRACK, trackJson)
                Log.d("Тег", "Записываем в историю1")
                startActivity(intent)
            } }


        binding.recyclerView.adapter = trackAdapter
        binding.recyclerVieww.adapter = searchHistoryAdapter


        setupObserves()
        setupListeners()
        setupContent()

    }
    fun setupObserves() {

        viewModel.getSearchLiveData().observe(this) {
            render(it)
        }

        viewModel.getHistoryLiveData().observe(this) {
            updateHistoryUi(it)
        }
    }

    private fun setupListeners() {
        trackAdapter.onItemClick = { track ->
            lifecycleScope.launch {
                openAudioPleer(track)
            }
        }
        searchHistoryAdapter.onItemClick = { track ->
            lifecycleScope.launch {
                openAudioPleer(track)
            }
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)

                binding.historyView.visibility=View.VISIBLE
                binding.historyView2.isVisible =
                    if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && searchHistoryAdapter.searchHistoryTrackList.isNotEmpty())
                        true else false
                binding.clearSearchButton.isVisible =
                    if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()) true else false

                viewModel.searchDebounce(
                    s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {
                searchString = s?.toString() ?: ""
            }

        }


        simpleTextWatcher.let { binding.inputEditText.addTextChangedListener(it) }



        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            val isTextEmpty = binding.inputEditText.text?.isEmpty() == true
          binding.historyView.isVisible  =
             searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()
           binding.clearSearchButton.isVisible =
              searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()
          binding.historyView2.isVisible =
                searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()
       }


        binding.backbutton.setOnClickListener {
            finish()
        }

        binding.clearSearchButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.clearSearchHistory()
                binding.historyView.visibility=View.GONE
        binding.clearSearchButton.visibility=View.GONE
        binding.historyView2.visibility=View.GONE

            }
        }

        binding.updateButton.setOnClickListener {
            viewModel.searchDebounce(searchString)
            closeErrorMessage()

        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            clearSearchList()
            closeErrorMessage()
//            updateHistoryUi(tracks)
            hideKeyboard()
        }
    }
    private fun clearSearchList() {
        trackAdapter.removeItems()
        trackAdapter.notifyDataSetChanged()
//

    }

    fun setupContent() {
        lifecycleScope.launch {
            viewModel.getHistoryList()
            binding.inputEditText.setText(viewModel.getSearchTextLiveData().value)
//            binding.historyView.visibility=View.VISIBLE
//            binding.historyView2.visibility=View.VISIBLE
//            binding.recyclerView.visibility=View.VISIBLE
//            binding.clearSearchButton.visibility=View.VISIBLE


        }
    }
    fun render(state: TrackState) {
        when (state) {
            is TrackState.Content -> showSearchContent(state.tracks)
            is TrackState.Empty -> showError(binding.ErrorSearch)
            is TrackState.Error -> showError(binding.ErrorSearch2)
            is TrackState.Loading -> showLoading()
        }}
    fun showLoading(){

        closeErrorMessage()
        binding.historyView.visibility=View.GONE
        binding.recyclerView.visibility=View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
    fun showSearchContent(tracks: List<Track>) {
        closeErrorMessage()
        binding.progressBar.visibility = View.GONE
        binding.historyView2.visibility=View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        trackAdapter.removeItems()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    fun showError(view: LinearLayout) {
        binding.progressBar.visibility =View.GONE
        binding.recyclerView.visibility=View.GONE
        binding.historyView.visibility=View.GONE
        binding.updateButton.visibility=View.VISIBLE
//        binding.ErrorSearch.visibility=View.VISIBLE
        view.isVisible = true

        hideKeyboard()
    }
    fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    private suspend fun openAudioPleer(track: Track) {
        if (clickDebounce()) {
            viewModel.addTrackInHistory(track)
            Log.d("Тег", "Записываем в историю3")
            startActivity(Intent(this, AudiopleerActivity::class.java))
        }
    }
    fun updateHistoryUi(list: MutableList<Track>) {
        searchHistoryAdapter.searchHistoryTrackList = list
        searchHistoryAdapter.notifyDataSetChanged()
    }
    fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }
    fun closeErrorMessage() {
        binding.ErrorSearch.isVisible = false
        binding.ErrorSearch2.isVisible = false
    }


    fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher.let { binding.inputEditText.removeTextChangedListener(it) }
    } }


