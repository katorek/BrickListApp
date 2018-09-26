package com.wjaronski.bricklistapp.util;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.wjaronski.bricklistapp.MainActivity;
import com.wjaronski.bricklistapp.SettingsActivity;

import java.util.Locale;

public class LanguageHelper {
    public static void changeLocale(Resources res,String locale){
        Configuration config;
        config = new Configuration(res.getConfiguration());
        Log.e("LANG","lang "+locale);
        switch (locale){
            case "en":
                config.setLocale(Locale.forLanguageTag("en"));
                break;
            default:
                config.setLocale(Locale.forLanguageTag("pl"));
                break;
        }
        res.updateConfiguration(config,res.getDisplayMetrics());
    }
//    public static void changeLocale(String locale){
//        Resources res = SettingsActivity.getRes();
//    }
}
