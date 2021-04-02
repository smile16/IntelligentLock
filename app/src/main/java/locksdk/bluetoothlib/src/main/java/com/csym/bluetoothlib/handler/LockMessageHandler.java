package locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.handler;

import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.handler.bean.BaseData;
import locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.handler.bean.LockMessageData;

public class LockMessageHandler extends BaseDataHandler{
    public LockMessageHandler(String data) {
        super(data);
    }

    @Override
    protected BaseData resolve(int[] b) {
        return new LockMessageData(b);
    }
}
