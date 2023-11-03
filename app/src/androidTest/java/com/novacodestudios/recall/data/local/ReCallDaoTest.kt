package com.novacodestudios.recall.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.model.copyAll
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
@SmallTest
class ReCallDaoTest {
    private lateinit var database: ReCallDatabase
    private lateinit var dao: ReCallDao
    private lateinit var words: MutableList<Word>
    private lateinit var quizzes: MutableList<Quiz>
    private lateinit var questions: MutableList<Question>

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ReCallDatabase::class.java
        ).allowMainThreadQueries().build() //Sorguların main thread de çalışmasını sağlıyoruz
        dao = database.reCallDao()

        words = mutableListOf(
            Word(id = 1, name = "name1", meaning = "meaning1"),
            Word(id = 2, name = "name2", meaning = "meaning2")
        )

        questions = mutableListOf(
            Question(id = 1, title = "title1", correctAnswer = "answer1", quizId = 1, wordId = 1),
            Question(id = 2, title = "title2", correctAnswer = "answer2", quizId = 1, wordId = 2)
        )

        quizzes =
            mutableListOf(Quiz(id = 1, isCompleted = false), Quiz(id = 2, isCompleted = false))
    }


    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertWordToRoom() = runTest {
        val word1 = Word(id = 1, name = "name1", meaning = "meaning1")
        dao.insertWordToRoom(word1)
        val allWords = dao.getAllWordsFromRoom().first()
        assertThat(allWords).contains(word1)
    }

    @Test
    fun insertQuizToRoom() = runTest {
        val quiz = Quiz(id = 1)
        dao.insertQuizToRoom(quiz)
        val allQuizzes = dao.getAllQuizzesFromRoom().first()
        assertThat(allQuizzes).contains(quiz)
    }

    @Test
    fun insertQuestions() = runTest {
        val questions = listOf(
            Question(id = 1, title = "title1", correctAnswer = "answer1", quizId = 1, wordId = 1),
            //Question(id = 2, title = "title2", correctAnswer = "answer2", quizId = 1, wordId = 2)
        )
        dao.insertQuestions(*questions.toTypedArray())
        val allQuestions = dao.getQuestionsByQuizId(1).first()

        assertThat(allQuestions).contains(questions.first())
    }

    @Test
    fun updateWordsInRoom() = runTest {

        words.forEach { dao.insertWordToRoom(it) }
        val actualUpdatedWords = words.copyAll(isInQuiz = true)
        dao.updateWordsInRoom(*actualUpdatedWords.toTypedArray())

        val expectedWords = dao.getAllWordsFromRoom().first()
        assertThat(actualUpdatedWords).containsExactlyElementsIn(expectedWords)
    }

    @Test
    fun updateWord() = runTest {
        val word1 = Word(id = 1, name = "name1", meaning = "meaning1")
        dao.insertWordToRoom(word1)
        val updatedWord = word1.copy(name = "updatedName")
        dao.updateWord(updatedWord)
        assertThat(updatedWord).isEqualTo(dao.getWordById(1))
    }

    @Test
    fun updateQuestionInRoom() = runTest {
        val question =
            Question(id = 1, title = "title1", correctAnswer = "answer1", quizId = 1, wordId = 1)
        dao.insertQuestions(question)
        val updatedQuestion = question.copy(correctAnswer = "newAnswer")
        dao.updateQuestionInRoom(updatedQuestion)
        assertThat(updatedQuestion).isEqualTo(dao.getQuestionsByQuizId(1).first().first())
    }

    @Test
    fun updateQuizzes() = runTest {
        val quiz = Quiz(id = 1, isCompleted = false)
        dao.insertQuizToRoom(quiz)

        val updatedQuiz = quiz.copy(isCompleted = true)
        dao.updateQuizzes(updatedQuiz)
        assertThat(updatedQuiz).isEqualTo(dao.getAllQuizzesFromRoom().first().first())
    }

    @Test
    fun updateQuiz() = runTest {
        val quiz = Quiz(id = 1, isCompleted = false)
        dao.insertQuizToRoom(quiz)

        val updatedQuiz = quiz.copy(isCompleted = true)
        dao.updateQuiz(updatedQuiz)
        assertThat(updatedQuiz).isEqualTo(dao.getAllQuizzesFromRoom().first().first())
    }

    @Test
    fun deleteWordFromRoom() = runTest {
        val word = Word(id = 1, name = "name1", meaning = "meaning1")
        dao.insertWordToRoom(word)
        dao.deleteWordFromRoom(word)
        val allWords = dao.getAllWordsFromRoom().first()
        assertThat(allWords).isEmpty()
    }

    @Test
    fun deleteAllWords() = runTest {
        words.forEach { dao.insertWordToRoom(it) }
        dao.deleteAllWords()
        val allWords = dao.getAllWordsFromRoom().first()
        assertThat(allWords).isEmpty()
    }

    @Test
    fun deleteAllQuestion() = runTest {
        dao.insertQuestions(*questions.toTypedArray())
        dao.deleteAllQuestion()
        val allQuestions = dao.getQuestionsByQuizId(1).first()
        assertThat(allQuestions).isEmpty()
    }

    @Test
    fun deleteAllQuizzes() = runTest {
        quizzes.forEach { dao.insertQuizToRoom(it) }
        dao.deleteAllQuizzes()
        val allQuizzes = dao.getAllQuizzesFromRoom().first()
        assertThat(allQuizzes).isEmpty()
    }

    @Test
    fun getAllWordsFromRoom() = runTest {
        words.forEach { dao.insertWordToRoom(it) }
        val allWords = dao.getAllWordsFromRoom().first()
        assertThat(allWords).isEqualTo(words)
    }

    @Test
    fun getAllQuizzesFromRoom() = runTest {
        quizzes.forEach { dao.insertQuizToRoom(it) }
        val allQuizzes = dao.getAllQuizzesFromRoom().first()
        assertThat(allQuizzes).isEqualTo(quizzes)
    }

    /*@Test
    fun searchWords() = runTest {
        words.forEach { dao.insertWordToRoom(it) }
        val searchWords = dao.searchWords("1").first().first()
        assertThat(searchWords).isEqualTo(words.first())
    }*/

    @Test
    fun getQuestionsByQuizId() = runTest {
        dao.insertQuestions(*questions.toTypedArray())
        assertThat(questions).isEqualTo(dao.getQuestionsByQuizId(questions.first().quizId).first())
    }

    @Test
    fun getCompletedQuizzesFromRoom() = runTest {
        val newQuiz = Quiz(id = 3, isCompleted = true)
        quizzes.add(newQuiz)
        quizzes.forEach { dao.insertQuizToRoom(it) }
        val completedQuiz = dao.getCompletedQuizzesFromRoom().first().first()
        assertThat(newQuiz).isEqualTo(completedQuiz)
    }

    @Test
    fun getActiveQuizzesFromRoom() = runTest {
        val activeQuizzes = listOf(
            Quiz(id = 1, isCompleted = false),
            Quiz(id = 2, isCompleted = false),
        )
        val notActiveQuiz = Quiz(id = 3, isCompleted = true)
        dao.insertQuizToRoom(notActiveQuiz)
        activeQuizzes.forEach { dao.insertQuizToRoom(it) }

        val getActives = dao.getActiveQuizzesFromRoom().first()
        assertThat(getActives).isEqualTo(activeQuizzes)
    }

    @Test
    fun getQuestionCandidateWords() = runTest {
        val today = LocalDateTime.now()
        val tomorrow = today + Duration.ofDays(1)
        val notCandidateWord = Word(
            id = 3,
            name = "name3",
            meaning = "meaning3",
            nextRepetitions = tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        dao.insertWordToRoom(notCandidateWord)
        words.forEach { dao.insertWordToRoom(it) }
        val candidateWords = dao.getQuestionCandidateWords().first()
        assertThat(candidateWords).doesNotContain(notCandidateWord)
    }

    @Test
    fun getUpcomingQuizzesWithQuestions() = runTest {
        val today = LocalDateTime.now()
        val yesterday = today - Duration.ofDays(1)
        val tomorrow = today + Duration.ofDays(1)
        val activeQuizzes = listOf(
            Quiz(id = 1, date = today.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
            Quiz(id = 2, date = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
        )
        val notActiveQuiz =
            Quiz(id = 3, date = tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        dao.insertQuizToRoom(notActiveQuiz)
        activeQuizzes.forEach { dao.insertQuizToRoom(it) }

        val questions = mutableListOf(
            Question(id = 1, title = "title1", correctAnswer = "answer1", quizId = 1, wordId = 1),
            Question(id = 2, title = "title2", correctAnswer = "answer2", quizId = 1, wordId = 2),
            Question(id = 3, title = "title1", correctAnswer = "answer1", quizId = 2, wordId = 3),
            Question(id = 4, title = "title2", correctAnswer = "answer2", quizId = 2, wordId = 4),
            Question(id = 5, title = "title1", correctAnswer = "answer1", quizId = 3, wordId = 5),
            Question(id = 6, title = "title2", correctAnswer = "answer2", quizId = 3, wordId = 6),
        )
        dao.insertQuestions(questions = questions.toTypedArray())
        val upcomingQuizWithQuestions = dao.getUpcomingQuizzesWithQuestions().first()
        val notUpcomingQuizWithQuestions = dao.getQuizWithQuestionsByQuizId(3).first()

        assertThat(upcomingQuizWithQuestions).doesNotContain(notUpcomingQuizWithQuestions)

    }

    @Test
    fun getWordById() = runTest {
        dao.insertWordToRoom(words.first())
        val word = dao.getWordById(words.first().id)
        assertThat(word).isEqualTo(words.first())
    }

    @Test
    fun getQuizWithQuestionsByQuizId() = runTest {
        val questions = listOf(
            Question(id = 1, title = "title1", correctAnswer = "answer1", quizId = 1, wordId = 1),
            Question(id = 2, title = "title2", correctAnswer = "answer2", quizId = 1, wordId = 2)
        )
        val newQuizWithQuestions = QuizWithQuestions(Quiz(id = 1), questions)
        dao.insertQuizToRoom(newQuizWithQuestions.quiz)
        dao.insertQuestions(*newQuizWithQuestions.questions.toTypedArray())
        val quizWithQuestions = dao.getQuizWithQuestionsByQuizId(1).first()

        assertThat(quizWithQuestions).isEqualTo(newQuizWithQuestions)


    }

    @Test
    fun shouldCreateQuiz() = runTest {
       val words = mutableListOf(
           Word(id = 1, name = "name1", meaning = "meaning1"),
           Word(id = 2, name = "name2", meaning = "meaning2"),
           Word(id = 3, name = "name1", meaning = "meaning1"),
           Word(id = 4, name = "name2", meaning = "meaning2"),
           Word(id = 5, name = "name1", meaning = "meaning1"),
           Word(id = 6, name = "name2", meaning = "meaning2"),
           Word(id = 7, name = "name1", meaning = "meaning1"),
           Word(id = 8, name = "name2", meaning = "meaning2"),
           Word(id = 9, name = "name1", meaning = "meaning1"),
           Word(id = 10, name = "name2", meaning = "meaning2"),
        )

        words.forEach { dao.insertWordToRoom(it) }
        assertThat(dao.shouldCreateQuiz()).isTrue()
    }

    @Test
    fun updateQuizIsCompleted() = runTest {
        dao.insertQuizToRoom(quizzes.first())
        dao.updateQuizIsCompleted(1)
        val updatedQuiz=dao.getAllQuizzesFromRoom().first().first()
        assertThat(updatedQuiz.isCompleted).isTrue()
    }

}