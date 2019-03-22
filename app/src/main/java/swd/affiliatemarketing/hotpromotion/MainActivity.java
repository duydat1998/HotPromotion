package swd.affiliatemarketing.hotpromotion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import swd.affiliatemarketing.hotpromotion.model.PromotionCodeTracking;

public class MainActivity extends AppCompatActivity {

    private EditText edtPromotionCode, edtTotalBill;
    private PromotionCodeTracking tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtPromotionCode = findViewById(R.id.edtPromotionCode);
        edtTotalBill = findViewById(R.id.edtTotalBill);
    }

    public void clickToUsePromotionCode(View view) {
        String promotionCode = edtPromotionCode.getText().toString().toUpperCase();
        String stringTotalBill = edtTotalBill.getText().toString();
        double totalBill = Double.parseDouble(stringTotalBill);
        postPromotionCodeTracking(promotionCode, totalBill);
        if(tracking != null){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("promotionCodeTrackings").push();
            myRef.setValue(tracking);
        }


    }

    private void postPromotionCodeTracking(String code, double totalBill){
        OkHttpClient okHttpClient = new OkHttpClient();

        Moshi moshi = new Moshi.Builder().build();

        Type type = Types.newParameterizedType(PromotionCodeTracking.class);
        final JsonAdapter<PromotionCodeTracking> jsonAdapter = moshi.adapter(type);

        String domain = getResources().getString(R.string.virtual_api);

        String url = domain + "api/PromotionCodeTrackings";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("promotionCode", code);
            jsonObject.put("totalAmoutOfOrder", totalBill);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("Error:", e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Use promotion code FAIL", Toast.LENGTH_SHORT).show();
                    }
                });
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String message = response.message();
                if(message.contains("Created")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Use promotion code successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    String json = response.body().string();
                    tracking = jsonAdapter.fromJson(json);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Use promotion code FAIL", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                countDownLatch.countDown();
            }
        });
        try{
            countDownLatch.await();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
