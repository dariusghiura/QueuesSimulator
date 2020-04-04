import java.io.*;
import java.util.*;

public class SimulationManager implements Runnable {
    private int maxSimulationTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int minProcessingTime;
    private int maxProcessingTime;
    private int nrClients;
    private int nrQueues;

    private File inputFile;
    private File outputFile;

    private List<Client> generatedClients;
    private Scheduler scheduler;
    
    private int totalWaitedTime = 0;

    public SimulationManager(String input, String output){
        inputFile = new File(input);
        outputFile = new File(output);
        readFromFile();
        generateNRandomClients();
        scheduler = new Scheduler(nrQueues, nrClients - 1);
    }

    private void readFromFile(){
        try (Scanner scan = new Scanner(inputFile)) {
            nrClients = Integer.parseInt(scan.nextLine());
            nrQueues = Integer.parseInt(scan.nextLine());
            maxSimulationTime = Integer.parseInt(scan.nextLine());
            String[] arrival = scan.nextLine().split(",");
            minArrivalTime = Integer.parseInt(arrival[0]);
            maxArrivalTime = Integer.parseInt(arrival[1]);
            String[] processing = scan.nextLine().split(",");
            minProcessingTime = Integer.parseInt(processing[0]);
            maxProcessingTime = Integer.parseInt(processing[1]);
        }catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void generateNRandomClients(){
        generatedClients = Collections.synchronizedList(new ArrayList<Client>(nrClients));
        for (int i = 0; i < nrClients; i++) {
            int arrTime = (int) (Math.random() * maxArrivalTime);
            int procTime = (int) (Math.random() *maxProcessingTime);
            if (arrTime < minArrivalTime) // Math.random poate returna 0
                arrTime += minArrivalTime; // In acest caz adunam timpul minim de arrival
            if (procTime < minProcessingTime) // Math.random poate returna 0
                procTime += minProcessingTime; // In acest caz adunam timpul minim de procesare 
            generatedClients.add(new Client(i+1, arrTime, procTime));
        }
        Collections.sort(generatedClients); // Sortam lista de clienti crescator dupa timpul de arrival
    }


    private void sendClient(int currentTime) {
    	for (Iterator i = generatedClients.iterator() ; i.hasNext();) {
            Client c = (Client) i.next();
            if (currentTime == c.getArrivalTime()){
                try {
                    scheduler.dispatchClient(c);
                    totalWaitedTime += c.getWaitedTime();
                    i.remove();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    	}
    }
    
    
    private void appendFile(FileWriter writer, int currentTime) {
    	try {
            writer.append("Time " + currentTime + "\n");
            writer.append("Clients waiting : " + generatedClients.toString() + "\n");
            writer.append(scheduler.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        int currentTime = 0;
        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (currentTime <= maxSimulationTime){
        	sendClient(currentTime);
        	appendFile(writer,currentTime);
            if (generatedClients.isEmpty() && scheduler.areQueuesEmpty()) // Oprim simularea daca nu mai avem clienti
                break;
            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.append("Average waiting time : " + totalWaitedTime / (float) nrClients);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SimulationManager manager = new SimulationManager(args[0], args[1]);
        Thread t = new Thread(manager);
        t.start();
    }
}
