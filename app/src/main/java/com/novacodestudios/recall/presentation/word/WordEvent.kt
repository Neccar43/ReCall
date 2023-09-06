package com.novacodestudios.recall.presentation.word

import com.novacodestudios.recall.domain.model.Word

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
}
