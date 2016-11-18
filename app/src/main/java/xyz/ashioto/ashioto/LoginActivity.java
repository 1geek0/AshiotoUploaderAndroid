package xyz.ashioto.ashioto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.ashioto.ashioto.retrofitClasses.AuthResponse;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_email)
    AppCompatEditText login_email;
    @BindView(R.id.login_password)
    AppCompatEditText login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_submit)
    void login_submit(){
        Call<AuthResponse> loginCall = ApplicationClass.getRetrofitInterface().authenticate(login_email.getText().toString(), login_password.getText().toString());
        loginCall.enqueue(loginCallback);
    }

    Callback<AuthResponse> loginCallback = new Callback<AuthResponse>() {
        @Override
        public void onResponse(Response<AuthResponse> response, Retrofit retrofit) {
            if (response.body().auth) {
                //If the auth call returns true, take the user to the Home activity
                Intent homeStartIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(homeStartIntent);
                // TODO: 18/11/16 add support for superadmin accounts
            } else {
                //If the auth call returns false, give a toast to user
                Toast.makeText(LoginActivity.this, "Wrong email/password combination", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            //If the auth call fails due to some reaso, give a toast to user
            Toast.makeText(LoginActivity.this, "Failed to login. Try again", Toast.LENGTH_SHORT).show();
        }
    };
}
