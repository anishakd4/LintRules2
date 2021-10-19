package com.example.lintrules2.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lintrules2.R
import java.util.*
import java.util.Locale.CANADA

abstract class BaseActivity : AppCompatActivity() {

    val ANISH = "manish"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anish_base)

        val i = CANADA
        val j = Locale.CHINA

        //TODO: Call me
    }
}

sealed class State {
    object State1 : HomeViewState()
    object State2 : HomeViewState()
    object State3 : HomeViewState()
}

class ExhaustiveWhenTest1 {

    fun test(someState: HomeViewState) {

        when (someState) {
            is State.State1 -> {
            }
            is State.State2 -> {
            }
            is State.State3 -> {
            }
            else -> {

            }
        }.exhaustive
    }

}

class ExhaustiveWhenTest2 {

    fun test(someState: HomeViewState) {

        when (someState) {
            is State.State1 -> {
            }
            is State.State2 -> {
            }
            is State.State3 -> {
            }
            else -> {

            }
        }.exhaustive
    }

}

class ExhaustiveWhenTest3 {

    fun test(someState: HomeViewState) {

        val test = when (someState) {
            is State.State1 -> {
            }
            is State.State2 -> {
            }
            else -> {

            }
        }
    }

}

open class HomeViewState() {

}

val <T> T.exhaustive: T
    get() = this

fun alertDialogTest(dialog: AlertDialog){

}

class AlertDialogTestClass(context: Context): AlertDialog(context) {

}