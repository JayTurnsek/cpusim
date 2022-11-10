import java.util.Arrays;

/*
 * PCB class implementation to hold data about any given job.
 * 
 * @param jobID: the specified ID of the process
 * @param state: current state of the process
 * @param pc: simulated program counter
 * @param burstCount: number of CPU bursts
 * @param curBurst: current CPU burst
 * @param ioComp: completion time of current I/O operation
 * @param report: holds statistics about this process in the simulation.
 */
public class PCB {
    int jobID;
    int arr;
    String state;
    int pc;
    int burstCount;
    int[] bursts;
    int curBurst;
    int ioComp;
    int cpuShots;

    public PCB(int jid, int arrival, int bCount, int[] bursts_arr) {
        jobID = jid;
        arr = arrival;
        state = "Ready";
        pc = 0;
        burstCount = bCount - 1;
        bursts = bursts_arr;
        curBurst = 0;
        ioComp = 0;
        cpuShots = 0;
    }

    public String toString() {
        return "Job ID: " + jobID + " Arrival: " + arr + " Bursts: " + Arrays.toString(bursts);
    }

    /*
     * Prints out current processes report to the console (completion time, waiting
     * time, turnaround time, and total cpu shots).
     * Also updates the full report to be sent out at the end of the simulation.
     * 
     * @param cpu is the cpu
     * 
     * @param fr is the full report object tracking aggregate report.
     */
    public void handleReports(CPU cpu, Report r) {

        // calculates important times from process simulation
        int compTime = cpu.getCounter();
        int turnaroundTime = cpu.getCounter() - this.arr;
        int waitTime = turnaroundTime - this.pc;

        // updates full report object
        r.addData(this.pc, waitTime, turnaroundTime, this.cpuShots);

        // Prep strings to be sent to console
        String s[] = {
                "JOB " + Integer.toString(this.jobID) + " DONE.",
                "Arr Time: " + Integer.toString(this.arr),
                "Comp Time: " + Integer.toString(compTime),
                "Proc Time: " + Integer.toString(this.pc),
                "Wait Time: " + Integer.toString(waitTime),
                "Turnaround Time: " + Integer.toString(turnaroundTime),
                "CPU shots:" + Integer.toString(this.cpuShots)
        };

        // Prints nicely formatted report with borders
        // Contains all fields of PCBReport object.
        System.out.println();
        System.out.println("-".repeat(145));
        System.out.printf(
                "| %-12s | %-16s | %-20s | %-16s | %-20s | %-25s | %-13s | %n",
                s[0], s[1], s[2], s[3], s[4], s[5], s[6]);
        System.out.println("-".repeat(145));
        System.out.println();
    }
}
