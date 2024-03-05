package edu.byu.cs.tweeter.model.net.response;

public class CountResponse extends Response {

    private final int count;

    public CountResponse(boolean success, int count) {
        super(success);
        this.count = count;
    }

    public CountResponse(boolean success, String message, int count) {
        super(success, message);
        this.count = count;
    }

    /**
     * An indicator of whether more data is available from the server. A value of true indicates
     * that the result was limited by a maximum value in the request and an additional request
     * would return additional data.
     *
     * @return true if more data is available; otherwise, false.
     */
    public int getCount() {
        return count;
    }
}
