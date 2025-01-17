package ua.syt0r.kanji.core.app_data

import kotlinx.coroutines.Deferred
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ua.syt0r.kanji.core.app_data.data.CharacterRadical
import ua.syt0r.kanji.core.app_data.data.FuriganaDBEntity
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import ua.syt0r.kanji.core.app_data.data.FuriganaStringCompound
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.KanjiData
import ua.syt0r.kanji.core.app_data.data.RadicalData
import ua.syt0r.kanji.core.app_data.data.ReadingType
import ua.syt0r.kanji.core.app_data.db.AppDataDatabase
import ua.syt0r.kanji.core.appdata.db.AppDataQueries

class SqlDelightAppDataRepository(
    private val deferredDatabase: Deferred<AppDataDatabase>
) : AppDataRepository {

    private suspend fun <T> runTransaction(
        transactionScope: AppDataQueries.() -> T
    ): T {
        val queries = deferredDatabase.await().appDataQueries
        return queries.transactionWithResult { queries.transactionScope() }
    }

    override suspend fun getStrokes(character: String): List<String> = runTransaction {
        getStrokes(character).executeAsList()
    }

    override suspend fun getRadicalsInCharacter(
        character: String
    ): List<CharacterRadical> = runTransaction {
        getCharacterRadicals(character)
            .executeAsList()
            .map {
                it.run {
                    CharacterRadical(
                        character = character,
                        radical = radical,
                        startPosition = start_stroke.toInt(),
                        strokesCount = strokes_count.toInt()
                    )
                }
            }
    }

    override suspend fun getMeanings(kanji: String): List<String> = runTransaction {
        getKanjiMeanings(kanji).executeAsList()
    }

    override suspend fun getReadings(
        kanji: String
    ): Map<String, ReadingType> = runTransaction {
        getKanjiReadings(kanji)
            .executeAsList()
            .associate { readingData ->
                readingData.reading to ReadingType.values()
                    .find { it.value == readingData.reading_type }!!
            }
    }

    override suspend fun getData(kanji: String): KanjiData? = runTransaction {
        getKanjiData(kanji)
            .executeAsOneOrNull()
            ?.run { KanjiData(kanji = kanji, frequency = frequency?.toInt()) }
    }


    override suspend fun getWordsWithTextCount(text: String): Int = runTransaction {
        getCountOfExpressionsWithText(text).executeAsOne().toInt()
    }

    override suspend fun getWordsWithText(
        text: String,
        offset: Int,
        limit: Int
    ): List<JapaneseWord> = runTransaction {
        getRankedExpressionsWithText(text, offset.toLong(), limit.toLong())
            .executeAsList()
            .map { expressionId ->
                val readings = getExpressionReadings(expressionId)
                    .executeAsList()
                    .map {
                        RankedReading(
                            it.expression,
                            it.kana_expression,
                            it.rank.toInt(),
                            it.furigana
                        )
                    }
                    .sortedWith(readingComparator(text))
                    .map { entity -> entity.toReading() }

                val meanings = getExpressionMeanings(expressionId)
                    .executeAsList()
                    .map { it.meaning }

                JapaneseWord(readings, meanings)
            }
    }

    override suspend fun getKanaWords(
        char: String,
        limit: Int
    ): List<JapaneseWord> = runTransaction {
        getKanaExpressionsReadings("%$char%", limit.toLong())
            .executeAsList()
            .groupBy { it.expression_id }
            .map { (id, readingEntities) ->
                val readings = readingEntities
                    .map {
                        RankedReading(
                            it.expression,
                            it.kana_expression,
                            it.rank.toInt(),
                            it.furigana
                        )
                    }
                    .sortedWith(readingComparator(char, true))
                    .map { it.toReading(kanaOnly = true) }
                    .distinct()

                val meanings = getExpressionMeanings(id)
                    .executeAsList()
                    .map { it.meaning }

                JapaneseWord(readings, meanings)
            }
    }

    override suspend fun getRadicals(): List<RadicalData> = runTransaction {
        getRadicals().executeAsList()
            .map { RadicalData(it.radical, it.strokesCount.toInt()) }
    }

    override suspend fun getCharactersWithRadicals(
        radicals: List<String>
    ): List<String> = runTransaction {
        getCharsWithRadicals(radicals, radicals.size.toLong())
            .executeAsList()
    }

    override suspend fun getAllRadicalsInCharactersWithSelectedRadicals(
        radicals: Set<String>
    ): List<String> = runTransaction {
        getAllRadicalsInCharactersWithSelectedRadicals(radicals, radicals.size.toLong())
            .executeAsList()
    }

    private data class RankedReading(
        val kanjiExpression: String?,
        val kanaExpression: String,
        val rank: Int,
        val furigana: String?
    )

    // To make sure that searched reading is the first in the list
    private fun readingComparator(
        prioritizedText: String,
        kanaOnly: Boolean = false
    ): Comparator<RankedReading> {
        return compareBy(
            {
                val containsText = it.kanjiExpression
                    ?.takeIf { !kanaOnly }
                    ?.contains(prioritizedText)
                    ?: it.kanaExpression.contains(prioritizedText)
                !containsText
            },
            { it.rank }
        )
    }

    private fun RankedReading.toReading(
        kanaOnly: Boolean = false
    ): FuriganaString {
        val compounds = furigana
            ?.takeIf { !kanaOnly }
            ?.let { Json.decodeFromString<List<FuriganaDBEntity>>(it) }
            ?.takeIf { it.isNotEmpty() }
            ?.map { FuriganaStringCompound(it.text, it.annotation) }
            ?: listOf(FuriganaStringCompound(kanaExpression))
        return FuriganaString(compounds)
    }

}
