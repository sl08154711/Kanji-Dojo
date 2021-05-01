package ua.syt0r.kanji_db_model.db

object KanjiReadingTable {

    const val TABLE_NAME = "readings"

    const val KANJI_COLUMN = "kanji"

    const val READING_TYPE_COLUMN = "reading_type"

    enum class ReadingType(val value: String) {
        ON("on"),
        KUN("kun")
    }

    const val READING_COLUMN = "reading"

}