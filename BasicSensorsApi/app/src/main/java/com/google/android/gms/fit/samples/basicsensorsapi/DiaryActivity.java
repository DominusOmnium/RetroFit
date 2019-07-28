package com.google.android.gms.fit.samples.basicsensorsapi;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.graphview.GraphView;
import com.graphview.series.DataPointGr;
import com.graphview.series.LineGraphSeries;

public class DiaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        GraphView graph = findViewById(R.id.graph);
        LineGraphSeries<DataPointGr> series = new LineGraphSeries<>(getDataPoint());

        graph.addSeries(series);

// custom paint to make a dotted line
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
    }

    public DataPointGr[] getDataPoint()
    {
        DataPointGr[] dp = new DataPointGr[]
                {
                        new DataPointGr(0, 70),
                        new DataPointGr(1, 74),
                        new DataPointGr(2, 71),
                        new DataPointGr(3, 73),
                        new DataPointGr(4, 78),
                        new DataPointGr(5, 83),
                        new DataPointGr(6, 75),
                        new DataPointGr(7, 73),
                        new DataPointGr(8, 68),
                        new DataPointGr(9, 61),
                        new DataPointGr(10, 65),
                        new DataPointGr(11, 59),
                        new DataPointGr(12, 49)
                };
        return (dp);
    }
}
