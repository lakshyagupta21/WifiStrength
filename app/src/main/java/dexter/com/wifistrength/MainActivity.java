package dexter.com.wifistrength;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Handler;

import com.github.lzyzsd.randomcolor.RandomColor;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private List<WifiSignal> legendList = new ArrayList<>();
    private LegendAdapter mAdapter ;
    RecyclerView recyclerView;
    GraphView graphView ;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    StringBuilder sb = new StringBuilder();
    int[] color ;
    HashMap<String,WifiSignal> connections ;
    private final Handler handler = new Handler();
    int j  = 0;
    int time = 0 ;
    private Runnable mTimer1;
    HashMap<String,LineGraphSeries<DataPoint>> plot ;
    int position = 0 ;
    RandomColor randomColor ;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphView = (GraphView) findViewById(R.id.graph);
        graphView.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.white));
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.white));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.white));
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Signal Strength(db)");
        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(getResources().getColor(R.color.white));
        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(getResources().getColor(R.color.white));
        graphView.getGridLabelRenderer().setPadding(100);
        graphView.getLegendRenderer().setSpacing(20);
        connections = new HashMap<>();
        plot = new HashMap<>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mAdapter = new LegendAdapter(legendList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        // Returns an array of 10 random color values
        randomColor = new RandomColor();
        color = randomColor.randomColor(20);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(mainWifi.isWifiEnabled()==false)
        {
            mainWifi.setWifiEnabled(true);
        }
        doInback();
    }

    public void doInback()
    {
        handler.postDelayed(new Runnable() {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                receiverWifi = new WifiReceiver();
                registerReceiver(receiverWifi, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mainWifi.startScan();
                doInback();
            }
        }, 1000);

    }
    @Override
    protected void onPause()
    {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
             mTimer1 = new Runnable() {
            @Override
            public void run() {
                Iterator it = connections.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    WifiSignal wifiSignal = (WifiSignal) pair.getValue();
                    if(plot.containsKey(pair.getKey())) {
                        LineGraphSeries<DataPoint> line = plot.get(pair.getKey());
                        line.appendData(new DataPoint(time , wifiSignal.level ), false , 10);
                    }else{
                        LineGraphSeries<DataPoint> line = new LineGraphSeries<>();
                        graphView.addSeries(line);
                        line.setColor(wifiSignal.color);
                        line.appendData(new DataPoint(time , wifiSignal.level ), false , 10);
                        legendList.add(wifiSignal);
                        System.out.println(legendList.size());
                        mAdapter.notifyDataSetChanged();
                        plot.put((String) pair.getKey(),line);
                    }
                }
                time ++;
                handler.postDelayed(this, 1000);
            }
        };
       handler.postDelayed(mTimer1, 1000);

        super.onResume();
    }

    class WifiReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            //For Remove
            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            ArrayList<String> names = new ArrayList<>();
            for(int h = 0 ; h < wifiList.size() ; h++){
                names.add(wifiList.get(h).SSID);
            }
            for(int i = 0; i < wifiList.size(); i++)
            {
//                int channelNo ;
                int freq = wifiList.get(i).level;
                int l = wifiList.get(i).frequency;
                // Displays Channel Number best for transmission
                l = convertFrequencyToChannel(l);
                if(!connections.containsKey(wifiList.get(i).SSID)){
                    if(j == color.length - 1){
                        color = randomColor.randomColor(20);
                        j = 0 ;
                    }
                    connections.put(wifiList.get(i).SSID,new WifiSignal(wifiList.get(i).SSID , color[j++],wifiList.get(i).level));
                }else{
                    WifiSignal s = connections.get(wifiList.get(i).SSID);
                    s.setLevel(freq);
                }
            }
           // System.out.println(connections);



        }
    }

    public static int convertFrequencyToChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }



}