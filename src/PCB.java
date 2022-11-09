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
    PCBReport report;

    public PCB(int jid, int arrival, int bCount, int[] bursts_arr) {
        jobID = jid;
        arr = arrival;
        state = "Ready";
        pc = 0;
        burstCount = bCount - 1;
        bursts = bursts_arr;
        curBurst = 0;
        ioComp = 0;
        report = new PCBReport(jid, arrival);
    }

    public String toString() {
        return "Job ID: " + jobID + " Arrival: " + arr + " Bursts: " + Arrays.toString(bursts);
    }
}
