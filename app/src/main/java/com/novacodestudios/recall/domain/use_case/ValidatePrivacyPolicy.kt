package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class ValidatePrivacyPolicy @Inject constructor() {

    operator fun invoke(isChecked:Boolean):Resource<Boolean>{
        if (!isChecked){
            return Resource.Error(UIText.StringResource(R.string.privacy_policy_required_field))
        }
        return Resource.Success(true)
    }
}