package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateEmail @Inject constructor() {

    operator fun invoke(email:String):Resource<Boolean>{
        if (email.isBlank()){
            return Resource.Error("The email can't be blank")
        }
        return Resource.Success(true)
    }
}