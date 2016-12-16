package server;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

/**
 * Created by a.olins on 15/12/2016.
 */
public class StateTest {

    StateWrapper fs = new StateWrapper();


    ServerStats s = new SmartSS();


    Integer mx1 = new Integer(1);
    Integer mx2 = new Integer(2);


    AtomicBoolean brkGetEntered = new AtomicBoolean(false);
    AtomicBoolean brkSetterThreadEntered = new AtomicBoolean(false);
    AtomicBoolean brkSetEntered = new AtomicBoolean(false);
    AtomicBoolean brkSetFinished = new AtomicBoolean(false);
    AtomicBoolean brkSetterThreadReleased = new AtomicBoolean(false);
    AtomicBoolean brkSetterReleasedMarker = new AtomicBoolean(false);
    AtomicBoolean brkGetterReleased = new AtomicBoolean(false);
    AtomicBoolean brkSetterFinished = new AtomicBoolean(false);

    boolean assertion = false;

    Thread t, t2, manager;

    @Test
    public void t() throws Exception {

        fs.add(s);
        t = new Thread(new Runnable() {
            public void run() {
                StateEnum se = fs.get("");
                assertion = StateEnum.READY == se;
//            synchronized (mx2){mx2.notify();
            }
        });

        t2 = new Thread(new Runnable() {
            public void run() {
                synchronized (mx2) {
                    brkSetterThreadEntered.set(true);
                    try {
                        mx2.wait();
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
                brkSetterThreadReleased.set(true);
                fs.set("", StateEnum.PAUSED);
                brkSetterFinished.set(true);
            }
        });

        manager = new Thread(new Runnable() {
            public void run() {

                while (!(
                          brkGetEntered.get()
                       && brkSetterThreadEntered.get()
                       && brkSetEntered.get()
                       && brkSetFinished.get()
                       && brkSetterThreadReleased.get()
                       && brkSetterFinished.get()
                       && brkSetterReleasedMarker.get()
                       && brkGetterReleased.get()
                )) { // all breakpoints passed
                    if (brkGetEntered.get() && brkSetterThreadEntered.get() ) {
                        // setter thread is before SET waiting; getter thread inside get method waiting; and we have never released setter thread
                        synchronized (mx2) {mx2.notify(); }
                        brkSetterReleasedMarker.set(true);
                    }

                    if (brkSetterThreadReleased.get() && brkSetEntered.get() && brkSetFinished.get() && brkSetterFinished.get() && !brkGetterReleased.get()) {
                        //Setter thread is over; and we have never released getter
                        synchronized (mx1) {mx1.notify();}
                        brkGetterReleased.set(true);
                    }

                    if (brkSetterThreadReleased.get() && t2.getState() == Thread.State.BLOCKED) {
                        //if setter state is blocked passed the breakpoint 5 it means that it is blocked by sync block. which is good
                        // release the getter hounds.
                        synchronized (mx1) {mx1.notify();}
                        brkGetterReleased.set(true);
                    }


                }
            }
        });


        manager.start(); t.start(); t2.start();
        try{ manager.join(); t.join();t2.join();
        } catch (InterruptedException e){System.out.println(e);}

        assertTrue(assertion);
    }


    class SmartSS extends ServerStats {
        @Override
        public StateEnum getSt() {
            brkGetEntered.set(true);
            synchronized (mx1) {try {mx1.wait();} catch (InterruptedException e) { System.out.println(e);}}
            return se;
        }

        @Override
        public void setSt(StateEnum e) {
            brkSetEntered.set(true);
            se = e;
            brkSetFinished.set(true);
        }

        private StateEnum se = StateEnum.READY;
    }

}


