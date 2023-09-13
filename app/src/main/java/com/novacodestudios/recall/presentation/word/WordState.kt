package com.novacodestudios.recall.presentation.word

import com.novacodestudios.recall.domain.model.Word

data class WordState(
    val word:String="",
    val wordError:String?=null,
    val meaning:String="",
    val meaningError: String?=null,
    val wordList: List<Word> = emptyList(),
    val search:String="",
    val isAddDialogVisible:Boolean=false,
    val isDeleteDialogVisible:Boolean=false,
    val isUpdateDialogVisible:Boolean=false,
    val deletedWord:Word?=null,
    val updatedWord:Word?=null,
    val isLoading:Boolean=false


    )
