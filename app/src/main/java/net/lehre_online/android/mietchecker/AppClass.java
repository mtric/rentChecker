package net.lehre_online.android.mietchecker;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Process;
import android.util.Log;

/**
 * Die Klasse zeigt die Verwendung einer Applikationsklasse in Android-
 * Anwendungen. Jede Applikation kann nur eine Applikationsklasse haben, die
 * von android.app.Application erbt und im Manifest (im Tag application)
 * deklariert und gestartet wird.
 *
 * @author  Wolfgang Lang
 * @version 2.0.0, 2015-01-25
 * @see     "Foliensatz zur Vorlesung"
 */
public class AppClass extends Application {

    public static final boolean DBG = true;
    private static final String CNAME = "AppClass.";
    private static AppClass singleton;

    @Override
    public final void onCreate() {

        super.onCreate();

        final String MNAME = "onCreate()";
        final String TAG = CNAME + MNAME;
        if( DBG ) {
            Log.v( TAG, "entering..." );
            Log.v( TAG, "TID: " + Process.myTid() + ", " +
                    Thread.currentThread().getName() );
        }

        singleton = this;

        if( DBG ) Log.v( TAG, "...exiting" );
    }

    /**
     *
     * @return returns the singleton
     */
    public static AppClass getInstance() { return singleton; }

    /**
     * Liefert android:versionName aus app.build.gradle bzw. einen Dummywert falls
     * versionName nicht verfügbar
     *
     * @return android:versionName
     */
    public String getVersionName() {

        final String MNAME = "getVersionName()";
        final String TAG = CNAME + MNAME;
        if( DBG ) Log.v( TAG, "entering..." );

        String sVersionName = "android:versionName not available.";

        try{
            sVersionName = getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionName;
        } catch( PackageManager.NameNotFoundException ex ) {
            sVersionName = "android:versionName not available.";
        }

        if( DBG ) Log.v( TAG, "...exiting" );
        return sVersionName;
    }
}