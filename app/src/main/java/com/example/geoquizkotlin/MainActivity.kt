package com.example.geoquizkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val USER_ANSWERS = "user_answers"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private val quizViewModel:QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)


        val currentIndex = savedInstanceState?.getInt(KEY_INDEX,0) ?: 0
        val userAnswersString = savedInstanceState?.getString(USER_ANSWERS,"") ?: ""
        quizViewModel.currentIndex = currentIndex
        val userAnswers = userAnswersString.split(";")
        if (userAnswers.size > 1){
            userAnswers.forEach {
                val s = it.split(":")
                if (s.size > 1){
                quizViewModel.addUserAnswer(s[1].toBoolean(),s[0].toInt())
                }
            }
        }

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)


        trueButton.setOnClickListener {view : View->
            checkAnswer(true)
         }

        falseButton.setOnClickListener {view : View->
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        cheatButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                val options = ActivityOptions.makeClipRevealAnimation(view , 0 ,0 , view.width, view.height)

                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            }
            else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        updateQuestion()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")

        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)

        outState.putString(USER_ANSWERS, getStringFromUserAnswers(quizViewModel.userAnswers()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK){
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT){
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false

//            quizViewModel.addUserAnswer(quizViewModel.isCheater)
        }
    }

    private fun updateQuestion(){
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer:Boolean){
        var messageId = quizViewModel.addUserAnswer(userAnswer)
        if (quizViewModel.quizEnded()){
            val str =getString(R.string.result_of_quiz, quizViewModel.userCorrectAnswers(), quizViewModel.questionsSize())
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
    }
}

private fun getStringFromUserAnswers(userAnswers1: MutableMap<Int, Boolean>):String{
    var str = ""
    userAnswers1.forEach {answer -> str+="${answer.key}:${answer.value};" }
    return str
}
