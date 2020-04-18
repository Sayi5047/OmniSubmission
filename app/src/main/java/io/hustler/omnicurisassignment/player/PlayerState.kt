package io.hustler.omnicurisassignment.player

data class PlayerState(
    var window: Int = 0,
    var position: Long = 0,
    var whenready: Boolean = true,
    var source: SourceType = SourceType.localAudio
)
