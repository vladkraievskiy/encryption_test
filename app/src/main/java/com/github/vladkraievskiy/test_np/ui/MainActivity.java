package com.github.vladkraievskiy.test_np.ui;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.vladkraievskiy.test_np.NPApplication;
import com.github.vladkraievskiy.test_np.R;

public final class MainActivity extends AppCompatActivity implements MainActivityView {

    private static final int CONFIRM_CREDENTIALS_REQUEST = 100;

    private MainActivityPresenter presenter;
    private TextView cipherTextView;
    private TextView textTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createPresenter();
        findViews();

        presenter.tryToGenerateKeys(this, new Runnable() {
            @Override
            public void run() {
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                if (keyguardManager == null) {
                    return;
                }

                Intent confirmDeviceCredentialIntent = keyguardManager.createConfirmDeviceCredentialIntent(getString(R.string.confirmation_title), "");
                startActivityForResult(confirmDeviceCredentialIntent, CONFIRM_CREDENTIALS_REQUEST);
            }
        });
    }

    private void createPresenter() {
        presenter = (MainActivityPresenter) getLastCustomNonConfigurationInstance();

        if (presenter == null) {
            presenter = NPApplication.get(this).getPresentersFactory().createMainActivityPresenter(this);
        }
    }

    private void findViews() {
        cipherTextView = findViewById(R.id.encrypted);
        textTextView = findViewById(R.id.decrypted);
        progressBar = findViewById(R.id.progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != CONFIRM_CREDENTIALS_REQUEST) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        presenter.loadRepositories();
    }

    @Override
    public MainActivityPresenter onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setCipherText(@NonNull String cipherText) {
        cipherTextView.setText(cipherText);
    }

    @Override
    public void setText(@NonNull String text) {
        textTextView.setText(text);
    }

    @Override
    public void showUnSecureDeviceDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.security_warning)
                .setMessage(R.string.security_warning_message)
                .setPositiveButton(R.string.security_warning_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD));
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}
