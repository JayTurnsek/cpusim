/*
 * CPU class to hold the current process and the overall processing counter.
 * 
 * @author Jay Turnsek
 * @date 2022-11-09
 */
public class CPU {
    PCB curProcess;
    int counter;
    int deadline;
    int jobsCompleted;

    /*
     * @param curProcess Denotes process currently being used by the CPU; null if
     * none
     * 
     * @param counter The current time of the CPU since simulation start
     * 
     * @param deadline The deadline of the currently running process; could be end
     * of burst or end of time quantum.
     * 
     * @param jobsCompleted Tracks how many jobs the CPU has completed since
     * simulation start
     */
    public CPU() {
        curProcess = null;
        counter = 0;
        deadline = 0;
        jobsCompleted = 0;
    }

    public void pushProcess(PCB proc) {
        curProcess = proc;
    }

    public boolean isFree() {
        return curProcess == null;
    }

    public void timeStep() {
        this.counter++;
    }

    public int getCounter() {
        return counter;
    }

    /*
     * Sets the deadline of the current process
     * 
     * @param d Deadline to be set
     */
    public void setDeadline(int d) {
        this.deadline = d;
    }

    /*
     * Checks if the current burst/quantum has finished, if there is a process
     * present in the CPU.
     * 
     * @returns True if the process is finished, False if not.
     */
    public boolean isComplete() {
        return this.counter >= this.deadline && curProcess != null;
    }

}
