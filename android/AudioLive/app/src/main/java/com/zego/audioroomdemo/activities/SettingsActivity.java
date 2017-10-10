package com.zego.audioroomdemo.activities;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zego.audioroomdemo.AudioApplication;
import com.zego.audioroomdemo.utils.AppSignKeyUtils;
import com.zego.audioroomdemo.utils.PrefUtils;
import com.zego.audioroomdemo.R;
import com.zego.audioroomdemo.utils.ShareUtils;
import com.zego.zegoaudioroom.*;

import java.io.File;
import java.io.FilenameFilter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.tv_version)
    public TextView tvVersion;

    @Bind(R.id.tv_version2)
    public TextView tvVersion2;

    @Bind(R.id.tv_user_id)
    public TextView tvUserId;

    @Bind(R.id.tv_user_name)
    public EditText etUserName;

    @Bind(R.id.checkbox_use_test_env)
    public CheckBox cbUseTestEnv;

    @Bind(R.id.checkbox_audio_prepare)
    public CheckBox cbTurnOnAudioPrepare;

    @Bind(R.id.checkbox_manual_publish)
    public CheckBox cbManualPublish;

    @Bind(R.id.sp_app_flavor)
    public Spinner spAppFlavors;

    @Bind(R.id.et_app_id)
    public EditText etAppId;

    @Bind(R.id.ll_app_key)
    public LinearLayout llAppKey;

    @Bind(R.id.et_app_key)
    public EditText etAppKey;

    @Bind(R.id.container)
    public LinearLayout llContainer;

    private boolean oldUseTestEnvValue;
    private boolean oldAudioPrepareValue;
    private boolean oldManualPublishValue;
    private String oldUserName;

    private long oldAppId;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.checkbox_audio_prepare:
                    PrefUtils.enableAudioPrepare(isChecked);
                    break;

                case R.id.checkbox_manual_publish:
                    PrefUtils.setManualPublish(isChecked);
                    break;

                case R.id.checkbox_use_test_env:
                    AudioApplication.sApplication.setUseTestEnv(isChecked);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        tvVersion.setText(ZegoAudioRoom.version());
        tvVersion2.setText(ZegoAudioRoom.version2());

        final Intent startIntent = getIntent();
        spAppFlavors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long appId = 0;
                if (position == 2) {
                    appId = (startIntent != null && startIntent.hasExtra("appId")) ? startIntent.getLongExtra("appId", -1) : -1;
                    String signKey = (startIntent != null && startIntent.hasExtra("rawKey")) ? startIntent.getStringExtra("rawKey") : "";
                    if (appId > 0 && !TextUtils.isEmpty(signKey)) {
                        etAppId.setText(String.valueOf(appId));
                        etAppKey.setText(signKey);

                        startIntent.removeExtra("appId");
                        startIntent.removeExtra("rawKey");
                    } else {
                        etAppId.setText("");
                        etAppKey.setText("");
                    }

                    etAppId.setEnabled(true);
                    llAppKey.setVisibility(View.VISIBLE);
                } else {
                    switch (position) {
                    case 0:
                        appId = AppSignKeyUtils.UDP_APP_ID;
                        break;

                    case 1:
                        appId = AppSignKeyUtils.INTERNATIONAL_APP_ID;
                        break;
                    }

                    etAppId.setEnabled(false);
                    etAppId.setText(String.valueOf(appId));

                    byte[] signKey = AppSignKeyUtils.requestSignKey(appId);
                    etAppKey.setText(AppSignKeyUtils.convertSignKey2String(signKey));
                    llAppKey.setVisibility(View.GONE);
                }
                setTitle(AppSignKeyUtils.getAppTitle(appId, SettingsActivity.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        oldAppId = (startIntent != null) ? startIntent.getLongExtra("appId", -1) : -1;
        if (AppSignKeyUtils.isUdpProduct(oldAppId)) {
            spAppFlavors.setSelection(0);
            startIntent.removeExtra("appId");
        } else if (AppSignKeyUtils.isInternationalProduct(oldAppId)) {
            spAppFlavors.setSelection(1);
            startIntent.removeExtra("appId");
        } else {
            spAppFlavors.setSelection(2);
        }

        tvUserId.setText(PrefUtils.getUserId());

        oldUserName = PrefUtils.getUserName();
        etUserName.setText(oldUserName);

        oldUseTestEnvValue = AudioApplication.sApplication.isUseTestEnv();
        cbUseTestEnv.setChecked(oldUseTestEnvValue);

        oldAudioPrepareValue = PrefUtils.isEnableAudioPrepare();
        cbTurnOnAudioPrepare.setChecked(oldAudioPrepareValue);

        oldManualPublishValue = PrefUtils.isManualPublish();
        cbManualPublish.setChecked(oldManualPublishValue);

        cbUseTestEnv.setOnCheckedChangeListener(checkedChangeListener);
        cbTurnOnAudioPrepare.setOnCheckedChangeListener(checkedChangeListener);
        cbManualPublish.setOnCheckedChangeListener(checkedChangeListener);

        llContainer.setOnClickListener(new View.OnClickListener() {

            private long[] mHits = new long[5];

            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= SystemClock.uptimeMillis() - 700) {
                    sendLog2App();

                    for (int i = 0; i < mHits.length; i++) {
                        mHits[i] = 0;
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        String _appIdStr = etAppId.getEditableText().toString();
        String appKey = etAppKey.getEditableText().toString();
        long appId = 0;
        if (!TextUtils.isEmpty(_appIdStr)) {
            try {
                appId = Long.valueOf(_appIdStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.zg_tip_appid_format_illegal, Toast.LENGTH_LONG).show();
                etAppId.requestFocus();
                return;
            }
        }

        boolean reInitSDK = false;
        Intent resultIntent = null;
        if (appId != oldAppId) {
            // appKey长度必须等于32位
            byte[] signKey;
            try {
                signKey = AppSignKeyUtils.parseSignKeyFromString(appKey);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.zg_tip_appkey_must_32_bits, Toast.LENGTH_LONG).show();
                etAppKey.requestFocus();
                return;
            }

            resultIntent = new Intent();
            resultIntent.putExtra("appId", appId);
            resultIntent.putExtra("signKey", signKey);
            resultIntent.putExtra("rawKey", appKey);
            reInitSDK = true;
        }

        String userName = etUserName.getEditableText().toString();
        if (!TextUtils.equals(userName, oldUserName)
                && !TextUtils.isEmpty(userName)) {
            PrefUtils.setUserName(userName);
            reInitSDK = true;
        }

        reInitSDK = reInitSDK | (PrefUtils.isEnableAudioPrepare() != oldAudioPrepareValue);
        reInitSDK = reInitSDK | (AudioApplication.sApplication.isUseTestEnv() != oldUseTestEnvValue);
        setResult(reInitSDK ? 1 : 0, resultIntent);
        super.onBackPressed();
    }

    private void sendLog2App() {
        String rootPath = com.zego.zegoavkit2.utils.ZegoLogUtil.getLogPath(this);
        File rootDir = new File(rootPath);
        File[] logFiles = rootDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !TextUtils.isEmpty(name) && name.startsWith("zegoavlog") && name.endsWith(".txt");
            }
        });

        if (logFiles.length > 0) {
            ShareUtils.sendFiles(logFiles, this);
        } else {
            Log.w("SettingFragment", "not found any log files.");
        }
    }
}
