package xyz.ashioto.ashioto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
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
    @BindView(R.id.login_event)
    AppCompatEditText login_event;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor sharedPrefEditor;
    Callback<AuthResponse> loginCallback = new Callback<AuthResponse>() {
        @Override
        public void onResponse(Response<AuthResponse> response, Retrofit retrofit) {
            if (response.body().auth && response.body().s_admin) {
                //If the auth call returns true, take the user to the Home activity
                Intent homeStartIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(homeStartIntent);
                sharedPrefEditor.putString("auth-type", "s_admin").commit();
                sharedPrefEditor.putString("current_event", login_event.getText().toString()).commit();
                finish();
            } else if(response.body().auth && !response.body().s_admin){
                //If the auth call returns true, take the user to the Home activity
                Intent homeStartIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(homeStartIntent);
                sharedPrefEditor.putString("auth-type", "admin").commit();
                sharedPrefEditor.putString("current_event", login_event.getText().toString()).commit();
                finish();
            }
            else {
                //If the auth call returns false, give a toast to user
                Toast.makeText(LoginActivity.this, "Wrong email/password combination", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            //If the auth call fails due to some reason, give a toast to user
            Toast.makeText(LoginActivity.this, "Failed to login. Try again", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("sharedprefs", MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();
    }

    @OnClick(R.id.login_submit)
    void login_submit() {
        login_email.setEnabled(false);
        login_password.setEnabled(false);
        login_event.setEnabled(false);
        Call<AuthResponse> loginCall = ApplicationClass.getRetrofitInterface().authenticate(login_email.getText().toString(), login_password.getText().toString(), login_event.getText().toString());
        loginCall.enqueue(loginCallback);
    }
}
