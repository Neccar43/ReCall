package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidateSurname @Inject constructor(){

    operator fun invoke(surname:String):Resource<Boolean>{
        if (surname.isBlank()){
            return Resource.Error("The surname can't be blank")
        }
        return Resource.Success(true)
    }
}