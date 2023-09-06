package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateName @Inject constructor() {

    operator fun invoke(name:String):Resource<Boolean>{
        if (name.isBlank()){
            return Resource.Error("The name can't be blank")
        }
        return Resource.Success(true)
    }
}