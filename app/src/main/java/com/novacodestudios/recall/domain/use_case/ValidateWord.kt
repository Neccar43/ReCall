package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateWord @Inject constructor(){

    operator fun invoke(word:String):Resource<Boolean>{
        if (word.isBlank()){
            return Resource.Error(UIText.StringResource(R.string.word_cannot_be_blank))
        }
        return Resource.Success(true)
    }
}