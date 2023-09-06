package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidatePassword @Inject constructor(){

    operator fun invoke(password:String):Resource<Boolean>{
        if (password.isBlank()){
            return Resource.Error("The password can't be blank")
        }
        return Resource.Success(true)
    }
}