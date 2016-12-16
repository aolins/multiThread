package server;

/**
 * Created by a.olins on 15/12/2016.
 */
public class ServerStats {

    private StateEnum st = StateEnum.READY;

    public StateEnum getSt() {
        return st;
    }

    public void setSt(StateEnum st) {
        this.st = st;
    }
}
