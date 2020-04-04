import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements Runnable, Comparable {
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;
    private volatile boolean empty;
    private Thread t;

    public Queue(int nrClients){
        clients = new ArrayBlockingQueue<Client>(nrClients);
        waitingPeriod = new AtomicInteger(0);
        empty = true;
    }

    public void addClient(Client c) throws InterruptedException {
        clients.put(c);
        waitingPeriod.getAndAdd(c.getProcessingTime());
        c.setWaitedTime(waitingPeriod.intValue()); // Setam valoarea timpului de asteptat = timpul de procesare + timpul curent de asteptat in coada
        empty = false;
        if (clients.size() == 1){ // Pornim un thread nou daca size == 1 pentru ca asta inseamnca ca inainte de inserare coada era goala
            Thread t = new Thread(this); // adica threadul era mort
            t.start();
        }
    }

    public BlockingQueue<Client> getClients() {
        return clients;
    }

    @Override
    public void run() {
        Client c = null;
        while(!empty) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            c = clients.peek();
            if (c != null){
                waitingPeriod.decrementAndGet();
                if (c.getProcessingTime() - 1 == 0) {
                    try {
                        clients.take();
                        if (isEmpty())
                            empty = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else
                    c.setProcessingTime(c.getProcessingTime() - 1);
            }
        }
    }


    public void setWaitingPeriod(AtomicInteger waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public boolean isEmpty(){
        return clients.isEmpty();
    }

    @Override
    public int compareTo(Object o) {
        Queue q = (Queue) o;
        return Integer.compare(waitingPeriod.intValue(),q.waitingPeriod.intValue());
    }

    @Override
    public String toString() {
        return clients.toString();
    }

    public Thread createThread() {
        t = new Thread(this);
        return t;
    }
}
