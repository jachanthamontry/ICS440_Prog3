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
    private boolean networkEmpty = false;
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
    	synchronized(this.list) {
    		list.add(p);
    		this.list.notifyAll();
    	}
    	
    }
    /*
     * End the thread, once no more packets are outstanding.
     */
    public synchronized void end() {
    	end = true;
    	synchronized(this.list) {
    		this.list.notifyAll();
    	}
    }

    public synchronized void networkEmpty() {
    	networkEmpty = true;
    }

    /*
     * Process packets.  Add some details on how this works.
     */
    public void run() {	
    	while(!(end && networkEmpty && this.list.isEmpty())) {
    		synchronized(this.list ) {
	    		//If the list is empty, end is false, and network empty is false, then wait. 
    			if(list.isEmpty()) {
	    			try {
	    				this.list.wait();
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
	        	}
	    		
    			//if the list is empty and end and networkempty are true, return.
	    		if(this.list.size() == 0 && this.end && this.networkEmpty) {
	    			return;
	    		}
	    		
	    		//If the list is not empty it will pull the first item in the list and record the path. Then it will check to 
	    		//see if the destination is the same as the current thread and if true, it will dec the packetCount.

	    		currentP = list.poll();
	    		currentP.Record(this.routerNum);
	    		if(this.routerNum == currentP.getDestination()) {	
	     			Routing.decPacketCount();
	    		}
    		}

    		//if the currentPacket's destination is not the same as the thread, then it will retrieve the value of the next router and forward it to that one. 
     		if(this.routerNum != currentP.getDestination()) {
    			int thing = routes[currentP.getDestination()];
    			routers[thing].addWork(currentP);	
    		}	    		
	    }    	
    }
}
