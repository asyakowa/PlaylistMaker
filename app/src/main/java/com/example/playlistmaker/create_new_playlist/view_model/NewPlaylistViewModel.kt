package create_new_playlist.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistViewModel(
    protected open val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _isCreateButtonEnabled = MutableStateFlow(false)
    val isCreateButtonEnabled: StateFlow<Boolean> = _isCreateButtonEnabled

    var playlistName: String = ""
        set(value) {
            field = value
            _isCreateButtonEnabled.value = value.isNotEmpty()
        }

    var playlistDescription: String = ""
    var coverPath: String? = null
    var hasUnsavedChanges: Boolean = false

    open fun savePlaylist(
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        if (playlistName.isEmpty()) {
            onError("playlist_name_required")
            return
        }

        viewModelScope.launch {
            try {
                val playlistId = playlistRepository.createPlaylist(
                    PlaylistEntity(
                        id = 0L,
                        name = playlistName,
                        description = playlistDescription,
                        coverPath = coverPath
                    )
                )
                onSuccess(playlistId)
            } catch (e: Exception) {
                e.printStackTrace()
                onError("playlist_save_error")
            }
        }
    }

    open fun saveChanges(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        savePlaylist(
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    open fun saveImageToPrivateStorage(uri: Uri, picturesDir: File, context: Context): String? {
        val dir = File(picturesDir, "playlist_covers")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "playlist_cover_${System.currentTimeMillis()}.jpg")
        return try {
            context.contentResolver.openInputStream(uri).use { input ->
                FileOutputStream(file).use { output ->
                    BitmapFactory.decodeStream(input)?.compress(Bitmap.CompressFormat.JPEG, 90, output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
