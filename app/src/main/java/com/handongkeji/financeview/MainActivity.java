package com.handongkeji.financeview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FinanceView fv = (FinanceView) findViewById(R.id.finance);
//        fv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "xxxxxx", Toast.LENGTH_SHORT).show();
//            }
//        });


        List<FinanceView.FinanceData> datas = new ArrayList<>();

        datas.add(new FinanceView.FinanceData(0,20));
        datas.add(new FinanceView.FinanceData(1,150));
        datas.add(new FinanceView.FinanceData(2,1466));
        datas.add(new FinanceView.FinanceData(3,400));
        datas.add(new FinanceView.FinanceData(4,300));
        datas.add(new FinanceView.FinanceData(5,350));

        fv.setFinanceData(datas);



    }
}
