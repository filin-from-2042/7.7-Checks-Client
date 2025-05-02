package com.tradeassistant1c77;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyApplication extends Application {
    private static MyApplication instance;

    public MyApplication() {
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * Проверка на запуск приложения в первый раз
     * @param context - контекст вызова
     * @return - признак первого запуска
     */
    public static boolean IsFirstRun(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences("GLOBAL", 0); // Get preferences file (0 = no option flags set)
        boolean firstRun = settings.getBoolean("firstRun", true); // Is it first run? If not specified, use "true"

        if (firstRun){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
            return true;
        }
        else return false;
    }

    /**
     * Возвращает текущее значение периодичности из настроек
     * @return - периодичность документов типа Чек ККМ
     */
    public static String getCurrentPeriod()
    {
        MyApplication myApp = MyApplication.getInstance();
        Context currContext = myApp.getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(currContext);
        return prefs.getString(currContext.getString(R.string.s_key_period), currContext.getString(R.string.s_default_period));
    }
}