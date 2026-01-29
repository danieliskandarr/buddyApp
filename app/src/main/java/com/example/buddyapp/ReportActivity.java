package com.example.buddyapp;

import android.graphics.Color; // Added for custom colors
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    DBHelper dbHelper;
    PieChart pieChart;
    BarChart barChart;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Hide default bar if you're using a custom header in XML
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }

        dbHelper = new DBHelper(this);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not identified. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadGenderChart();
        loadStateChart();
    }

    private void loadGenderChart() {
        int males = dbHelper.getGenderCount(userId, "Male");
        int females = dbHelper.getGenderCount(userId, "Female");

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        // Add entries and matching colors in the same order
        if (males > 0) {
            entries.add(new PieEntry(males, "Male"));
            colors.add(Color.parseColor("#E3F2FD")); // Light Blue matching Main List
        }
        if (females > 0) {
            entries.add(new PieEntry(females, "Female"));
            colors.add(Color.parseColor("#FFEBEE")); // Light Red matching Main List
        }

        if (entries.isEmpty()) {
            pieChart.setNoDataText("No Data Available");
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors); // Using custom gender colors
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setValueTextSize(14f);
        dataSet.setSliceSpace(3f); // Adds space between slices for a cleaner look

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // --- Visual Polish ---
        pieChart.setHoleRadius(50f); // Adjust for Donut look
        pieChart.setTransparentCircleRadius(55f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelColor(Color.BLACK);

        // Configure Legend
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYOffset(5f);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void loadStateChart() {
        Map<String, Integer> stateData = dbHelper.getStateCounts(userId);
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : stateData.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        if (entries.isEmpty()) {
            barChart.setNoDataText("No Data Available");
            barChart.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Friends per State");

        // Single color for a clean professional look (Modern Purple)
        dataSet.setColor(Color.parseColor("#6750A4"));
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);

        // --- Configure Chart Appearance ---
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false); // Hide right axis for simplicity
        barChart.getAxisLeft().setDrawGridLines(false); // Clean background

        // --- Configure X-Axis ---
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false); // Hide vertical lines
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(-45);

        barChart.animateY(1200);
        barChart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}