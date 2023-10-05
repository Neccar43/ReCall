package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SendResetPasswordEmail @Inject constructor(private val repository: ReCallRepository) {

    suspend operator fun invoke(
        email: String,
    ) {
        repository.sendResetEmail(email)
    }
}