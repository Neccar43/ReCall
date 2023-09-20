package com.novacodestudios.recall.presentation.word

import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.util.WordOrder


sealed class WordEvent{
    data class OnWordChanged(val word:String):WordEvent()
    data class OnMeaningChanged(val meaning:String):WordEvent()
    data class OnSearchChanged(val search:String):WordEvent()
    data class OnAddDialogVisibilityChange(val isVisible:Boolean):WordEvent()
    data class OnDeleteDialogVisibilityChange(val isVisible:Boolean,val deletedWord: Word?):WordEvent()
    data class OnUpdateDialogVisibilityChange(val isVisible:Boolean):WordEvent()
    data class OnSetUpdatedWord(val updatedWord: Word):WordEvent()
    data object SaveWord:WordEvent()
    data object DeleteWord:WordEvent()
    data object UpdateWord:WordEvent()
    data class OnOrderChanged(val wordOrder: WordOrder):WordEvent()
    data class OnSelectedGroupChanged(val group: Group?):WordEvent()
    data object OnSheetVisibilityChange:WordEvent()
    data class OnWordSelected(val word: Word):WordEvent()
    data class OnWordUnSelected(val word: Word):WordEvent()
    data object OnAllWordsSelected:WordEvent()
    data object OnAllWordsUnSelected:WordEvent()
    data object OnLongClickedChange:WordEvent()
    data object OnGroupDialogVisibilityChanged:WordEvent()
    data object OnGroupAdded:WordEvent()
    data class OnGroupNameChanged(val groupName:String):WordEvent()
    data object OnBulkDelete:WordEvent()
    data object OnBulkDeleteDialogVisibilityChanged:WordEvent()
    data object OnWordsMove:WordEvent()
    data object OnWordsMoveDialogVisibilityChanged:WordEvent()
    data class OnGroupToMoveChange(val group: Group?):WordEvent()
    data object OnDeleteGroupDialogVisibilityChanged:WordEvent()
    data object OnUpdatedGroupDialogVisibilityChanged:WordEvent()
    data object OnGroupDeleted:WordEvent()
    data object OnGroupUpdated : WordEvent()
    data object OnDeleteAllWordsCheckChanged:WordEvent()
    data class OnNewGroupNameChanged(val newGroupName:String):WordEvent()
    data class OnEditedGroupChanged(val group: Group):WordEvent()



}
