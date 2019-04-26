package com.mobitechstudio.wise.quotes.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.ads.mobitechadslib.AdsModel;
import com.ads.mobitechadslib.MobiAdBanner;
import com.ads.mobitechadslib.MobitechAds;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.utility.ActivityUtilities;

import io.reactivex.disposables.CompositeDisposable;


public class SettingsActivity extends BaseActivity {
    private AdsModel adsModel ;
    private MobiAdBanner mobiAdBanner ;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String adCategory = "3" ; //specify the ad category you want to show.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setContentView(R.layout.activity_settings);

        // replace linear layout by preference screen
        getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();

        initToolbar(true);
        setToolbarTitle(getString(R.string.settings));
        enableUpButton();
        // ....................Interstitial Ad ...............
        MobitechAds.getIntertistialAd (
                SettingsActivity.this ,
                adCategory);
// ...................End of Interstitial ad............
    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preference);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityUtilities.getInstance().invokeNewActivity(SettingsActivity.this, MainActivity.class, true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityUtilities.getInstance().invokeNewActivity(SettingsActivity.this, MainActivity.class, true);
    }
}