import java.util.LinkedList;
import java.util.Queue;

/*
 * Ready Queue implementation using simple FIFO queue; holds processes in waiting for CPU.
 * Self explanitory methods have no description.
 * 
 * param processes Is the queue of processes.
 */
public class FIFOReadyQueue {
    Queue<PCB> processes;

    public FIFOReadyQueue() {
        processes = new LinkedList<>();
    }

    public void addProcess(PCB proc) {
        this.processes.add(proc);
    }

    public int getSize() {
        return this.processes.size();
    }

    public PCB getNext() {
        return this.processes.remove();
    }

    public String toString() {
        String out = "";
        for (PCB p : this.processes) {
            out += p.toString() + " | curBurst : " + Integer.toString(p.curBurst) + "\n";
        }
        return out;
    }
}
