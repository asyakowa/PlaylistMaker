package com.example.playlistmaker.edit_playlist.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
 import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream



class EditPlaylistViewModel(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _initialPlaylist = MutableLiveData<PlaylistEntity?>()
    val initialPlaylist: LiveData<PlaylistEntity?> = _initialPlaylist


    private val _isSaveButtonEnabled = MutableStateFlow(false)
    val isSaveButtonEnabled: StateFlow<Boolean> get() = _isSaveButtonEnabled

    var playlistName: String = ""
        set(value) {
            field = value
            _isSaveButtonEnabled.value = value.isNotEmpty()
        }

    var playlistDescription: String = ""
    var coverPath: String? = null
    var hasUnsavedChanges: Boolean = false

    private var originalPlaylist: PlaylistEntity? = null

    fun loadPlaylist(id: Long) {
        viewModelScope.launch {
            val loaded = playlistRepository.getPlaylist(id)
            originalPlaylist = loaded

            playlistName = ""
            playlistDescription = ""
            coverPath = null
            hasUnsavedChanges = false
            _isSaveButtonEnabled.value = false

            loaded?.let {
                playlistName = it.name
                playlistDescription = it.description
                coverPath = it.coverPath
                hasUnsavedChanges = false
                _isSaveButtonEnabled.value = it.name.isNotEmpty()
                _initialPlaylist.postValue(it)
            }
        }
    }


    fun saveChanges(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (playlistName.isEmpty()) {
            onError("playlist_name_required")
            return
        }

        viewModelScope.launch {
            try {
                val prev = originalPlaylist
                if (prev == null) {
                    onError("playlist_not_loaded")
                    return@launch
                }

                val updated = prev.copy(
                    name = playlistName,
                    description = playlistDescription,
                    coverPath = coverPath ?: prev.coverPath
                )

                Log.d("tag1","Before update: original=$prev, updated=$updated")

                playlistRepository.updatePlaylist(updated)

                val fresh = playlistRepository.getPlaylist(updated.id)
                if (fresh != null) {
                    originalPlaylist = fresh
                    playlistName = fresh.name
                    playlistDescription = fresh.description
                    coverPath = fresh.coverPath
                    hasUnsavedChanges = false
                    _isSaveButtonEnabled.value = fresh.name.isNotEmpty()
                    _initialPlaylist.postValue(fresh)
                    Log.d("tag1", "After update from DB (fresh): $fresh")
                } else {
                    Log.d("tag1", "After update: fresh is NULL from DB for id=${updated.id}")
                }



                hasUnsavedChanges = false
                onSuccess()
            } catch (e: Exception) {
                Log.d("tag1", "saveChanges error", e)
                onError("playlist_save_error")
            }
        }
    }



    fun saveImageToPrivateStorage(uri: Uri, picturesDir: File, context: Context): String? {
        val dir = File(picturesDir, "playlist_covers").apply { if (!exists()) mkdirs() }
        val file = File(dir, "playlist_cover_${System.currentTimeMillis()}.jpg")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    BitmapFactory.decodeStream(input)?.compress(Bitmap.CompressFormat.JPEG, 90, output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    } }

