package com.dominusomnium.charttest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.fit.samples.basicsensorsapi.R;
import com.graphview.GraphView;
import com.graphview.ValueDependentColor;
import com.graphview.series.BarGraphSeries;
import com.graphview.series.DataPointGr;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPointGr> series = new BarGraphSeries<>(new DataPointGr[] {
                new DataPointGr(0, -1),
                new DataPointGr(1, 5),
                new DataPointGr(2, 3),
                new DataPointGr(3, 2),
                new DataPointGr(4, 6)
        });
        graph.addSeries(series);

// styling
        series.setValueDependentColor(new ValueDependentColor<DataPointGr>() {
            @Override
            public int get(DataPointGr data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(50);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
//series.setValuesOnTopSize(50);
    }
}
