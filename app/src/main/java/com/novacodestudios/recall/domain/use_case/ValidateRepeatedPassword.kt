package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateRepeatedPassword @Inject constructor(){

    operator fun invoke(password: String, repeatedPassword: String): Resource<Boolean> {
        if (repeatedPassword.isBlank()) {
            return Resource.Error("The repeated password can't be blank")
        }
        val isSame = password == repeatedPassword
        if (!isSame){
            return Resource.Error("Passwords do not match")
        }
        return Resource.Success(true)
    }
}