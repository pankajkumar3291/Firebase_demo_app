package com.pank.pankapp.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.widget.Toast;

import com.pank.pankapp.components.SessionSecuredPreferences;
import com.pank.pankapp.util.Constants;
import com.pank.pankapp.util.ObjectUtil;

import org.jetbrains.annotations.NotNull;

public class GlobalApplication extends Application {

    private String packageName;
    private Typeface fontAwesomeTypeface;
    private SessionSecuredPreferences loginPref;

    @Override
    public void onCreate() {
        super.onCreate ( );
        this.setAppInstance ( );
    }

    protected void setAppInstance() {
        ApplicationHelper.setApplicationObj ( this );
    }

    public Typeface getTypeface() {
        if(this.fontAwesomeTypeface == null) {
            this.initIcons ( );
        }
        return this.fontAwesomeTypeface;
    }

    public void initIcons() {
        this.fontAwesomeTypeface = Typeface.createFromAsset ( this.getAssets ( ), Constants.ICON_FILE );
    }

    public SessionSecuredPreferences loginPreferences(String preferenceName) {
        if(this.loginPref == null) {
            this.loginPref = new SessionSecuredPreferences ( this.getBaseContext ( ), this.getBaseContext ( ).getSharedPreferences ( preferenceName, Context.MODE_PRIVATE ) );
        }
        return this.loginPref;
    }

    public Context getContext() {
        return this.getApplicationContext ( );
    }

    public String phoneFormat() {
        return Constants.phoneFormat;
    }

    public int getResourceId(String resourceName, String defType) {
        return getResourceId ( resourceName, defType, this.packageName ( ) );
    }

    public int getResourceId(String resourceName, String defType, String packageName) {
        return (!ObjectUtil.isNumber ( resourceName ) && ObjectUtil.isNonEmptyStr ( resourceName )) ? this.getResources ( ).getIdentifier ( resourceName, defType, packageName ) : 0;
    }

    public PackageInfo packageInfo() {
        PackageManager manager = this.getPackageManager ( );
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo ( this.getPackageName ( ), 0 );
        } catch (PackageManager.NameNotFoundException e2) {
            Toast.makeText ( this.getBaseContext ( ), "Package Not Found : " + e2, Toast.LENGTH_SHORT ).show ( );
        }
        return info;
    }

    public String packageName() {
        if(this.packageName == null) {
            PackageInfo info = this.packageInfo ( );
            this.packageName = info != null ? info.packageName : "com.smartit.talabia";
        }
        return this.packageName;
    }

    @Override
    public void onTerminate() {
        super.onTerminate ( );
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged ( newConfig );
    }

}
