import java.util.Comparator;
import java.util.PriorityQueue;

/*
 * Ready Queue implementation using Shortest Job (current burst time) First as the priority method for a priority queue.
 * 
 * @author Jay Turnsek
 * @date 2022-11-09
 * 
 * param processes Is the queue of processes.
 */
public class SJFReadyQueue {
    PriorityQueue<PCB> processes;

    public SJFReadyQueue() {
        processes = new PriorityQueue<PCB>(10, new PCBComparator());
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
            out += "Process " + Integer.toString(p.jobID) + "\n";
        }
        return out;
    }

}

/*
 * Implementation of Comparator class to control the priority criteria. In this
 * case, the priority is determined by the length of
 * the current burst the process is on.
 */
class PCBComparator implements Comparator<PCB> {

    /*
     * @returns 1 if p2 priority > p1 priority, -1 if p1 priority < p2 priority, and
     * 0 if p1 priority = p2 priority.
     */
    public int compare(PCB p1, PCB p2) {
        if (p1.bursts[p1.curBurst] > p2.bursts[p2.curBurst]) {
            return 1;
        } else if (p1.bursts[p1.curBurst] < p2.bursts[p2.curBurst]) {
            return -1;
        } else {
            return 0;
        }
    }
}
