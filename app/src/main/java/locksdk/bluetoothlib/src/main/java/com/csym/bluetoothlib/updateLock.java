package locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.locks.Lock;

import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event.LockWriteSucessEvent;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.handler.bean.LockMessageBean;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.handler.bean.LockMessageData;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.helper.BluzScanHelper;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.helper.IBluzScanHelper;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.interf.BlueToothListener;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.service.BluetoothCommands;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.service.TransferDataService;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.utils.Utils;

public class updateLock implements IBluzScanHelper.OnConnectionListener{
    private Context context;
    private BlueToothListener blueToothListener=null;
    private static updateLock updatelock;
    private updateLock(){}

    public static updateLock getInstance(Context context) {
//        EventBusUtils.register(context);
        if (updatelock == null) {
            updatelock = new updateLock();
        }
        return updatelock;
    }

    //用于更新锁信息
    public  boolean connectLockBlueTooth(String deviceAddress, Context context, BlueToothListener listener,LockMessageBean lockMessageBean) {
        if (lockMessageBean.getNAME().getBytes().length>16){
            return false;
        }
        if (lockMessageBean.getAPID().length()>13){
            return false;
        }
        if (!Utils.HeartIsLegal(lockMessageBean.getHBT()+"")){
            return false;
        }
        if (!Utils.HeartIsLegal(lockMessageBean.getMWT()+"")){
            return false;
        }
        if (!Utils.adressIsLegal(lockMessageBean.getADDR()+"")||lockMessageBean.getADDR().length()>8){
            return false;
        }
        if (!Utils.adressIsLegal(lockMessageBean.getCH()+"")||lockMessageBean.getCH().length()>4){
            return false;
        }
        this.context=context;
        this.blueToothListener=listener;
        BluzScanHelper.getInstance(context).setOnConnectionListener(this);
        //连接蓝牙
        boolean connect = BluzScanHelper.getInstance(context).connect(deviceAddress);
        return true;
    }

    //蓝牙连接成功之后方可进行数据传输
    public void sendLockMessage(LockMessageBean lockMessageBean){
                BluetoothCommands.writeLockSetting(lockMessageBean);
    }


    @Override
    public void onConnected(BluetoothDevice device) {
        Log.e("Anubis","updateLock回调回来了");
        Intent intent = new Intent(context, TransferDataService.class);
        context.startService(intent);
        //蓝牙连接成功的回调
        blueToothListener.onBlueToothConnectSucess();

    }

    @Override
    public void onDisconnected(BluetoothDevice device) {
        blueToothListener.onBlueToothConnectFail();
    }
}
