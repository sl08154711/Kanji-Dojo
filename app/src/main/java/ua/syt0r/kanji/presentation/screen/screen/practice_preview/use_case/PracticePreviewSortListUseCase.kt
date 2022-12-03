package ua.syt0r.kanji.presentation.screen.screen.practice_preview.use_case

import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PracticeGroupItem
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortOption
import javax.inject.Inject

class PracticePreviewSortListUseCase @Inject constructor() :
    PracticePreviewScreenContract.SortListUseCase {

    override fun sort(
        sortConfiguration: SortConfiguration,
        characterList: List<PracticeGroupItem>
    ): List<PracticeGroupItem> {

        val comparator = when (sortConfiguration.sortOption) {
            SortOption.ADD_ORDER -> {

                val reviewDateComparator: Comparator<PracticeGroupItem> = when (
                    sortConfiguration.isDescending
                ) {
                    true -> compareByDescending { it.positionInPractice }
                    false -> compareBy { it.positionInPractice }
                }

                reviewDateComparator

            }
            SortOption.NAME -> {

                val nameComparator: Comparator<PracticeGroupItem> = when (
                    sortConfiguration.isDescending
                ) {
                    true -> compareByDescending { it.character }
                    false -> compareBy { it.character }
                }

                nameComparator

            }
            SortOption.FREQUENCY -> {

                val frequencyComparator: Comparator<PracticeGroupItem> = when (
                    sortConfiguration.isDescending
                ) {
                    true -> compareByDescending { it.frequency }
                    false -> compareBy { it.frequency }
                }

                frequencyComparator.thenBy { it.character }

            }
        }

        return characterList.sortedWith(comparator)
    }

}
