package locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.handler.bean;

import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event.LockWriteSucessEvent;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.service.BluetoothCommands;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.utils.EventBusUtils;

public class LockMessageData extends BaseData {
    private boolean isWriteSucess;
    public LockMessageData(int[] data) {
        super(data);
    }

    @Override
    protected void handleData() {
        if (data[2]== BluetoothCommands.PRODUCT_ORDER_WRITE_SUCESS){
            isWriteSucess=true;
        }else if (data[2]!=-1){
            isWriteSucess=false;
        }

        EventBusUtils.post(new LockWriteSucessEvent(isWriteSucess));
    }
}
