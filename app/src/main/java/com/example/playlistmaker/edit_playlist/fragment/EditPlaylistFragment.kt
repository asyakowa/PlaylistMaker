package com.example.playlistmaker.edit_playlist.fragment


import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.create_new_playlist.NewPlaylistFragment
import com.example.playlistmaker.edit_playlist.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : NewPlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    private var playlistId: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playlistId = arguments?.getLong("playlistId") ?: 0L
        (viewModel as EditPlaylistViewModel).loadPlaylist(playlistId)

        super.onViewCreated(view, savedInstanceState)

        binding.createNewPlaylistButton.text = getString(R.string.save)
        binding.textView2.text = getString(R.string.edit)

        setupUI()
    }

    private fun setupUI() {
        (viewModel as EditPlaylistViewModel).initialPlaylist.observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                binding.nameNewPlaylist.setText(playlist.name)
                binding.descriptionNewPlaylist.setText(playlist.description ?: "")
                playlist.coverPath?.let {
                    binding.placeholderNewPlaylist.setImageURI(Uri.fromFile(File(it)))
                }

                viewModel.playlistName = playlist.name
                viewModel.playlistDescription = playlist.description ?: ""
                viewModel.coverPath = playlist.coverPath
                viewModel.hasUnsavedChanges = false
            }
        }
    }

    override fun setupClickListeners() {
        binding.coverNewPlaylistLayout.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            viewModel.hasUnsavedChanges = true
        }

        binding.backButtonPlayer.setOnClickListener {
            checkUnsavedChangesAndNavigate()
        }

        binding.createNewPlaylistButton.setOnClickListener {
            (viewModel as EditPlaylistViewModel).saveChanges(
                onSuccess = {
                    showToast(getString(R.string.changed_playlist))
                    findNavController().navigateUp()
                },
                onError = { key ->
                    when (key) {
                        "playlist_name_required" -> binding.nameNewPlaylist.error =
                            getString(R.string.playlist_name_required)
                        else -> showToast(getString(R.string.playlist_save_error))
                    }
                }
            )
        }
    }

    override fun checkUnsavedChangesAndNavigate() {
        if (viewModel.hasUnsavedChanges) {
            showCustomDialog(
                title = getString(R.string.exit_dialog_title2),
                message = getString(R.string.exit_dialog_message),
                positiveText = getString(R.string.exit),
                negativeText = getString(R.string.cancel)
            ) {
                findNavController().navigateUp()
            }
        } else {
            findNavController().navigateUp()
        }
    }
}
