package kov_p.pixelplayer.feature_albums.detail

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kov_p.pixelplayer.domain_albums.AlbumVo

sealed interface AlbumDetailState {
    data object Loading : AlbumDetailState
    data class Data(
        val title: String,
        val artist: String,
        val cover: String,
        val year: String,
        val disks: ImmutableList<Disk>,
    ) : AlbumDetailState

    @Immutable
    data class Disk(
        val diskNumber: Int,
        val tracks: ImmutableList<TrackVs>,
    )

    @Immutable
    data class TrackVs(
        val id: String,
        val title: String,
        val artist: String,
        val position: Int,
        val globalPosition: Int,
        val duration: String,
        val quality: AlbumVo.Quality,
    )
}
