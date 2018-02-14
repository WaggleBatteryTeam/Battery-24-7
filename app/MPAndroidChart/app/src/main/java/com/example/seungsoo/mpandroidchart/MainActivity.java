package com.example.seungsoo.mpandroidchart;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = (LineChart)findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 0));
        entries.add(new Entry(4, 4));
        entries.add(new Entry(5, 3));

        LineDataSet lineDataSet = new LineDataSet(entries, "속성명1");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();

    }








//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        LineChart lineChart  = (LineChart) findViewById(R.id.chart);
//
//        // 모든 데이터는 DataSet 객체로 변환된 후 차트에 쓰임
//        // 이 DataSet은 차트마다 다른 Sub Class가 있어 이에 맞춰서 사용해야 함
//        // Entry는 좌표가 될 raw 데이터
//        ArrayList<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(100.0f, 0));
//        entries.add(new Entry(50.0f, 1));
//        entries.add(new Entry(75.0f, 2));
//        entries.add(new Entry(50.0f, 3));
//
//        // Entry를 LineDataSet 차트로 사용할 수 있도록 dataSet을 변경
//        LineDataSet lineDataSet = new LineDataSet(entries, "# of Calls");
//        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//        dataSets.add(lineDataSet);
//
//        // x축으로 쓰일 라벨
//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("1.Q");
//        labels.add("2.Q");
//        labels.add("3.Q");
//        labels.add("4.Q");
//
//        // labels과 dataSet을 가지고 LineData 클래스 객체를 생성
//        LineData lineData = new LineData(dataSets);
//
//        // LineChart에서 LineData를 사용할 것임
//        lineChart.setData(lineData);
//        lineChart.invalidate();
//
//    }
}
