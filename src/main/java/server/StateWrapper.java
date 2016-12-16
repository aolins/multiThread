package server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a.olins on 15/12/2016.
 */
public class StateWrapper {

    private Map<String, ServerStats> map = new HashMap<String, ServerStats>();

    public StateWrapper(){
        map.put("1", new ServerStats());
        map.put("2", new ServerStats());
    }

    public synchronized void set(String s, StateEnum e){
        map.get(s).setSt(e);
    }

    public synchronized StateEnum get(String s){
       return map.get(s).getSt();
    }

    public void add(ServerStats s ){
        map.put("",s);
    }
}
