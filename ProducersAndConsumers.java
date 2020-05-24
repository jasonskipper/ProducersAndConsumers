/* 
 *  Michael Skipper, N01162792 
 *  COP 4620, Operating Systems  
 *  Dr. Ahuja 
 *  
 *  This program creates producer and consumer threads, 
 *  where the producer "produces" by setting the elements of 
 *  an array of integers to the current count, a way of denoting 
 *  that the element is "FULL," and where the consumer "consumes" 
 *  by setting the elements of the array of integers to -1, a way 
 *  of denoting that the element is "EMPTY." 
 *  
 *  Note: 
 *  ------ 
 *  The Producer threads do not produce past the end nor beginning of the buffer. 
 *  The Consumer threads do not consume past the end nor beginning of the buffer. 
 *  
 *  The Producer thread does unfortunately still write to an array element that is not EMPTY, or set to -1. 
 *  The Consumer thread also unfortunately still sets elements that are not FULL to EMPTY. 
 *  
 *  wait() and notify() are used to avoid errors and ensure thread synchronization 
 *  
 *  
 *   
 *  References: 
 *  ------------  
 *  https://www.youtube.com/watch?v=A1tnVMpWHh8&t=774s  
 *  https://www.geeksforgeeks.org/producer-consumer-solution-using-threads-java/
 *  https://www.geeksforgeeks.org/inter-thread-communication-java/
 *  https://www.baeldung.com/java-wait-notify 
 *  Operating Systems Concepts with Java, 8th edition (Silberschatz, Galvin, and Gagne, John Wiley Publishers) 
 *  Dr. Ahuja's PowerPoint slides 
 */
public class FinalP1OS {
	public static void main(String[] args) {
		
		BoundedBuffer boundedbuffer = new BoundedBuffer();
		Thread thread1 = new Thread(new Producer(boundedbuffer), "Producer");
		//Thread thread3 = new Thread(new Producer(boundedbuffer), "Producer");
		//Thread thread4 = new Thread(new Producer(boundedbuffer), "Producer");
		Thread thread2 = new Thread(new Consumer(boundedbuffer), "Consumer");
		thread1.start();
		thread2.start();

		
	}

}

// this class is based on slide 3.33 and page 118 of the book 
// "the following variables reside in a region of memory shared by the producer and consumer processes: " 
class BoundedBuffer {
	
	private static final int BUFFER_SIZE = 5; // size of the shared buffer, #define BUFFER_SIZE 10  
	private int count; // number of items in the buffer 
	private int in; // points to the next free position 
	private int out; // points to the next full position 
	private int[] buffer; // array of integers 
	public BoundedBuffer() { 
		// buffer is initially empty 
		count = 0;
		in = 0;
		out = 0;
		buffer = new int[BUFFER_SIZE];	
	}
	
	// producers calls this method 
	public void insert(int item) throws InterruptedException {
		// Figure 3.16 
		synchronized(buffer) {
			while(count == BUFFER_SIZE) {
				//; // do nothing -- no free buffers 
				buffer.wait();
				System.out.println("Producer waiting, buffer is full. ");	
			}
			// add an item to the buffer 
			++count;
			buffer[in] = item;
			System.out.println();
			System.out.println("Produced: " + buffer[in]);
			System.out.println("buffer as of recent insertion: ");
			for(int x = 0; x < count; x++) {
				System.out.print(buffer[x] + " ");
			}
			in = (in + 1) % BUFFER_SIZE;
			buffer.notifyAll();
		}
	}
	
	public int remove() throws InterruptedException {
		// Figure 3.17 
		synchronized(buffer) {
			int item;
			while(count == 0) {
				buffer.wait();
				System.out.println("Consumer waiting, buffer is empty. ");
			}
			// remove an item from the buffer 
			--count;
			item = buffer[out];
			buffer[out] = -1;
			System.out.println();
			System.out.println("Consumed: " + item);
			//
			System.out.println();
			System.out.println("buffer after recent removal: ");
			for(int y = 0; y < count; y++) {
				System.out.println(buffer[y] + " ");
			}
			//
			out = (out + 1) % BUFFER_SIZE;
			buffer.notifyAll();
			return item;
			
		}
		
	}
}

class Producer implements Runnable {
	BoundedBuffer boundedbuffer;
	
	public Producer(BoundedBuffer boundedbuffer) {
		this.boundedbuffer = boundedbuffer;
	}
	public void run() {
		int i = 0;
		while(true) {
			try {
				this.boundedbuffer.insert(i);
				i++;
				Thread.sleep(500);
			} catch(InterruptedException e) {
				
			}
		}
	}
	
	
}

class Consumer implements Runnable {
	BoundedBuffer boundedbuffer;
	
	public Consumer(BoundedBuffer boundedbuffer) {
		this.boundedbuffer = boundedbuffer;
	}
	public void run() {
		while(true) {
			try {
				this.boundedbuffer.remove();
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				
			}
		}
	}
}
