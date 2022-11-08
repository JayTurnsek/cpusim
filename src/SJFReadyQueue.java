import java.util.Comparator;
import java.util.PriorityQueue;

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

class PCBComparator implements Comparator<PCB> {
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
