package parkjunu.finder.com.viewer;

import android.preference.Preference;
import android.os.Bundle;

public class SettingActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceClickListener {
    //TODO 셋팅 액티비티 구현
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setting);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }
}
