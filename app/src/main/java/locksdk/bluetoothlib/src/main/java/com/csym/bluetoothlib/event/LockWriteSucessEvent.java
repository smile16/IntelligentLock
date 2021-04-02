package locksdk.bluetoothlib.src.main.java.com.csym.bluetoothlib.event;

public class LockWriteSucessEvent {
    public LockWriteSucessEvent(boolean isWriteSucess) {
        this.isWriteSucess = isWriteSucess;
    }

    private boolean isWriteSucess;

    public boolean isWriteSucess() {
        return isWriteSucess;
    }

    public void setWriteSucess(boolean writeSucess) {
        isWriteSucess = writeSucess;
    }
}
