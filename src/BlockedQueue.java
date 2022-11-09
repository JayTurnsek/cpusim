import java.util.LinkedList;
import java.util.Queue;

/*
 * Data structure used to hold processes waiting for I/O. Typical FIFO queue used for implementation.
 * Ommitted method headers that are self explanitory.
 */
public class BlockedQueue {
    Queue<PCB> processes;

    public BlockedQueue() {
        processes = new LinkedList<>();
    }

    public void addProcess(PCB proc) {
        this.processes.add(proc);
    }

    /*
     * Used to check if the current process at the top of the queue has completed
     * it's I/O operation (assumed to be 10 time units)
     * 
     * @param cpuTime the current time of the cpu counter
     * 
     * @returns True if the I/O process is complete, False otherwise.
     */
    public boolean isReady(int cpuTime) {
        return cpuTime >= processes.peek().ioComp;
    }

    public PCB peek() {
        return this.processes.peek();
    }

    public PCB getNext() {
        return this.processes.remove();
    }

    public int getSize() {
        return this.processes.size();
    }

    public String toString() {
        String out = "";
        for (PCB p : this.processes) {
            out += p.toString() + " | curBurst : " + Integer.toString(p.curBurst) + " | ioComp : "
                    + Integer.toString(p.ioComp) + "\n";
        }
        return out;
    }

}
