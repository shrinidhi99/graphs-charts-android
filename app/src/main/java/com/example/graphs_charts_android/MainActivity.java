package com.example.graphs_charts_android;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.graphs_charts_android.datapoints.XYValue;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    PointsGraphSeries<DataPoint> xySeries;

    private Button btnAddPt;

    private EditText mX, mY;

    GraphView mScatterPlot;

    private ArrayList<XYValue> xyValueArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddPt = findViewById(R.id.btnAddPt);
        mX = findViewById(R.id.numX);
        mY = findViewById(R.id.numY);
        mScatterPlot = findViewById(R.id.scatterPlot);
        xyValueArray = new ArrayList<>();
        init();
    }

    private void init() {
        xySeries = new PointsGraphSeries<>();

        btnAddPt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mX.getText().toString().equals("") && !mY.getText().toString().equals("")) {
                    double x = Double.parseDouble(mX.getText().toString().trim());
                    double y = Double.parseDouble(mY.getText().toString().trim());
                    Log.d(TAG, "onClick: Adding a new point. (x,y): (" + x + "," + y + ")");
                    xyValueArray.add(new XYValue(x, y));
                    init();
                } else {
                    toastMessage("You must fill out both fields!");
                }
            }
        });

        // little bit of exception handling in case there is no data
        if (xyValueArray.size() != 0) {
            createScatterPlot();
        } else {
            Log.d(TAG, "onCreate: No data to plot.");
        }
    }

    private void createScatterPlot() {
        Log.d(TAG, "createScatterPlot: Creating a scatter plot.");

        // sort the array of xy values
        xyValueArray = sortArray(xyValueArray);

        // add the data to the series
        for (int i = 0; i < xyValueArray.size(); i++) {
            try {
                double x = xyValueArray.get(i).getX();
                double y = xyValueArray.get(i).getY();
                xySeries.appendData(new DataPoint(x, y), true, 1000);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "createScatterPlot: IllegalArgumentException: " + e.getMessage());
            }
        }

        // set some properties
        xySeries.setShape(PointsGraphSeries.Shape.RECTANGLE);
        xySeries.setColor(Color.BLUE);
        xySeries.setSize(20f);

        // set scrollable and scalable
        mScatterPlot.getViewport().setScalable(true);
        mScatterPlot.getViewport().setScalableY(true);
        mScatterPlot.getViewport().setScrollable(true);
        mScatterPlot.getViewport().setScrollableY(true);

        // set manual y bounds
        mScatterPlot.getViewport().setYAxisBoundsManual(true);
        mScatterPlot.getViewport().setMaxY(150);
        mScatterPlot.getViewport().setMinY(-150);

        // set manual x bounds
        mScatterPlot.getViewport().setXAxisBoundsManual(true);
        mScatterPlot.getViewport().setMaxX(150);
        mScatterPlot.getViewport().setMinX(-150);

        mScatterPlot.addSeries(xySeries);
    }

    private ArrayList<XYValue> sortArray(ArrayList<XYValue> array) {
        /*
        //Sorts the xyValues in Ascending order to prepare them for the PointsGraphSeries<DataSet>
         */
        int factor = Integer.parseInt(String.valueOf(Math.round(Math.pow(array.size(), 2))));
        int m = array.size() - 1;
        int count = 0;
        Log.d(TAG, "sortArray: Sorting the XYArray.");


        while (true) {
            m--;
            if (m <= 0) {
                m = array.size() - 1;
            }
            Log.d(TAG, "sortArray: m = " + m);
            try {
                double tempY = array.get(m - 1).getY();
                double tempX = array.get(m - 1).getX();
                if (tempX > array.get(m).getX()) {
                    array.get(m - 1).setY(array.get(m).getY());
                    array.get(m).setY(tempY);
                    array.get(m - 1).setX(array.get(m).getX());
                    array.get(m).setX(tempX);
                } else if (tempX == array.get(m).getX()) {
                    count++;
                    Log.d(TAG, "sortArray: count = " + count);
                } else if (array.get(m).getX() > array.get(m - 1).getX()) {
                    count++;
                    Log.d(TAG, "sortArray: count = " + count);
                }
                //break when factorial is done
                if (count == factor) {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "sortArray: ArrayIndexOutOfBoundsException. Need more than 1 data point to create Plot." +
                        e.getMessage());
                break;
            }
        }
        return array;
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
