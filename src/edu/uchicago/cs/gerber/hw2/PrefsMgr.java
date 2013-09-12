package edu.uchicago.cs.gerber.hw2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by aakritprasad on 8/8/13.
 */
public class PrefsMgr
{
    private static SharedPreferences sSharedPreferences;

    public static void setInt(Context context, String strLocale, int nPos)
    {
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putInt(strLocale, nPos);
        editor.commit();
    }

    public static int getInt(Context context, String strLocale)
    {
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sSharedPreferences.getInt(strLocale, -99);

    }
}
