package com.zkxl.intelligentlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event.ConnectSucessEvent;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event.ConnectionEvent;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event.LockWriteSucessEvent;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event.SearchDeviceEvent;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.handler.bean.LockMessageBean;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.interf.BlueToothListener;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.model.ExtendedBluetoothDevice;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.service.ConnectionService;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.updateLock;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.utils.EventBusUtils;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.utils.SharedPrefUtils;

public class MainActivity extends AppCompatActivity implements BlueToothListener {
    /**
     * ?????????????????????????????????????????????
     */
    ArrayList<BluetoothDevice> normalDevices = new ArrayList<>();
    private RecyclerView recyclerView;
    private FindBandRecycleViewAdapter mAdapter;
    private EditText stationName;
    private EditText stationNumber;
    private AppCompatSpinner lockFuction;
    private AppCompatSpinner lockSpeed;
    private EditText heartbeatInterval;
    private EditText openLockTime;
    private EditText lockAdress;
    private EditText frequencyPoint;
    private static final String[] fuction = {"??????????????????", "????????????????????????"};
    private static final String[] speed = {"2MBPS", "1MBPS"};
    private String lockFuctionS;
    private String lockSpeedS;
    private String lockName;
    private String lockNumber;
    private String heartIntervalS;
    private String openLockTimeS;
    private String lockAdressS;
    private String frequencyPointS;
    private LockMessageBean lockMessageBean;
    private ContentLoadingProgressBar progressCircular;
    private RelativeLayout progress;
    private Timer timer;
    private boolean isSendMessageSucess=false;
    private ProgressBar progressHorizontal;
    private TextView showLockState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView();
        initData();
        initListener();
        EventBusUtils.register(this);
        initRequestPermission();
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLockStationState("????????????????????????");
                progressHorizontal.setIndeterminate(true);
                progressHorizontal.setVisibility(View.VISIBLE);
                normalDevices.clear();
                if (mAdapter!=null){
                    mAdapter.notifyDataSetChanged();
                }
                EventBusUtils.post(new ConnectionEvent(ConnectionEvent.SEARCH_ACTION));
            }
        });
    }

    private void initListener() {
        lockFuction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Anubis", fuction[position]);
                lockFuctionS=(position+1)+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lockSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Anubis", speed[position]);
                lockSpeedS=(position)+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initData() {
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fuction);
        ArrayAdapter<String> speedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, speed);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        lockFuction.setAdapter(nameAdapter);
        lockSpeed.setAdapter(speedAdapter);
        lockFuction.setVisibility(View.VISIBLE);
        lockSpeed.setVisibility(View.VISIBLE);
    }

    private void initView() {
        recyclerView = findViewById(R.id.show_lock_recycle);
        stationName = findViewById(R.id.station_name);
        stationNumber = findViewById(R.id.station_number);
        lockFuction = findViewById(R.id.lock_fuction);
        lockSpeed = findViewById(R.id.lock_speed);
        heartbeatInterval = findViewById(R.id.heartbeat_interval);
        openLockTime = findViewById(R.id.open_lock_time);
        lockAdress = findViewById(R.id.lock_adress);
        frequencyPoint = findViewById(R.id.frequency_point);
        progressCircular = findViewById(R.id.progress_circular);
        progress = findViewById(R.id.progress);
        progressHorizontal = findViewById(R.id.progress_horizontal);
        showLockState = findViewById(R.id.show_lock_state);
        progressCircular.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.red), PorterDuff.Mode.MULTIPLY);
//        progressHorizontal.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.red), PorterDuff.Mode.MULTIPLY);
        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        String userStationName = SharedPrefUtils.INSTANCE.getUserStationName(this);
        String userStationNumber = SharedPrefUtils.INSTANCE.getUserStationNumber(this);
        String userWorkMode = SharedPrefUtils.INSTANCE.getUserWorkMode(this);
        String userWorkSpeed = SharedPrefUtils.INSTANCE.getUserWorkSpeed(this);
        String userWorkHeart = SharedPrefUtils.INSTANCE.getUserWorkHeart(this);
        String userWorkOpenLockTime = SharedPrefUtils.INSTANCE.getUserWorkOpenLockTime(this);
        String userLockAdress = SharedPrefUtils.INSTANCE.getUserLockAdress(this);
        String userFrequencyPoint = SharedPrefUtils.INSTANCE.getUserFrequencyPoint(this);
        setViewText(stationName,userStationName);
        setViewText(stationNumber,userStationNumber);
        setSpinnerView(lockFuction,userWorkMode);
        setSpinnerView(lockSpeed,userWorkSpeed);
        setViewText(heartbeatInterval,userWorkHeart);
        setViewText(openLockTime,userWorkOpenLockTime);
        setViewText(lockAdress,userLockAdress);
        setViewText(frequencyPoint,userFrequencyPoint);
    }


    private void setViewText(EditText view,String s){
        if (!TextUtils.isEmpty(s)){
            view.setText(s);
        }
    }

    private void setSpinnerView(AppCompatSpinner view,String s){
        if (!TextUtils.isEmpty(s)){
            view.setSelection(Integer.parseInt(s));
        }
    }

    private void setLockStationState(String state){
        showLockState.setText("?????????????????????"+state);
    }

    private void initRequestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(granted -> {
            if (granted) {
//                EventBusUtils.post(new ConnectionEvent(ConnectionEvent.SEARCH_ACTION));
                Intent intent = new Intent(this, ConnectionService.class);
                startService(intent);
            } else {
                Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*******??????*****/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchDeviceEvent(SearchDeviceEvent deviceEvent) {
        //??????????????????,?????????
        TreeSet<ExtendedBluetoothDevice> deviceSet = deviceEvent.getDevices();
        normalDevices.clear();
        for (ExtendedBluetoothDevice device : deviceSet) {
            normalDevices.add(device.device);
            dealDevices(normalDevices);
        }
    }


    /**
     * ???????????????
     *
     * @param normalDevices ?????????????????????
     */
    private void dealDevices(ArrayList<BluetoothDevice> normalDevices) {
//        tvSearching.setText("??????????????????????????????????????????");
        initRecycle();
        onAdapterOnListener();
    }


    private void onAdapterOnListener() {
        mAdapter.setOnItemClick(new FindBandRecycleViewAdapter.OnItemClick() {
            @Override
            public void onItemClick(BluetoothDevice device) {
                progressHorizontal.setVisibility(View.GONE);
                progressHorizontal.setIndeterminate(false);
                lockName = stationName.getText().toString();
                lockNumber = stationNumber.getText().toString();
                heartIntervalS = heartbeatInterval.getText().toString();
                openLockTimeS = openLockTime.getText().toString();
                lockAdressS = lockAdress.getText().toString();
                frequencyPointS = frequencyPoint.getText().toString();
                saveStationSetting();
                if (!TextUtils.isEmpty(lockName)&&!TextUtils.isEmpty(lockNumber)&&!TextUtils.isEmpty(heartIntervalS)
                &&!TextUtils.isEmpty(openLockTimeS)&&!TextUtils.isEmpty(lockAdressS)&&!TextUtils.isEmpty(frequencyPointS)
                        &&!TextUtils.isEmpty(lockFuctionS)&&!TextUtils.isEmpty(lockSpeedS)
                ){
                    progress.setVisibility(View.VISIBLE);
                    lockMessageBean = new LockMessageBean(lockName,lockNumber,(byte) Integer.parseInt(lockFuctionS,16),
                            (byte)Integer.parseInt(lockSpeedS,16),(byte)Integer.parseInt(heartIntervalS,16),(byte)Integer.parseInt(openLockTimeS,16),
                            lockAdressS,frequencyPointS
                            );
                    boolean b = updateLock.getInstance(MainActivity.this).connectLockBlueTooth(device.getAddress(), MainActivity.this, MainActivity.this, lockMessageBean);
                    if (!b){
                        progress.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,"????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                    }else {
                        //?????????????????????
                        setLockStationState("????????????????????????");
                        timer = new Timer();
                        timer.schedule(new RemindTask(),15*1000);
                    }

                }else {
                    Toast.makeText(getBaseContext(), "????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveStationSetting() {
        if (!TextUtils.isEmpty(lockName)){
            SharedPrefUtils.INSTANCE.saveUserStationName(MainActivity.this,lockName);
        }
        if (!TextUtils.isEmpty(lockNumber)){
            SharedPrefUtils.INSTANCE.saveUserStationNumber(MainActivity.this,lockNumber);
        }
        if (!TextUtils.isEmpty(lockFuctionS)){
            SharedPrefUtils.INSTANCE.saveUserWorkMode(MainActivity.this,lockFuctionS);
        }
        if (!TextUtils.isEmpty(lockSpeedS)){
            SharedPrefUtils.INSTANCE.saveUserWorkSpeed(MainActivity.this,lockSpeedS);
        }
        if (!TextUtils.isEmpty(heartIntervalS)){
            SharedPrefUtils.INSTANCE.saveUserHeart(MainActivity.this,heartIntervalS);
        }
        if (!TextUtils.isEmpty(openLockTimeS)){
            SharedPrefUtils.INSTANCE.saveUserOpenLockTime(MainActivity.this,openLockTimeS);
        }
        if (!TextUtils.isEmpty(lockAdressS)){
            SharedPrefUtils.INSTANCE.saveUserLockAdress(MainActivity.this,lockAdressS);
        }
        if (!TextUtils.isEmpty(frequencyPointS)){
            SharedPrefUtils.INSTANCE.saveUserFrequencyPoint(MainActivity.this,frequencyPointS);
        }
    }

    private void initRecycle() {
        if (recyclerView != null) {
            if (mAdapter == null) {
                mAdapter = new FindBandRecycleViewAdapter(this, normalDevices);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(mAdapter);
            //??????????????????Adapter???Item?????????????????????RecyclerView?????????????????????????????????true???RecyclerView???????????????????????????
            recyclerView.setHasFixedSize(true);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);
    }


    /*******??????*****/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWriteResult(LockWriteSucessEvent lockWriteSucessEvent) {
        isSendMessageSucess=true;
        progress.setVisibility(View.GONE);

        if (lockWriteSucessEvent.isWriteSucess()) {
            //????????????
            setLockStationState("??????????????????");
            Log.e("Anubis", "??????????????????");
            Toast.makeText(this,"??????????????????",Toast.LENGTH_SHORT).show();
            EventBusUtils.post(new ConnectionEvent(ConnectionEvent.DIS_CONNECTION_ACTION));
        } else {
            //????????????
            Log.e("Anubis", "??????????????????");
            Toast.makeText(this,"??????????????????",Toast.LENGTH_SHORT).show();
            setLockStationState("??????????????????");
            EventBusUtils.post(new ConnectionEvent(ConnectionEvent.DIS_CONNECTION_ACTION));
        }

    }

    /*******??????*****/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlueToothConnectState(ConnectSucessEvent connectSucessEvent) {
        Log.e("Anubis", "post????????????????????????");
        updateLock.getInstance(this).sendLockMessage(lockMessageBean);
    }

    @Override
    public void onBlueToothConnectSucess() {
        Log.e("Anubis", "?????????????????????");
        setLockStationState("??????????????????");
        EventBusUtils.post(new ConnectionEvent(ConnectionEvent.CANCEL_SEARCH_ACTION));
    }

    @Override
    public void onBlueToothConnectFail() {
        //???????????????????????????
        progress.setVisibility(View.GONE);
        setLockStationState("?????????????????????");
        Log.e("Anubis", "?????????????????????");
    }

    class RemindTask extends TimerTask{

        @Override
        public void run() {
            if (!isSendMessageSucess){
                setLockStationState("??????????????????");
                EventBusUtils.post(new ConnectionEvent(ConnectionEvent.CANCEL_SEARCH_ACTION));
            }
            timer.cancel();
        }
    }

    private void forStopServices(){
        Intent intent=new Intent(this,ConnectionService.class);
        stopService(intent);
    }

}