package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateEmail @Inject constructor() {

    operator fun invoke(email:String):Resource<Boolean>{
        if (email.isBlank()){
            return Resource.Error(UIText.StringResource(R.string.email_cannot_blank))
        }
        return Resource.Success(true)
    }
}