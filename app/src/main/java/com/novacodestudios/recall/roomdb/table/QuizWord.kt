package com.novacodestudios.recall.roomdb.table

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class QuizWord(
    //PK yı Word tablosundan alacağız
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,
    val question: String,
    val answer: String,
    val isCorrect: Boolean? = null,
    val quizId:Int
)

/*fun Word.convertWordToQuizWord(): QuizWord {
    return QuizWord(id = this.id, question = this.originalName, answer = this.translations)
}

val wordList = listOf(
    Word(1, "orange", "portakal"),
    Word(2, "paper", "kağıt"),
    Word(3, "table", "masa")
)*/
