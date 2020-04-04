public class Client implements Comparable{
    private int ID;
    private int arrivalTime;
    private int processingTime;
    private int waitedTime = 0;

    public Client(int ID, int arrivalTime, int processingTime){
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.processingTime = processingTime;
    }

    public int getWaitedTime() {
        return waitedTime;
    }

    public void setWaitedTime(int waitedTime) {
        this.waitedTime = waitedTime;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    @Override
    public int compareTo(Object o) {
        Client c = (Client) o;
        return Integer.compare(arrivalTime, c.arrivalTime);
    }

    public String toString(){
        return "( " + ID + ", " + arrivalTime + ", " + processingTime + " )";
    }
}
