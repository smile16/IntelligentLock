package locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event;

import java.util.TreeSet;

import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.model.ExtendedBluetoothDevice;

/**
 * Date: 17/11/1
 * Time: 11:08
 * Description: 蓝牙设备搜索事件
 *
 * @author csym_ios_04.
 */

public class SearchDeviceEvent {
    private TreeSet<ExtendedBluetoothDevice> devices;
    public SearchDeviceEvent(TreeSet<ExtendedBluetoothDevice> devices) {
        this.devices = devices;
    }


    public TreeSet<ExtendedBluetoothDevice> getDevices() {
        return devices;
    }
}
