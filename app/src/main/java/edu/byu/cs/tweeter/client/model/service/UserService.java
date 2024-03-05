package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticateTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetUserTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleTaskObserver;

public class UserService {
    public static final String URL_PATH_LOGIN = "/login";
    public static final String URL_PATH_REGISTER = "/register";
    public static final String URL_PATH_USER = "/getuser";
    public static final String URL_PATH_LOGOUT = "/logout";

    public void getUserProfile(String userAlias, GetUserTaskObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                userAlias, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
    }

    public void getUserClickable(String clickable, GetUserTaskObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                clickable, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
    }

    public void loginUser(String userAlias, String password, AuthenticateTaskObserver observer) {
        LoginTask loginTask = new LoginTask(userAlias, password, new AuthenticateTaskHandler(observer));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void registerUser(String firstName, String lastName, String alias, String password,
                             Drawable imageToUpload, AuthenticateTaskObserver observer) {
        // Convert image to byte array.
        Bitmap image = ((BitmapDrawable) imageToUpload).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        // Send register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new AuthenticateTaskHandler(observer));

        BackgroundTaskUtils.runTask(registerTask);
    }

    public void logoutUser(SimpleTaskObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new SimpleTaskHandler(observer));
        BackgroundTaskUtils.runTask(logoutTask);
    }

    public void clearUserCache() {
        Cache.getInstance().clearCache();
    }
}
