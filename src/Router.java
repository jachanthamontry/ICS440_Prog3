import java.util.LinkedList;

/*
 * This runnable routes packets as they traverse the network.
 */
public class Router implements Runnable {
    private LinkedList<Packet> list = new LinkedList<Packet>();
    private int routes[];
    private Router routers[];
    private int routerNum;
    private boolean end = false;
    private Packet currentP;
    
    Router(int rts[], Router rtrs[], int num) {
        routes = rts;
        routers = rtrs;
        routerNum = num;
    }
    /*
     * Add a packet to this router.  Add some details on how this works.
     */
    public void addWork(Packet p) {
    	synchronized(this) {
    		list.add(p);
    		notifyAll();
    	}
    	
    }
    /*
     * End the thread, once no more packets are outstanding.
     */
    public synchronized void end() {
    	
    }

    public synchronized void networkEmpty() {
    	end = true;
    	notify();
    }

    /*
     * Process packets.  Add some details on how this works.
     */
    public void run() {
    	
    	while(!end) {
    		synchronized(this ) {
	    		if(list.isEmpty()) {
	    			try {
	    				wait();
	    			} catch (InterruptedException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	        	}
    		}

    		currentP = list.poll();
     		if(this.routerNum != currentP.getDestination()) {
     			currentP.Record(this.routerNum);
    			//Packet newP = new Packet(this.routerNum, currentP.getDestination()); I dont think I would create a new packet. Should just forward to next router
    			int thing = routes[currentP.getDestination()];
    			
    			routers[thing].addWork(currentP);	
    		}
     			
     		else {
     			currentP.Record(this.routerNum);
     			System.out.println("getAndDecrementing: " + Routing.getPacketCount());
     			Routing.packetCount.getAndDecrement();
     		}
	    		
	    }//end of while(!end)
    	
    }//end of run
}
