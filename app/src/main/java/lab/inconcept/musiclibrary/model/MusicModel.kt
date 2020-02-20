package lab.inconcept.musiclibrary.model

data class MusicModel(
        var key: String? = null,
        var name: String? = null,
        var author: String? = null,
        var album: String? = null,
        var imageUrl: String? = null) {

    override fun equals(other: Any?): Boolean {
        other as MusicModel
        return key == other.key
    }

    override fun hashCode(): Int {
        return 1
    }
}
