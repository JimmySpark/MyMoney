package mohaqeqi.mahdi.mymoney.App;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import mohaqeqi.mahdi.mymoney.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AppInitializer extends Application {

    public static AppDatabase db;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig
                .Builder()
                .setDefaultFontPath("font/yekan.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        db = new AppDatabase(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
    }
}
