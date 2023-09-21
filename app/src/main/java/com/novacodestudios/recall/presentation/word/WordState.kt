package com.novacodestudios.recall.presentation.word

import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.util.OrderType
import com.novacodestudios.recall.domain.util.WordOrder

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
    val isLoading:Boolean=false,
    val selectedGroup: Group?=null,
    val wordOrder:WordOrder=WordOrder.CreationDate(OrderType.Descending), // TODO: Burayı sonradan data store bağla
    val selectedWords:List<Word> = emptyList(),
    val groups:List<Group> = emptyList(),
    val isSheetOpen:Boolean=false,
    val isLongClicked:Boolean=false,
    val isGroupDialogVisible:Boolean=false,
    val groupName:String="",
    val groupNameError: String?=null,
    val isBulkDeleteDialogVisible:Boolean=false,
    val isWordsMoveDialogVisible:Boolean=false,
    val groupToMove:Group?=null,
    val isDeleteGroupDialogVisible:Boolean=false,
    val isUpdateGroupDialogVisible:Boolean=false,
    val isDeleteAllWordsInGroupSelected:Boolean=false,
    val newGroupName:String="",
    val newGroupNameError:String?=null,
    val editedGroup:Group?=null,
    val isMeaningVisible:Boolean=true,
    )
