package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dto.AuthTokenDTO;

public interface AuthTokenDAO {
    AuthToken recordAuthToken(String token, long timestamp, String alias);
    String isValidAuthToken(String token, long timestamp);
    boolean deleteAuthToken(String token);
}
