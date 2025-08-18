package create_new_playlist.models

sealed class CreationState {
    data class Success(
        val playlistId: Long,
        val message: String
    ) : CreationState()
    data class Error(val message: String) : CreationState()
}