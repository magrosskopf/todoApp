package org.dieschnittstelle.mobile.android.skeleton.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.util.Patterns;

import org.dieschnittstelle.mobile.android.skeleton.MainActivity;
import org.dieschnittstelle.mobile.android.skeleton.classes.User;
import org.dieschnittstelle.mobile.android.skeleton.data.LoginRepository;
import org.dieschnittstelle.mobile.android.skeleton.data.Result;
import org.dieschnittstelle.mobile.android.skeleton.data.model.LoggedInUser;
import org.dieschnittstelle.mobile.android.skeleton.R;
import org.dieschnittstelle.mobile.android.skeleton.util.Api;
import org.dieschnittstelle.mobile.android.skeleton.util.ApiInterface;

import java.io.IOException;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private ApiInterface apiInterface = Api.getClient();
    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        MutableLiveData<Result<User>> result = new MutableLiveData<>();
        User user = new User();
        user.setEmail(username);
        user.setPwd(password);

        Call<Boolean> create = apiInterface.login(user);
        try {
            create.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.body()) {
                        User data = ((Result.Success<User>) new Result.Success(user)).getData();
                        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getEmail() + "asdf")));
                        //result.setValue(new Result.Success(user));
                    } else {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                        //result.setValue(new Result.Error(new IOException("Error logging in", new Throwable())));
                    }

                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                   // result.setValue( new Result.Error(new IOException("Error logging in", t)));
                    call.cancel();
                }
            });
        } catch (Exception e) {
            loginResult.setValue(new LoginResult(R.string.login_failed));
           // result.setValue( new Result.Error(new IOException("Error logging in", e)));
        }
       /* System.out.println("RESULT " + result.getValue());
        if (result.getValue() instanceof Result.Success) {
            User data = ((Result.Success<User>) result.getValue()).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getEmail() + "asdf")));

        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        } */
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
        return false;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() == 6;
    }
}