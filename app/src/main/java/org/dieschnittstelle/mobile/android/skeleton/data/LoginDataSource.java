package org.dieschnittstelle.mobile.android.skeleton.data;

import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;
import org.dieschnittstelle.mobile.android.skeleton.classes.User;
import org.dieschnittstelle.mobile.android.skeleton.data.model.LoggedInUser;
import org.dieschnittstelle.mobile.android.skeleton.util.Api;
import org.dieschnittstelle.mobile.android.skeleton.util.ApiInterface;

import java.io.IOException;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private ApiInterface apiInterface = Api.getClient();
    public Result res;
    public MutableLiveData<Result> responseMutableLiveData = new MutableLiveData<>();
    public void login(String username, String password) {
        User user = new User();
        user.setEmail(username);
        user.setPwd(password);

        Call<Boolean> create = apiInterface.login(user);
        try {
            create.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    responseMutableLiveData.setValue(new Result.Success(user));

                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                    responseMutableLiveData.setValue( new Result.Error(new IOException("Error logging in", t)));
                    res =  new Result.Error(new IOException("Error logging in", t));
                    call.cancel();
                }
            });
        } catch (Exception e) {
            res =  new Result.Error(new IOException("Error logging in", e));
        }

    };

    public void logout() {
        // TODO: revoke authentication
    }
}