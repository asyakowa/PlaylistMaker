package com.example.playlistmaker.edit_playlist.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
 import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import create_new_playlist.view_model.NewPlaylistViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class EditPlaylistViewModel(
    override val playlistRepository: PlaylistRepository
) : NewPlaylistViewModel(playlistRepository) {

    private val _initialPlaylist = MutableLiveData<PlaylistEntity?>()
    val initialPlaylist: LiveData<PlaylistEntity?> = _initialPlaylist

    private val _isSaveButtonEnabled = MutableStateFlow(false)
    val isSaveButtonEnabled: StateFlow<Boolean> get() = _isSaveButtonEnabled

    private var originalPlaylist: PlaylistEntity? = null

    fun loadPlaylist(id: Long) {
        viewModelScope.launch {
            val loaded = playlistRepository.getPlaylist(id)
            originalPlaylist = loaded

            playlistName = loaded?.name ?: ""
            playlistDescription = loaded?.description ?: ""
            coverPath = loaded?.coverPath
            hasUnsavedChanges = false
            _isSaveButtonEnabled.value = playlistName.isNotEmpty()
            _initialPlaylist.postValue(loaded)
        }
    }

    override fun saveChanges(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (playlistName.isEmpty()) {
            onError("playlist_name_required")
            return
        }

        viewModelScope.launch {
            val prev = originalPlaylist
            if (prev == null) {
                onError("playlist_not_loaded")
                return@launch
            }

            try {
                val updated = prev.copy(
                    name = playlistName,
                    description = playlistDescription,
                    coverPath = coverPath ?: prev.coverPath
                )

                playlistRepository.updatePlaylist(updated)

                val fresh = playlistRepository.getPlaylist(updated.id)
                fresh?.let {
                    originalPlaylist = it
                    playlistName = it.name
                    playlistDescription = it.description
                    coverPath = it.coverPath
                    hasUnsavedChanges = false
                    _isSaveButtonEnabled.value = it.name.isNotEmpty()
                    _initialPlaylist.postValue(it)
                }

                hasUnsavedChanges = false
                onSuccess()
            } catch (e: Exception) {
                onError("playlist_save_error")
            }
        }
    }
}
