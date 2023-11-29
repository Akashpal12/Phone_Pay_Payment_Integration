package zuned.paniwalah.phonepaypaymentintegration;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static ApiServices getClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-preprod.phonepe.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices apiService = retrofit.create(ApiServices.class);
        return apiService;
    }

}