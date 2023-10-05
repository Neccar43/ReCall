package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateRepeatedPassword @Inject constructor(){

    operator fun invoke(password: String, repeatedPassword: String): Resource<Boolean> {
        if (repeatedPassword.isBlank()) {
            return Resource.Error(UIText.StringResource(R.string.repeated_password_cannot_be_blank))
        }
        val isSame = password == repeatedPassword
        if (!isSame){
            return Resource.Error(UIText.StringResource(R.string.passwords_do_not_match))
        }
        return Resource.Success(true)
    }
}