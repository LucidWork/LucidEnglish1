package com.thesejongproject.smart;

public interface Constants {

    /* Shared Preferences */

    String SP_LOGGED_USER_ID = "loggedUserID";
    String SP_LOGGED_USER = "loggedUser";
    String SP_WORD_HOUSE = "wordHouse";
    String SP_IS_MANUAL_MODE = "isManualMode";
    String SP_HIT_TIMER = "isHitTimer";

    /* Web Services */
    String WEB_PERFORM_LOGIN = "performLogin";
    String WEB_GET_EXERCISE = "getExercise";
    String WEB_QUIT_EXERCISE = "quitExercise";
    String WEB_GET_EXERCISE_HISTORY = "getExerciseHistory";
    String WEB_GET_MANUAL_MODE = "getManualMode";
    String WEB_PERFORM_REGISTRATION = "performRegistration";
    String WEB_FORGOT_PASSWORD = "forgotPassword";
    String WEB_SAVE_MANUAL_MODE = "saveManualMode";

    /* Tables */
    String TABLE_EXERCISE = "exerciseTable";
    String TABLE_EXERCISE_HISTORY = "exerciseHistoryTable";
}
