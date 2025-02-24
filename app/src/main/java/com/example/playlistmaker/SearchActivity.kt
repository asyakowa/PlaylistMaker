package com.example.playlistmaker
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private var savedSearchText: String? = null

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageView>(R.id.backbutton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finish()
        }

        inputEditText = findViewById(R.id.inputEditText)
        clearIcon = findViewById(R.id.clearIcon)
        recyclerView = findViewById(R.id.recyclerView) // Инициализация recyclerView

        setupTextWatcher()
        clearIcon.setOnClickListener {
            clearSearch()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputEditText.requestFocus()
                inputEditText.post {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }

        savedInstanceState?.let {
            savedSearchText = it.getString(SEARCH_TEXT_KEY)
            inputEditText.setText(savedSearchText)
        }

        // Инициализация адаптера
        trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter
    }

    private fun setupTextWatcher() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (charSequence.isNullOrEmpty()) View.GONE else View.VISIBLE
                savedSearchText = charSequence.toString()
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun clearSearch() {
        inputEditText.text.clear()
        clearIcon.visibility = View.GONE
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, inputEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedSearchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
        inputEditText.setText(savedSearchText)
    }




    }
