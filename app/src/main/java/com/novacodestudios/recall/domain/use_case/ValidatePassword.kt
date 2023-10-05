package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidatePassword @Inject constructor(){

    operator fun invoke(password:String):Resource<Boolean>{
        if (password.isBlank()){
            return Resource.Error(UIText.StringResource(R.string.password_cannot_be_blank))
        }
        return Resource.Success(true)
    }
}