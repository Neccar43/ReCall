package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateName @Inject constructor() {

    operator fun invoke(name:String):Resource<Boolean>{
        if (name.isBlank()){
            return Resource.Error(UIText.StringResource(R.string.name_cannot_be_blank))
        }
        return Resource.Success(true)
    }
}