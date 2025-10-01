package com.example.playlistmaker.create_new_playlist

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistnewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import create_new_playlist.view_model.NewPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


open class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistnewBinding? = null
    protected val binding get() = _binding!!

    protected open val viewModel by viewModel<NewPlaylistViewModel>()

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                binding.placeholderNewPlaylist.setImageURI(uri)
                val picturesDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                viewModel.coverPath = viewModel.saveImageToPrivateStorage(uri, picturesDir!!, requireContext())
                viewModel.hasUnsavedChanges = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistnewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextWatchers()
        setupClickListeners()
        setupBackPressHandler()
        setupButtonStateObserver()
    }

    protected open fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.hasUnsavedChanges = true
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.nameNewPlaylist.addTextChangedListener(textWatcher)
        binding.descriptionNewPlaylist.addTextChangedListener(textWatcher)

        binding.nameNewPlaylist.doAfterTextChanged { text ->
            viewModel.playlistName = text?.toString() ?: ""
        }

        binding.descriptionNewPlaylist.doAfterTextChanged { text ->
            viewModel.playlistDescription = text?.toString() ?: ""
        }
    }

    protected open fun setupClickListeners() {
        binding.coverNewPlaylistLayout.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            viewModel.hasUnsavedChanges = true
        }

        binding.backButtonPlayer.setOnClickListener {
            checkUnsavedChangesAndNavigate()
        }

        binding.createNewPlaylistButton.setOnClickListener {
            viewModel.playlistName = binding.nameNewPlaylist.text.toString()
            viewModel.playlistDescription = binding.descriptionNewPlaylist.text.toString()

            viewModel.savePlaylist(
                onSuccess = {
                    showToast(getString(R.string.playlist_created))
                    findNavController().navigateUp()
                },
                onError = { errorKey ->
                    when (errorKey) {
                        "playlist_name_required" -> binding.nameNewPlaylist.error =
                            getString(R.string.playlist_name_required)
                        else -> showToast(getString(R.string.playlist_save_error))
                    }
                }
            )
        }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            checkUnsavedChangesAndNavigate()
        }
    }

    private fun setupButtonStateObserver() {
        lifecycleScope.launch {
            viewModel.isCreateButtonEnabled.collect { isEnabled ->
                binding.createNewPlaylistButton.isEnabled = isEnabled
                binding.createNewPlaylistButton.backgroundTintList = ColorStateList.valueOf(
                    if (isEnabled) ContextCompat.getColor(requireContext(), R.color.switch_thumb_active_color)
                    else ContextCompat.getColor(requireContext(), R.color.text_color_hint)
                )
            }
        }
    }

    protected open fun checkUnsavedChangesAndNavigate() {
        if (viewModel.hasUnsavedChanges) {
            showExitDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    protected open fun showExitDialog() {
        showCustomDialog(
            title = getString(R.string.exit_dialog_title),
            message = getString(R.string.exit_dialog_message),
            positiveText = getString(R.string.exit),
            negativeText = getString(R.string.cancel)
        ) {
            findNavController().navigateUp()
        }
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun showCustomDialog(
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        onPositive: () -> Unit
    ) {
        val messageContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24.dpToPx(), 24.dpToPx(), 24.dpToPx(), 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val titleText = TextView(requireContext()).apply {
            text = title
            setTextColor(Color.BLACK)
            textSize = 18f
            typeface = ResourcesCompat.getFont(requireContext(), R.font.ys_display_regular)
            gravity = Gravity.START
            setPadding(0, 0, 0, 8.dpToPx())
        }

        val messageText = TextView(requireContext()).apply {
            text = message
            setTextColor(Color.BLACK)
            textSize = 16f
            typeface = ResourcesCompat.getFont(requireContext(), R.font.ys_display_regular)
            gravity = Gravity.START
        }

        messageContainer.addView(titleText)
        messageContainer.addView(messageText)

        val space = Space(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                16.dpToPx()
            )
        }
        messageContainer.addView(space)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(messageContainer)
            .setNegativeButton(negativeText, null)
            .setPositiveButton(positiveText, null)
            .create()

        dialog.show()

        val widthPx = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(widthPx, WindowManager.LayoutParams.WRAP_CONTENT)

        val background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            cornerRadius = 16.dpToPx().toFloat()
        }
        dialog.window?.setBackgroundDrawable(background)

        val buttonColor = ContextCompat.getColor(requireContext(), R.color.blue)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(buttonColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(buttonColor)

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener { dialog.dismiss() }
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            dialog.dismiss()
            onPositive()
        }

        dialog.window?.decorView?.setPadding(0, 0, 0, 0)
        dialog.window?.setGravity(Gravity.CENTER)
    }

    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}
