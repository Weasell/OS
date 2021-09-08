  
//Implementation of Producer and Consumer problem
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class PC {
    public static void main(String[] arg) {

        myObj PC = new myObj();
        Semaphore sem_p = new Semaphore(15); 
        Semaphore sem_c = new Semaphore(0);
        Semaphore mutex = new Semaphore(1);

        Thread P = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PC.Producer(sem_p, sem_c, mutex);
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
                    PC.Consumer(sem_p, sem_c, mutex);
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

class myObj {
    List<String> buffer = new LinkedList<>();
    int count = 0;
    public void Producer(Semaphore sem_p, Semaphore sem_c, Semaphore mutex) throws InterruptedException {
        int head_ptr = 0;
        try {
            File myObj = new File("pc-input.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                sem_p.acquire();
                mutex.acquire();
                    String data = myReader.nextLine();
                    System.out.println("Producer: "  + data + " head_ptr: " + head_ptr);
                    buffer.add(data);
                    head_ptr++;          
                    // Thread.sleep(100);
                    if(!myReader.hasNextLine()){
                        count = head_ptr;
                        System.out.println("---Producer end---");
                    }
                mutex.release();
                sem_c.release();               
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }        
    }

    public void Consumer(Semaphore sem_p, Semaphore sem_c, Semaphore mutex) throws InterruptedException {
        int tail_ptr = 0;
        while (true) {
            if(tail_ptr == count && count != 0) break;
            sem_c.acquire();
            mutex.acquire();
                String val = buffer.get(tail_ptr);
                System.out.println("Consumer: " + val + " tail_ptr: " + tail_ptr);
                tail_ptr++;          
                // Thread.sleep(100);            
            mutex.release();
            sem_p.release();         
        }
        System.out.println("---Consumer end---");
    }
}
  

