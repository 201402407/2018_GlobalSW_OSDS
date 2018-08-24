package com.example.administrator.huha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph2);

        LineChart chart = findViewById(R.id.chart);
        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        valsComp1.add(new Entry(100f,0));
        valsComp1.add(new Entry(50f,1));
        valsComp1.add(new Entry(75f,2));
        valsComp1.add(new Entry(50f,3));

        LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1 ");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("January");
        xVals.add("February");
        xVals.add("March");
        xVals.add("April");

        LineData data = new LineData(setComp1);


    }
}
