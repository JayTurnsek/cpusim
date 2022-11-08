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

    // prints report of terminated process
    public void prepReport(CPU cpu) {
        this.compTime = cpu.getCounter();
        this.turnaroundTime = cpu.getCounter() - this.arrTime;
        this.waitTime = this.turnaroundTime - this.procTime;
    }

    public void print() {
        String s[] = {
                "JOB " + Integer.toString(this.jobID) + " DONE.",
                "Arr Time: " + Integer.toString(this.arrTime),
                "Comp Time: " + Integer.toString(this.compTime),
                "Proc Time: " + Integer.toString(this.procTime),
                "Wait Time: " + Integer.toString(this.waitTime),
                "Turnaround Time: " + Integer.toString(this.turnaroundTime),
                "CPU shots:" + Integer.toString(this.cpuShots)
        };
        System.out.println();
        System.out.println("-".repeat(145));
        System.out.printf(
                "| %-12s | %-16s | %-20s | %-16s | %-20s | %-25s | %-13s | %n",
                s[0], s[1], s[2], s[3], s[4], s[5], s[6]);
        System.out.println("-".repeat(145));
        System.out.println();
    }

}
