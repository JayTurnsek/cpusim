import java.util.LinkedList;
import java.util.Queue;

public class FCFSReadyQueue {
    Queue<PCB> processes;

    public FCFSReadyQueue() {
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
