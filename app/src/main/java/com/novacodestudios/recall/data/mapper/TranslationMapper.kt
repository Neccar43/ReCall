package com.novacodestudios.recall.data.mapper

import com.novacodestudios.recall.data.remote.dto.TranslationResponseDto
import com.novacodestudios.recall.domain.model.Translation

fun TranslationResponseDto.toTranslation(word:String):Translation=
    Translation(
        word=word,
        translatedWord = data.translations.first().translatedText
    )