package miles.diary.data.error;

public class GoogleApiNotConnectedException extends IllegalStateException {

    public GoogleApiNotConnectedException() {
        super("Client is not connected yet");
    }
}