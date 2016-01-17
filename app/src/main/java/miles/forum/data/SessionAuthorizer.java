package miles.forum.data;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by mbpeele on 1/15/16.
 */
public interface SessionAuthorizer {
    String serviceProviderHeader = "X-Auth-Service-Provider";
    String credentialsAuthorizationHeader = "X-Verify-Credentials-Authorization";

    @GET("/verify_credentials?provider=digits")
    Observable<Response> verifyCredentials(
            @Header(serviceProviderHeader) String serviceProvider,
            @Header(credentialsAuthorizationHeader) String authorization,
            @Query("id") long id);
}
