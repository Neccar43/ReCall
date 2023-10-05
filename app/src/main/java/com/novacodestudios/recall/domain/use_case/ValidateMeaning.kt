package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateMeaning @Inject constructor() {

    operator fun invoke(meaning:String):Resource<Boolean>{
        if (meaning.isBlank()){
            return Resource.Error(UIText.StringResource(R.string.meaning_cannot_blank))
        }
        return Resource.Success(true)
    }
}