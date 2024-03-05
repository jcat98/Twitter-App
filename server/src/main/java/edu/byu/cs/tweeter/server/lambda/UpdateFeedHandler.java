package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.domain.Feed;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.util.Utility;

public class UpdateFeedHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        DAOFactory dynamoDAOFactory = new DynamoDAOFactory();
        StatusService service = new StatusService(dynamoDAOFactory);
        Utility utility = new Utility();

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            Feed feed = utility.deserialize(msg.getBody(), Feed.class);
            service.postStatusToFeed(feed);
        }
        return null;
    }
}
