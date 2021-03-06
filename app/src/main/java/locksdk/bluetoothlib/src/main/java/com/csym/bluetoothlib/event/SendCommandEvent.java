package locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event;

import android.util.Log;

/**
 * Date: 17/11/1
 * Time: 15:12
 * Description: 蓝牙命令发送事件
 *
 * @author csym_ios_04.
 */

public class SendCommandEvent {
    private byte[] commands;

    public SendCommandEvent(byte[] commands) {
        this.commands = commands;
    }

    public byte[] getCommands() {
        return commands;
    }
}
