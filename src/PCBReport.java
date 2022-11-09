/*
 * Data class to hold statistics about specific process in simulation.
 * 
 * @param jobID: job ID of corresponding process
 * @param arrTime: time of arrival of process
 * @param compTime: time process completed fully
 * @param procTime: total processing time of process
 * @param waitTime: total time spent waiting in simulation
 * @param turnaroundTime: total turnaround time of process
 * @param cpuShots: number of times process had access to the cpu.
 */

public class PCBReport {
    int jobID;
    int arrTime;
    int compTime;
    int procTime;
    int waitTime;
    int turnaroundTime;
    int cpuShots;

    public PCBReport(int jid, int arrival) {
        jobID = jid; // done
        arrTime = arrival; // done
        compTime = 0; // done
        procTime = 0; // done
        waitTime = 0;
        turnaroundTime = 0; // done
        cpuShots = 0; // done
    }

    /*
     * Finishes up filling report with calculations that had to wait until last
     * burst was completed.
     * 
     * @param cpu is the cpu.
     */
    public void prepReport(CPU cpu) {
        this.compTime = cpu.getCounter();
        this.turnaroundTime = cpu.getCounter() - this.arrTime;
        this.waitTime = this.turnaroundTime - this.procTime;
    }

    /*
     * Prints complete report of job once it has completed; intended to be used
     * after last burst finishes processing.
     */
    public void print() {

        // Prep strings to be sent to console
        String s[] = {
                "JOB " + Integer.toString(this.jobID) + " DONE.",
                "Arr Time: " + Integer.toString(this.arrTime),
                "Comp Time: " + Integer.toString(this.compTime),
                "Proc Time: " + Integer.toString(this.procTime),
                "Wait Time: " + Integer.toString(this.waitTime),
                "Turnaround Time: " + Integer.toString(this.turnaroundTime),
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
