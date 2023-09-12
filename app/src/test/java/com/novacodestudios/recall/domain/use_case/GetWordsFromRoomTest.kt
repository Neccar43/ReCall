package com.novacodestudios.recall.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.novacodestudios.recall.data.repository.FakeReCallRepository
import com.novacodestudios.recall.domain.model.Word
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetWordsFromRoomTest{

    private lateinit var getWordsFromRoom: GetWordsFromRoom
    private lateinit var fakeReCallRepository: FakeReCallRepository

    @Before
    fun setUp(){
        fakeReCallRepository= FakeReCallRepository()
        getWordsFromRoom= GetWordsFromRoom(fakeReCallRepository)
    }

    @Test
    fun `Get words from room`(): Unit = runBlocking {
        val word1=Word(id = 1, name = "name1", meaning = "meaning1")
        val word2=Word(id = 2, name = "name2", meaning = "meaning2")

        fakeReCallRepository.saveWordToRoom(word1)
        fakeReCallRepository.saveWordToRoom(word2)
        val words = getWordsFromRoom().first()

        //google truth library
        assertThat(words).containsExactly(word1,word2)
    }
}