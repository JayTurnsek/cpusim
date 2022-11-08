import java.util.LinkedList;
import java.util.Queue;

public class BlockedQueue {
    Queue<PCB> processes;

    public BlockedQueue() {
        processes = new LinkedList<>();
    }

    public void addProcess(PCB proc) {
        this.processes.add(proc);
    }

    public boolean isReady(int cpu_time) {
        return cpu_time >= processes.peek().ioComp;
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
