package zuned.paniwalah.phonepaypaymentintegration;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ApiServices {
    @GET("/apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")

    Call<ResponseBody> checkStatus(
            @Path("merchantId") String merchantId,
            @Path("transactionId") String transactionId,
             @HeaderMap Map<String, String> headers

    );
}
