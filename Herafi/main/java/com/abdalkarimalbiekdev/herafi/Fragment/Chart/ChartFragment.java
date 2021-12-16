package com.abdalkarimalbiekdev.herafi.Fragment.Chart;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Common.MyAxisValueFormatter;
import com.abdalkarimalbiekdev.herafi.ProjectDetails;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.databinding.FragmentChartBinding;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.networkModel.YearRequests;
import com.abdalkarimalbiekdev.herafi.service.CheckNewRequests;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart lineChart;
    private HerafiAPI api;

    FragmentChartBinding binding;

    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_chart , container , false);

        lineChart = binding.chartCountRequests;

        api = Common.getAPI();

        boolean dark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        binding.swipeChart.setProgressBackgroundColorSchemeResource(dark? R.color.black_item : R.color.white_item);
        binding.swipeChart.setColorSchemeColors(
                getResources().getColor(R.color.primary_color),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
        );
        binding.swipeChart.setOnRefreshListener(() -> {

            if (Common.isConnectionToInternet(getActivity())){

                getData();
                binding.swipeChart.setRefreshing(false);
            }
            else {
                Toast.makeText(getActivity(), this.getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
                configNoInternet();
                binding.swipeChart.setRefreshing(false);
                return;
            }

        });


        if (Common.isConnectionToInternet(container.getContext())){

            getData();
        }
        else{
            configNoInternet();
            Toast.makeText(container.getContext(), getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
        }


        return binding.getRoot();

    }

    private void configNoData() {
        binding.imgBackground.setImageResource(R.drawable.no_data);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.no_data_to_show));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.chartCountRequests.setVisibility(View.GONE);
    }

    private void configGetData() {
        binding.imgBackground.setVisibility(View.GONE);
        binding.textBackground.setVisibility(View.GONE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.chartCountRequests.setVisibility(View.VISIBLE);
    }

    private void configSomeProblem() {
        binding.imgBackground.setImageResource(R.drawable.some_problem);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.some_problem));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.chartCountRequests.setVisibility(View.GONE);
    }

    private void configNoInternet() {
        binding.imgBackground.setImageResource(R.drawable.no_internet);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.you_need_internet));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.chartCountRequests.setVisibility(View.GONE);
    }


    private void getData() {

        try {
            String token = Hawk.get("TOKEN");
            String year = AES.encrypt("2021");

            Log.d("enc_year" , year);
            Log.d("enc_year" , Common.craftmanData.getId());


            new CompositeDisposable().add(
                    api.getYearRequests("Bearer " + token,
                            Common.craftmanData.getId(),
                            year)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listResultModel -> {

                        if (listResultModel != null){
                            if (!listResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String errorMessage = AES.decrypt(listResultModel.getErrorMessage());
                                    Log.d("error_message_here" , errorMessage);
                                    Toasty.error(getActivity(), errorMessage, Toast.LENGTH_SHORT , true).show();
                                    configSomeProblem();

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                    configSomeProblem();
                                }

                            }
                            if (listResultModel.getResponse().getResult().size() <= 0){
                                configNoData();
                            }
                            else if (!listResultModel.getResponse().getToken().isEmpty()){

                                configGetData();

                                //Save the token
                                Hawk.put("TOKEN" , listResultModel.getResponse().getToken());

                                //decrypt data
                                try {
                                    decryptData(listResultModel.getResponse().getResult());
                                    Log.d("get_it_data" , "OK");
                                }
                                catch (Exception e) {
                                    configSomeProblem();
                                    Toasty.error(getActivity() , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                    e.printStackTrace();
                                }

                            }
                            else {

                                configGetData();

                                //decrypt data
                                try {
                                    decryptData(listResultModel.getResponse().getResult());
                                    Log.d("get_it_data" , "OK");
                                }
                                catch (Exception e) {
                                    configSomeProblem();
                                    Toasty.error(getActivity() , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                    e.printStackTrace();
                                }
                            }
                        }

                    }, throwable -> {
                        configSomeProblem();
                        Log.d("error_chart" , throwable.getMessage());
                    })
            );

        }
        catch (Exception e){
            e.printStackTrace();
            configSomeProblem();
            Log.d("error_aes" , e.getMessage());
        }


    }

    private void decryptData(List<YearRequests> data) {

        Log.d("data_enc" , data.size() + "");
        Log.d("data_enc" , data.get(0).getData() + "");

        for (int i = 0; i < data.size(); i++) {

            try {
                data.get(i).setMonth(AES.decrypt(data.get(i).getMonth()));
                data.get(i).setData(AES.decrypt(data.get(i).getData()));
            }
            catch (Exception e) {
                e.printStackTrace();
                configSomeProblem();
            }
        }

        Log.d("data_enc" , data.size() + "");
        Log.d("data_enc" , data.get(0).getData() + "");

        setupChart(data);
    }

    private void setupChart(List<YearRequests> chartData) {

        lineChart.setOnChartGestureListener(this);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);


        LimitLine upperLimit = new LimitLine(8f , getResources().getString(R.string.chart_limit_cool));
        upperLimit.setLineWidth(2f);
        upperLimit.enableDashedLine(10f , 10f , 0f);
        upperLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upperLimit.setTextSize(15f);
        upperLimit.setLineColor(android.R.color.holo_green_light);
        upperLimit.setTextColor(android.R.color.holo_green_light);


        LimitLine lowerLimit = new LimitLine(4f , getResources().getString(R.string.chart_limit_bad));
        upperLimit.setLineWidth(2f);
        upperLimit.enableDashedLine(10f , 10f , 10f);
        upperLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        upperLimit.setTextSize(15f);
        upperLimit.setLineColor(android.R.color.holo_red_light);
        upperLimit.setTextColor(android.R.color.holo_red_light);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upperLimit);
        leftAxis.addLimitLine(lowerLimit);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setTextColor(android.R.color.holo_red_light);//R.attr.text_title
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f , 10f , 0f);
        leftAxis.setDrawGridLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);

        //Fill data
        ArrayList<Entry> data = new ArrayList<>();

        boolean hasDataInMonth = false;

        for (int i = 1; i <= 12; i++) {
            for (int j = 0; j < chartData.size(); j++) {

                if (Integer.parseInt(chartData.get(j).getMonth()) == i){
                    data.add(new Entry(i ,Float.parseFloat(chartData.get(j).getData())));
                    Log.d("data_added" + i , chartData.get(j).getData() + "");
                    hasDataInMonth = true;
                    break;
                }

            }
            if (!hasDataInMonth){
                data.add(new Entry(i , 0));
            }
            hasDataInMonth = false;
        }

//        ArrayList<Entry> data = new ArrayList<>();
//        data.add(new Entry(1 , Float.parseFloat(chartData.get(0).getData())));
//        data.add(new Entry(2 , Float.parseFloat(chartData.get(1).getData())));
//        data.add(new Entry(3 , Float.parseFloat(chartData.get(2).getData())));
//        data.add(new Entry(4 , Float.parseFloat(chartData.get(3).getData())));
//        data.add(new Entry(5 , Float.parseFloat(chartData.get(4).getData())));
//        data.add(new Entry(6 , Float.parseFloat(chartData.get(5).getData())));
//        data.add(new Entry(7 , Float.parseFloat(chartData.get(6).getData())));
//        data.add(new Entry(8 , Float.parseFloat(chartData.get(7).getData())));
//        data.add(new Entry(9 , Float.parseFloat(chartData.get(8).getData())));
//        data.add(new Entry(10 , Float.parseFloat(chartData.get(9).getData())));
//        data.add(new Entry(11 , Float.parseFloat(chartData.get(10).getData())));
//        data.add(new Entry(12 , Float.parseFloat(chartData.get(11).getData())));


        LineDataSet dataSet = new LineDataSet(data , getResources().getString(R.string.chart_requests_per_month));
        dataSet.setFillAlpha(110);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLUE);

        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(dataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);



        String [] values = new String[12];
        for (int i = 0; i < 12 ; i++) {
            values[i] = String.valueOf(i + 1);
        }
//        String [] values = new String[]{
//                chartData.get(0).getMonth(),
//                chartData.get(1).getMonth(),
//                chartData.get(2).getMonth(),
//                chartData.get(3).getMonth(),
//                chartData.get(4).getMonth(),
//                chartData.get(5).getMonth(),
//                chartData.get(6).getMonth(),
//                chartData.get(7).getMonth(),
//                chartData.get(8).getMonth(),
//                chartData.get(9).getMonth(),
//                chartData.get(10).getMonth(),
//                chartData.get(11).getMonth()};

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new MyAxisValueFormatter(values));
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);

    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}