package org.dieschnittstelle.mobile.android.skeleton.data;

import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;
import org.dieschnittstelle.mobile.android.skeleton.classes.User;
import org.dieschnittstelle.mobile.android.skeleton.data.model.LoggedInUser;
import org.dieschnittstelle.mobile.android.skeleton.util.Api;
import org.dieschnittstelle.mobile.android.skeleton.util.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private ApiInterface apiInterface = Api.getClient();
    public Result res;
    public Result<User> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            User user = new User();
            user.setEmail(username);
            user.setPwd(password);

            Call<Boolean> create = apiInterface.login(user);
            create.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    res = new Result.Success(user);
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    res =  new Result.Error(new IOException("Error logging in", t));
                    call.cancel();
                }
            });

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
        return new Result.Success(new User());
    }

    public void logout() {
        // TODO: revoke authentication
    }
}