
//Implementation of Producer and Consumer problem
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class ProducerConsumer {
    public static void main(String[] arg) {

        PCObj PC = new PCObj();
        Semaphore sem_p = new Semaphore(10); 
        Semaphore sem_c = new Semaphore(0);
        Thread P = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PC.Producer(sem_p, sem_c);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        Thread C = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PC.Consumer(sem_p, sem_c);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        P.start();
        C.start();
        try {
            P.join();
            C.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}

class PCObj {
    List<String> buffer = new LinkedList<>();
    Semaphore sem_p;
    Semaphore sem_c;
    boolean flag = false;
    public void Producer(Semaphore sem_p, Semaphore sem_c) throws InterruptedException {
        int head_ptr = 0;
        try {
            File myObj = new File("pc-input.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                sem_p.acquire();
                    synchronized (this) {
                        String data = myReader.nextLine();
                        System.out.println("Producer: " + data + " head_ptr: " + head_ptr);
                        buffer.add(data);
                        head_ptr++;
                        // Thread.sleep(100);
                    }
                sem_c.release();
            }
            flag = true;
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void Consumer(Semaphore sem_p, Semaphore sem_c) throws InterruptedException {
        int tail_ptr = 0;
        while (true && !flag) {
            sem_c.acquire();
                synchronized (this) {
                    String val = buffer.get(tail_ptr);
                    System.out.println("Consumer: " + val + " tail_ptr: " + tail_ptr);
                    tail_ptr++;          
                    // Thread.sleep(100);
                }
            sem_p.release();         
        }
    }
}
