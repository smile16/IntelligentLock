package locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event;


import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.handler.bean.BaseData;

/**
 * Date: 17/11/2
 * Time: 11:41
 * Description: 蓝牙数据接收事件
 *
 * @author csym_ios_04.
 */

public class ReceiveDataEvent {
    private BaseData baseData;
    public ReceiveDataEvent(BaseData data) {
        this.baseData = data;
    }

    public BaseData getBaseData() {
        return baseData;
    }
}
