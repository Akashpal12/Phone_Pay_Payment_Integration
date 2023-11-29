package zuned.paniwalah.phonepaypaymentintegration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class PaymentRecordsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<MYDataModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_records);

        recyclerView = findViewById(R.id.recyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(PaymentRecordsActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new MyAdapter(PaymentRecordsActivity.this,list));



    }
}