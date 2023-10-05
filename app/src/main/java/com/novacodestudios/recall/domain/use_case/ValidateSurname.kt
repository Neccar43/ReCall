package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateSurname @Inject constructor(){

    operator fun invoke(surname:String):Resource<Boolean>{
        if (surname.isBlank()){
            return Resource.Error(UIText.StringResource(R.string.surname_cannot_be_blank))
        }
        return Resource.Success(true)
    }
}