package com.example.buddyapp;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    DBHelper dbHelper;
    PieChart pieChart;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        dbHelper = new DBHelper(this);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        loadGenderChart();
        loadStateChart();
    }

    private void loadGenderChart() {
        int males = dbHelper.getGenderCount("Male");
        int females = dbHelper.getGenderCount("Female");

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(males, "Male"));
        entries.add(new PieEntry(females, "Female"));

        PieDataSet dataSet = new PieDataSet(entries, "Gender Distribution");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void loadStateChart() {
        Map<String, Integer> stateData = dbHelper.getStateCounts();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : stateData.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey()); // Note: Labels handling varies by library version
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Friends per State");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}