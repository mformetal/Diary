package miles.forum.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;
import miles.forum.R;
import miles.forum.data.SessionAuthorizer;
import miles.forum.ui.widget.TypefaceButton;
import miles.forum.util.Logg;
import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 1/15/16.
 */
public class AuthenticateActivity extends BaseActivity implements AuthCallback {

    @Bind(R.id.activity_authenticate_button)
    DigitsAuthButton digitsAuthButton;

    private ProgressDialog progressDialog;
    private SessionAuthorizer authorizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        digitsAuthButton.setBackgroundColor(ContextCompat.getColor(this, R.color.accent));
        digitsAuthButton.setAuthTheme(R.style.Forum);
        digitsAuthButton.setCallback(this);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("https://api.twitter.com/1/account/verify_credentials.json")
                .build();
        authorizer = adapter.create(SessionAuthorizer.class);
    }

    private void startHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void showProgress() {
        progressDialog.show();
    }

    private void dismissProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void success(DigitsSession digitsSession, String phone) {
        TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
        TwitterAuthToken authToken = (TwitterAuthToken) digitsSession.getAuthToken();
        DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(authConfig, authToken);
        Map<String, String> authHeaders = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();
        authHeaders.put("phone", phone);

        ParseCloud.callFunctionInBackground("authorize", authHeaders,
                new FunctionCallback<HashMap>() {
                    @Override
                    public void done(HashMap object, ParseException e) {
                        if (e == null) {
                            Logg.log("NO EXCEPTIOn");
                        } else {
                            Logg.log("EXCEPTION", e);
                        }
                    }
                });
    }

    @Override
    public void failure(DigitsException e) {
        Logg.log("FAILURE", e);
    }
}
