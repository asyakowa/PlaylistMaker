package com.example.playlistmaker.edit_playlist.fragment

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistnewBinding
import com.example.playlistmaker.edit_playlist.view_model.EditPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class EditPlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistnewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditPlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val picturesDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            viewModel.coverPath = viewModel.saveImageToPrivateStorage(it, picturesDir!!, requireContext())
            binding.placeholderNewPlaylist.setImageURI(Uri.fromFile(File(viewModel.coverPath!!)))
            viewModel.hasUnsavedChanges = true
        }
    }

    private var playlistId: Long = 0L

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

        playlistId = arguments?.getLong("playlistId") ?: 0L
        viewModel.loadPlaylist(playlistId)

        setupUI()
        setupTextWatchers()
        setupClickListeners()
        setupBackPressHandler()
        setupButtonObserver()
    }

    private fun setupUI() {
        binding.createNewPlaylistButton.text = getString(R.string.save)
        binding.textView2.text = getString(R.string.edit)

        viewModel.initialPlaylist.observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                binding.nameNewPlaylist.setText(playlist.name)
                binding.descriptionNewPlaylist.setText(playlist.description)
                playlist.coverPath?.let {
                    binding.placeholderNewPlaylist.setImageURI(Uri.fromFile(File(it)))
                }
            }
        }

    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.hasUnsavedChanges = true
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.nameNewPlaylist.addTextChangedListener(watcher)
        binding.descriptionNewPlaylist.addTextChangedListener(watcher)

        binding.nameNewPlaylist.doAfterTextChanged { text ->
            viewModel.playlistName = text?.toString() ?: ""
        }

        binding.descriptionNewPlaylist.doAfterTextChanged { text ->
            viewModel.playlistDescription = text?.toString() ?: ""
        }
    }

    private fun setupClickListeners() {
        binding.coverNewPlaylistLayout.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButtonPlayer.setOnClickListener { navigateBack() }

        binding.createNewPlaylistButton.setOnClickListener {
            viewModel.saveChanges(
                onSuccess = {
                    Toast.makeText(requireContext(), getString(R.string.save), Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                },
                onError = { key ->
                    when (key) {
                        "playlist_name_required" -> binding.nameNewPlaylist.error = getString(R.string.playlist_name_required)
                        else -> Toast.makeText(requireContext(), getString(R.string.playlist_save_error), Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateBack()
        }
    }

    private fun navigateBack() {
        if (viewModel.hasUnsavedChanges) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.exit_dialog_title))
                .setMessage(getString(R.string.exit_dialog_message))
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.exit)) { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }.show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun setupButtonObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.isSaveButtonEnabled.collect { enabled ->
                binding.createNewPlaylistButton.isEnabled = enabled
                binding.createNewPlaylistButton.backgroundTintList =
                    ColorStateList.valueOf(
                        if (enabled) ContextCompat.getColor(requireContext(), R.color.switch_thumb_active_color)
                        else ContextCompat.getColor(requireContext(), R.color.text_color_hint)
                    )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(playlistId: Long) = EditPlaylistFragment().apply {
            arguments = Bundle().apply { putLong("playlistId", playlistId) }
        }
    }
}
