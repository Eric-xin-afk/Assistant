package com.example.eric.assistant.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.eric.assistant.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;

import static com.example.eric.assistant.activity.MainActivity.qq_times;
import static com.example.eric.assistant.activity.MainActivity.weibo_times;
import static com.example.eric.assistant.activity.MainActivity.weixin_times;
import static com.example.eric.assistant.activity.MainActivity.zhifu_times;

/**
 * Created by Eric on 2020/5/27.
 */

public class ChartActivity extends Activity {

    private ColumnChartView mColumnChartCc;
    private static final int DEFAULT_DATA = 0;
    private static final int SUBCOLUMNS_DATA = 1;
    private static final int STACKED_DATA = 2;

    private ColumnChartView chart;
    private ColumnChartData data;         //柱形图对应的各种属性
    private boolean hasAxes = true;         //是否要添加横纵轴的属性
    private boolean hasAxesNames = true;    //是否设置横纵轴的名字
    private boolean hasLabels = false;       //是否显示柱形图的数据
    private boolean hasLabelForSelected = false;   // 是否点中显示数据
    private int dataType = DEFAULT_DATA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        mColumnChartCc = (ColumnChartView) findViewById(R.id.column_chart_cc);
        mColumnChartCc.setOnValueTouchListener(new ValueTouchListener());
        generateData();
    }

    private void generateData() {
        switch (dataType) {
            case SUBCOLUMNS_DATA:
                generateSubcolumnsData();
                break;
            default:
                generateSubcolumnsData();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.column_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_subcolumns) {
            dataType = SUBCOLUMNS_DATA;
            generateData();
            toggleLabels();
            return true;
        }
        if (id == R.id.action_stacked) {
            Toast.makeText(ChartActivity.this,"应用使用图表",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Generates columns with subcolumns, columns have larger separation than subcolumns.
     */
    private void generateSubcolumnsData() {
        int numColumns = 4;
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;          //柱子的属性
        List<AxisValue> axisValueList = new ArrayList<>();
        Integer[] integers = {weixin_times, qq_times, weibo_times, zhifu_times};  //柱子数值的数组
        String[] selectedNames = {"微信", "QQ", "微博", "支付宝"};     //柱子名称的数组

        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue(integers[i], ChartUtils.pickColor()));  //数值和颜色
            axisValueList.add(new AxisValue(i).setLabel(selectedNames[i]));
            Column column = new Column(values);
            column.setHasLabels(true);        //是否显示柱子的数据
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }
        data = new ColumnChartData(columns);
        data.setAxisXBottom(new Axis(axisValueList));   //X轴的属性
        data.setAxisYLeft(new Axis());                  //Y轴的属性
        mColumnChartCc.setColumnChartData(data);
        mColumnChartCc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void toggleLabels() {
        hasLabels = !hasLabels;
        if (hasLabels) {
            hasLabelForSelected = false;
            mColumnChartCc.setValueSelectionEnabled(hasLabelForSelected);
        }
        generateData();
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {
        @Override
        public void onValueSelected(int i, int i1, SubcolumnValue value) {
            Toast.makeText(ChartActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onValueDeselected() {
        }
    }
}
