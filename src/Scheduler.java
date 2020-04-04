import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Scheduler {
    private List<Queue> queues;
    private int nrQueues;
    private int nrMaxClientsQueue;

    public Scheduler(int nrQueues, int nrMaxClientsQueue){
        queues = Collections.synchronizedList(new ArrayList<Queue>(nrQueues));
        for (int i = 0; i < nrQueues; i++) {
            queues.add(new Queue(nrMaxClientsQueue));
        }
        for (Queue q : queues) {
            Thread t = q.createThread();
            t.start(); // Pornim threadurile pentru cozi
        }
    }

    public void dispatchClient(Client c) throws InterruptedException {
        Queue qmin = null;
        int min = Integer.MAX_VALUE;
        for (Queue q : queues) {
            if (q.getWaitingPeriod().intValue() < min){
                min = q.getWaitingPeriod().intValue();
                qmin = q; // Salvam coada care are timpul de asteptare cel mai mic
            }
        }
        if (qmin != null)
            qmin.addClient(c);
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public boolean areQueuesEmpty(){
        for (Queue q : queues) {
            if (!q.isEmpty()) // Daca exista o coada care nu e goala returnam false
                return false;
        }
        return true;
    }

    public String toString(){
        String s ="";
        int i = 0;
        for (Queue q : queues) {
            i++;
            if (q.isEmpty())
                s += "Queue " + i + " : closed\n";
            else
                s += "Queue " + i + " : " + q.toString() + " Waiting Period : " + q.getWaitingPeriod() + "\n";
        }
        return s;
    }
}
