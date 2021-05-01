package com.personal.accountantAssistant.utils

import io.reactivex.functions.Action

object ActionUtils {
    @JvmField
    var NONE_ACTION_TO_DO = Action {}

    @JvmStatic
    fun runAction(action: Action) {
        try {
            action.run()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun conditionalActions(condition: Boolean,
                           trueUnit: Unit?) {
        conditionalActions(condition, ParserUtils.toAction(trueUnit), NONE_ACTION_TO_DO)
    }

    fun conditionalActions(condition: Boolean,
                           trueUnit: Unit?,
                           falseUnit: Unit?) {
        conditionalActions(condition, ParserUtils.toAction(trueUnit), ParserUtils.toAction(falseUnit))
    }

    private fun conditionalActions(condition: Boolean,
                                   trueAction: Action?,
                                   falseAction: Action?) {
        if (condition) {
            trueAction?.let { runAction(it) }
        } else {
            falseAction?.let { runAction(it) }
        }
    }
}