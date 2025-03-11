package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList



class SearchActivity : AppCompatActivity() {
    private val retrofit = Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackApiService = retrofit.create(TrackApiService::class.java)
    private val tracks = ArrayList<Track>()
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var updateButton: Button
     private var historyTracks = ArrayList<Track>()
    private var trackAdapter: TrackAdapter? = null
    private var savedSearchText: String = ""
    private lateinit var searchedHistoryTracks: MaterialTextView
    private lateinit var searchedHistoryTracksClearBtn: Button

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        historyTracks = SearchHistory.getHistory()
        updateButton = findViewById(R.id.updateButton)
        updateButton.visibility = View.GONE
        inputEditText = findViewById(R.id.inputEditText)
        clearIcon = findViewById(R.id.clearIcon)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderText = findViewById(R.id.placeholderText)
        placeholderImage = findViewById(R.id.placeholderImage)
        val backButton = findViewById<ImageView>(R.id.backbutton)
        backButton.setOnClickListener {

            finish()
        }
        searchedHistoryTracks = findViewById(R.id.historyView)
        searchedHistoryTracksClearBtn = findViewById(R.id.clearSearchButton)
        trackAdapter = TrackAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = trackAdapter
        trackAdapter!!.tracks = tracks
        if (inputEditText.text.isEmpty() && inputEditText.hasFocus() && historyTracks.isNotEmpty()) {
            showHistory()
        }
        savedInstanceState?.let {
            savedSearchText = it.getString(SEARCH_TEXT_KEY).toString()
            inputEditText.setText(savedSearchText)
        }

        setupTextWatcher()
        clearIcon.setOnClickListener {
            inputEditText.setText("")
            clearSearch()

        }
        updateButton.setOnClickListener {
            search(inputEditText)
        }

        searchedHistoryTracksClearBtn.setOnClickListener {
            SearchHistory.clearHistory()
            historyTracks.clear()
            historyTracksClearBtn()
            trackAdapter?.notifyDataSetChanged()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            focusforBtn(hasFocus)
        }
    }

    private fun focusforBtn(hasFocus: Boolean) {
        if (hasFocus && inputEditText.text.isEmpty() && historyTracks.isNotEmpty()) {
            searchedHistoryTracks.visibility = View.VISIBLE
            searchedHistoryTracksClearBtn.visibility = View.VISIBLE
        } else {
            historyTracksClearBtn()
        }
        trackAdapter?.tracks = historyTracks
        trackAdapter?.notifyDataSetChanged()
    }



private fun showHistory() {
    searchedHistoryTracks.visibility = View.VISIBLE
    searchedHistoryTracksClearBtn.visibility = View.VISIBLE
    historyTracks = SearchHistory.getHistory()
    trackAdapter?.tracks = historyTracks
    trackAdapter?.notifyDataSetChanged()
}

private fun historyTracksClearBtn() {
    searchedHistoryTracks.visibility = View.GONE
    searchedHistoryTracksClearBtn.visibility = View.GONE
}
    private fun searchedTracksClearButtonVisibility(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            searchedHistoryTracksClearBtn.visibility = View.GONE
            tracks.clear()
            trackAdapter?.notifyDataSetChanged()
            if (historyTracks.isNotEmpty()) {
                showHistory()
            } else {
                historyTracksClearBtn()
            }
        } else  {
            searchedHistoryTracksClearBtn.visibility = View.VISIBLE
            historyTracksClearBtn()
            trackAdapter?.tracks = tracks
        }
    }


    private fun setupTextWatcher() {
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(inputEditText)
                true
            }
            false
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (charSequence.isNullOrEmpty()) View.GONE else View.VISIBLE
                savedSearchText = charSequence.toString()
                searchedTracksClearButtonVisibility(charSequence)
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun clearSearch() {
        inputEditText.text.clear()
        clearIcon.visibility = View.GONE
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        tracks.clear()
    }


    private fun search(inputEditText: EditText) {
        trackApiService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    Log.d("API Response", "Response code: ${response.code()}")
                    Log.d("API Response", "Response body: ${response.body()}")


                    Log.d("API Response", "Results: ${response.body()?.results}")

                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            trackAdapter?.notifyDataSetChanged()
                            showMessage("")
                        } else {
                            showMessage(getString(R.string.err_srch))
                        }
                    } else {
                        showMessage(getString(R.string.error_cnn))


                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(getString(R.string.error_cnn))

                    Log.e("API Failure", "Error: ${t.message}")
                }
            })
    }
    fun showMessage(message: String) {
        if (message.isNotEmpty()) {

            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            trackAdapter?.notifyDataSetChanged()
            placeholderText.text = message

            if (message == getString(R.string.err_srch)) {
                placeholderImage.setImageResource(R.drawable.err_srch)
                recyclerView.visibility = View.GONE
                updateButton.visibility = View.GONE
            } else {
                placeholderImage.setImageResource(R.drawable.err_cnct)
                updateButton.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            placeholderMessage.visibility = View.GONE
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, inputEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedSearchText = savedInstanceState.getString(SEARCH_TEXT_KEY).toString()
        inputEditText.setText(savedSearchText)

        if (savedSearchText.isEmpty() && inputEditText.hasFocus() && historyTracks.isNotEmpty()) {
            showHistory()
        }
    }




}
