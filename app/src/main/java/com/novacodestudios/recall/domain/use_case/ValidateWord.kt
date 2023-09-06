package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateWord @Inject constructor(){

    operator fun invoke(word:String):Resource<Boolean>{
        if (word.isBlank()){
            return Resource.Error("The word can't be blank")
        }
        return Resource.Success(true)
    }
}