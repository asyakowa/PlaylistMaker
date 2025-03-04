//package com.example.playlistmaker
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

//class SearchActivityyy : AppCompatActivity() {
//    private val retrofit = Builder()
//        .baseUrl("https://itunes.apple.com")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//    private val trackApiService = retrofit.create(TrackApiService::class.java)
//    private val tracks = ArrayList<Track>()
//    private val  trackAdapter = TrackAdapter(tracks)
//
//    private lateinit var inputEditText: EditText
//    private lateinit var clearIcon: ImageView
//    private lateinit var recyclerView: RecyclerView
//    //    private lateinit var errorCE: Button
//    private lateinit var placeholderMessage: LinearLayout
//    private lateinit var placeholderImage: ImageView
//    private lateinit var placeholderText: TextView
//    private lateinit var updateButton: Button
//    //    private lateinit var trackAdapter: TrackAdapter
//    private var savedSearchText: String? = null
//
//
//    companion object {
//        private const val SEARCH_TEXT_KEY = "search_text"
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)
//
//
//        updateButton = findViewById(R.id.updateButton)
//
//        updateButton.visibility = View.GONE
//        inputEditText = findViewById(R.id.inputEditText)
//        clearIcon = findViewById(R.id.clearIcon)
//        recyclerView = findViewById(R.id.recyclerView)
//        placeholderMessage = findViewById(R.id.placeholderMessage)
//        placeholderText = findViewById(R.id.placeholderText) // Инициализация текста заглушки
//        placeholderImage = findViewById(R.id.placeholderImage) // Инициализация изображения заглушки
//        val backButton = findViewById<ImageView>(R.id.backbutton)
//        backButton.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            finish()
//        }
//
//        trackAdapter.tracks = tracks
//        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        recyclerView.adapter = trackAdapter
//
//        savedInstanceState?.let {
//            savedSearchText = it.getString(SEARCH_TEXT_KEY)
//            inputEditText.setText(savedSearchText)
//        }
//
//        setupTextWatcher()
//        clearIcon.setOnClickListener {
//            inputEditText.setText("")
//            clearSearch()
//
//        }
//        updateButton.setOnClickListener {
//            search(inputEditText)
//        }
//
//        inputEditText.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                inputEditText.requestFocus()
//                inputEditText.post {
//                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
//                }
//            }
//        }
//
//
//
////        // Инициализация адаптера
////        trackAdapter = TrackAdapter(trackList)
////        recyclerView.adapter = trackAdapter
//    }
//
//    private fun setupTextWatcher() {
//        inputEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                search(inputEditText)
//                true
//            }
//            false
//        }
//
//        inputEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
//                clearIcon.visibility = if (charSequence.isNullOrEmpty()) View.GONE else View.VISIBLE
//                savedSearchText = charSequence.toString()
//
//            }
//            override fun afterTextChanged(editable: Editable?) {}
//        })
//    }
//
//    private fun clearSearch() {
//        inputEditText.text.clear()
//        clearIcon.visibility = View.GONE
//        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
//        tracks.clear()
//    }
//
//
//    private fun search(inputEditText: EditText) {
//        trackApiService.search(inputEditText.text.toString())
//            .enqueue(object : Callback<TrackResponse> {
//                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
//                    Log.d("API Response", "Response code: ${response.code()}")
//                    Log.d("API Response", "Response body: ${response.body()}")
//
//
//                    Log.d("API Response", "Results: ${response.body()?.results}")
//
//                    if (response.code() == 200) {
//                        tracks.clear()
//                        if (response.body()?.results?.isNotEmpty() == true) {
//                            tracks.addAll(response.body()?.results!!)
//                            trackAdapter.notifyDataSetChanged()
//                            showMessage("")
//                        } else {
//                            showMessage(getString(R.string.err_srch))
//                        }
//                    } else {
//                        showMessage(getString(R.string.error_cnn))
//
//
//                    }
//                }
//
//                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
//                    showMessage(getString(R.string.error_cnn))
//
//                    Log.e("API Failure", "Error: ${t.message}")
//                }
//            })
//    }
//    fun showMessage(message: String) {
//        if (message.isNotEmpty()) {
//
//            placeholderMessage.visibility = View.VISIBLE
//            tracks.clear()
//            trackAdapter.notifyDataSetChanged()
//            placeholderText.text = message
//
//            if (message == getString(R.string.err_srch)) {
//                placeholderImage.setImageResource(R.drawable.searchlm)
//                recyclerView.visibility = View.GONE
//                updateButton.visibility = View.GONE
//            } else {
//                placeholderImage.setImageResource(R.drawable.connectiondm)
//                updateButton.visibility = View.VISIBLE
//                recyclerView.visibility = View.GONE
//            }
//        } else {
//
//            placeholderMessage.visibility = View.GONE
//        }
//    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(SEARCH_TEXT_KEY, inputEditText.text.toString())
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        savedSearchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
//        inputEditText.setText(savedSearchText)
//    }
//
//
//
//
//}
