package com.hari.solutionhub.data;

import com.hari.solutionhub.data.model.LoggedInUser;

// Class that authentication w/ login credentials and retrieves user information.
public class LoginDataSource {

    /**
     * @noinspection rawtypes
     */
    public Result login(String username, String password){
        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Jo"
                    );
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
           return new Result.Error(new Exception("Error logging in",e));
        }
    }
    public void logout(){
        // TODO: revoke authentication
    }
}
