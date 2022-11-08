import java.util.Arrays;

/*
 * PCB class implementation to hold data about any given job.
 * jobID:       the specified ID of the job
 * state: 0:    ready, 1: running, 2: blocked
 * pc:          simulated program counter
 * burstCount:  number of CPU bursts
 * curBurst:    current CPU burst
 * ioComp:      completion time of current I/O operation
 */
public class PCB {
    int jobID;
    int arr;
    int state;
    int pc;
    int burstCount;
    int[] bursts;
    int curBurst;
    int ioComp;
    PCBReport report;

    public PCB(int jid, int arrival, int bCount, int[] bursts_arr) {
        jobID = jid;
        arr = arrival;
        state = 0;
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
