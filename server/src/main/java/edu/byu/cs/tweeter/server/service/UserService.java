package edu.byu.cs.tweeter.server.service;

import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    private DAOFactory daoFactory;
    private AuthTokenDAO authTokenDAO;
    private UserDAO userDAO;
    private ImageDAO imageDAO;
    private Utility utility;

    public UserService(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.authTokenDAO = daoFactory.getAuthTokenDAO();
        this.userDAO = daoFactory.getUserDAO();
        this.imageDAO = daoFactory.getImageDAO();
        this.utility = new Utility();
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        //validate the user
        User user = userDAO.validateUser(request.getUsername(), utility.hashPassword(request.getPassword()));
        if(user == null) {
            throw new RuntimeException("[Bad Request] Incorrect Username or Password");
        }
        else {
            user = userDAO.getUser(request.getUsername());
        }
        AuthToken authToken = authTokenDAO.recordAuthToken(UUID.randomUUID().toString(), utility.futureTimestamp(), request.getUsername());
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getFirstName() == null){
            throw new RuntimeException("[Bad Request] Missing a first name");
        }else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if(request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing an alias");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }
        // check if the user exists
        User user = userDAO.getUser(request.getAlias());
        if(user != null) {
            throw new RuntimeException("[Bad Request] User already exists");
        }
        // hash the password
        String hashedPassword = utility.hashPassword(request.getPassword());
        // upload image to s3
        String image_url = imageDAO.uploadImage(request.getAlias(), request.getImage());
        // run the dao method
        user = userDAO.recordUser(request.getFirstName(), request.getLastName(), request.getAlias(),
                hashedPassword, image_url);
        //create an authtoken
        AuthToken authToken = authTokenDAO.recordAuthToken(UUID.randomUUID().toString(), utility.futureTimestamp(), request.getAlias());

        return new RegisterResponse(user, authToken);
    }

    public UserResponse getUser(UserRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing an authToken");
        } else if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing a user");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        User user = userDAO.getUser(request.getUserAlias());
        if(user == null) {
            throw new RuntimeException("[Bad Request] User does not exist");
        }
        return new UserResponse(user);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing an authToken");
        }
        boolean deleted = authTokenDAO.deleteAuthToken(request.getAuthToken().getToken());
        return new LogoutResponse(deleted);
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    User getDummyUserWithAlias(String alias) {
        return getFakeData().findUserByAlias(alias);
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
