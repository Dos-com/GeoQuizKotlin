package com.example.geoquizkotlin

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG  = "QuizViewModel"

class QuizViewModel : ViewModel(){
    private val questionBank = listOf(
        Question(R.string.question_australia,true),
        Question(R.string.question_oceans,true),
        Question(R.string.question_mideast,false),
        Question(R.string.question_africa,false),
        Question(R.string.question_americas,true),
        Question(R.string.question_asia,true)
    )
    private val userAnswers = mutableMapOf<Int,Boolean>()
    var currentIndex = 0
    var isCheater = false
    val currentQuestionAnswer = questionBank[currentIndex].answer

    val currentQuestionText:Int
    get() = questionBank[currentIndex].textResId


    fun moveToNext(){
        currentIndex = (currentIndex+1)%questionBank.size
    }
    fun moveToPrev(){
        currentIndex = (currentIndex-1)%questionBank.size
        if (currentIndex<0)
            currentIndex = questionBank.size-1
    }

    fun addUserAnswer(answer:Boolean):Int{
        var resultStringResId:Int
        val question = questionBank[currentIndex]
        if (question in userAnswers.keys){
            resultStringResId = R.string.answered_question
        } else{
            userAnswers[currentIndex] = answer
            resultStringResId = if (question.answer == userAnswers[question]) R.string.correct_toast else R.string.incorrect_toast
        }
        if (isCheater){
            resultStringResId = R.string.judgment_toast
        }


        return resultStringResId
    }
    fun addUserAnswer(answer:Boolean, index:Int){
        userAnswers[index] = answer
    }

    fun quizEnded() = questionBank.size == userAnswers.size
    fun userCorrectAnswers() = userAnswers.count { it.value }
    fun questionsSize() = questionBank.size
    fun userAnswers() = userAnswers


}