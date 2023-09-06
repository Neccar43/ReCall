package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateMeaning @Inject constructor() {

    operator fun invoke(meaning:String):Resource<Boolean>{
        if (meaning.isBlank()){
            return Resource.Error("The meaning can't be blank")
        }
        return Resource.Success(true)
    }
}