package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SaveQuestions @Inject constructor(private val repository: ReCallRepository) {

       suspend operator fun invoke(questions:List<Question>){
        return repository.saveQuestionsToRoom(questions)
    }
}