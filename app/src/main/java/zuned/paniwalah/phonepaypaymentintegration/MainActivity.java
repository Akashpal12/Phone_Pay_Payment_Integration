package zuned.paniwalah.phonepaypaymentintegration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.phonepe.intent.sdk.api.B2BPGRequest;
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder;
import com.phonepe.intent.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.PhonePeInitException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button recordBtn;
    private String apiEndPoint = "/pg/v1/pay";
    private final String salt = "58a63b64-574d-417a-9214-066bee1e4caa"; // salt key
    private final String MERCHANT_ID = "ATMOSTUAT";  // Merchant id
    private String MERCHANT_TID = generateRandomString(4);
    String myamout = "";

    TextInputEditText amount;
    // private String MERCHANT_TID = "xkos";
    private final String BASE_URL = "https://api-preprod.phonepe.com/";

    List<MYDataModel> myDataModelList = new ArrayList<>();

    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordBtn = findViewById(R.id.recordBtn);
        amount = findViewById(R.id.amount);
        myamout = amount.getText().toString();

        PhonePe.init(this);

        try {
            PhonePe.getUpiApps();
        } catch (PhonePeInitException exception) {
            exception.printStackTrace();
        }


        Button button = findViewById(R.id.paymentBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (amount.getText().toString().isEmpty() ){
                    Toast.makeText(MainActivity.this, "Please Fill the amount", Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        myamout = amount.getText().toString();

                        JSONObject data = new JSONObject();

                        data.put("merchantTransactionId", MERCHANT_TID); // String. Mandatory
                        data.put("merchantId", MERCHANT_ID); // String. Mandatory
                        data.put("amount", Integer.parseInt(myamout)); // Long. Mandatory
                        data.put("mobileNumber", "6387040456"); // String. Optional
                        data.put("callbackUrl", "https://papaycoders.in"); // String. Mandatory

                        JSONObject paymentInstrument = new JSONObject();
                        paymentInstrument.put("type", "UPI_INTENT");
                        // paymentInstrument.put("targetApp", "net.one97.paytm");
                        paymentInstrument.put("targetApp", "com.phonepe.simulator");
                        data.put("paymentInstrument", paymentInstrument); // OBJECT. Mandatory

                        JSONObject deviceContext = new JSONObject();
                        deviceContext.put("deviceOS", "ANDROID");
                        data.put("deviceContext", deviceContext);

                        String payloadBase64 = android.util.Base64.encodeToString(data.toString().getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);
                        String checksum = sha256(payloadBase64 + apiEndPoint + salt) + "###1";

                        Log.d("PAPAYACODERS", "onCreate: " + payloadBase64);
                        Log.d("PAPAYACODERS checksum", "onCreate: " + checksum);

                        B2BPGRequest b2BPGRequest = new B2BPGRequestBuilder()
                                .setData(payloadBase64)
                                .setChecksum(checksum)
                                .setUrl(apiEndPoint)
                                .build();


                        // Intent intent = PhonePe.getImplicitIntent(MainActivity.this, b2BPGRequest, "net.one97.paytm");
                        Intent intent = PhonePe.getImplicitIntent(MainActivity.this, b2BPGRequest, "com.phonepe.simulator");
                        if (intent != null) {
                            startActivityForResult(intent, 1);
                        }
                    } catch (Exception e) {
                        Log.d("Ondtaa", "onCreate: " + e.getMessage());
                    }

                }


            }
        });
        recordBtn.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick (View v){
                Intent intent = new Intent(MainActivity.this, PaymentRecordsActivity.class);
                startActivity(intent);
            }
        });


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.d("PAPAYACODERS", "onActivityResult: " + data);
            Log.d("PAPAYACODERS", "onActivityResult: " + (data != null ? data.getData() : null));
            checkStatus();
        }



    }
    private String sha256(String input) {
        try {
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String generateRandomString(int length) {
        String charset = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = charset.charAt(random.nextInt(charset.length()));
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private void checkStatus() {
        String apiUrl = "/pg/v1/status/" + MERCHANT_ID + "/" + MERCHANT_TID + salt;
        String xVerify1 = sha256(apiUrl) + "###1";
        String xVerify = sha256("/pg/v1/status/" + MERCHANT_ID + "/" + MERCHANT_TID + salt) + "###1";
        //  String xVerify =" d16687da3aadeb983fd8cc9ba64a73360a85c0763fc3a7f3b85cf69db3dc8134###1";

        Log.d("phonepe", "onCreate  xverify : " + xVerify);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-VERIFY", xVerify);
        headers.put("X-MERCHANT-ID", MERCHANT_ID);

        String fullUrl = BASE_URL + apiUrl;
        Log.d("phonepedkskjhd", "Request URL: " + fullUrl); // Print the URL to the log
        Log.d("Currenttie", "onCreate  xverify : " + MERCHANT_TID);


        Map<String, String> headers1 = new HashMap<>();
        headers1.put("Content-Type", "application/json");

        headers1.put("X-VERIFY", xVerify);
        headers1.put("X-MERCHANT-ID", MERCHANT_ID);
        //headers.put("accept", "application/json");

        RetrofitClient.getClient().checkStatus(MERCHANT_ID,MERCHANT_TID,headers1).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()){
                        String responseBodyString = response.body().string();
                        Log.d("Response", responseBodyString);
                        TextView status = findViewById(R.id.status);
                        status.setText(responseBodyString);
                        Toast.makeText(MainActivity.this, responseBodyString, Toast.LENGTH_SHORT).show();
                        JsonObject jsonObject = new Gson().fromJson(responseBodyString,JsonObject.class);
                        JsonObject jsonObject1 =jsonObject.getAsJsonObject("data");
                        JsonObject jsonObject2 = jsonObject1.getAsJsonObject("paymentInstrument");
                        MYDataModel model = new MYDataModel();
                        model.setSuccess(jsonObject.get("success").getAsBoolean());
                        model.setCode(jsonObject.get("code").getAsString());
                        model.setMessage(jsonObject.get("message").getAsString());
                        model.setMerchantId(jsonObject1.get("merchantId").getAsString());
                        model.setMerchantId(jsonObject1.get("merchantTransactionId").getAsString());
                        model.setAmount(jsonObject1.get("amount").getAsString());
                        model.setState(jsonObject1.get("state").getAsString());

                        model.setMaskedAccountNumber(jsonObject2.get("maskedAccountNumber").getAsString());
                        model.setUtr(jsonObject2.get("utr").getAsString());
                        model.setUpiTransactionId(jsonObject2.get("upiTransactionId").getAsString());
                        model.setAccountHolderName(jsonObject2.get("accountHolderName").getAsString());
                        myDataModelList.add(model);
                        amount.setText("");

                        Toast.makeText(MainActivity.this, ""+myDataModelList.get(0).getAccountHolderName(), Toast.LENGTH_SHORT).show();



                        myamout="";
                    }
                    else {
                        try {
                            Toast.makeText(MainActivity.this, ""+response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        //lifecycleScope.launch(Dispatchers.IO, () -> {
        // Your network request code goes here
    }

}