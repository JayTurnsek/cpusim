/*
 * CPU class to hold the current process and the overall processing counter.
 */
public class CPU {
    PCB curProcess;
    int counter;
    int deadline;
    int jobsCompleted;

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

    public void setDeadline(int d) {
        this.deadline = d;
    }

    public boolean isComplete() {
        return this.counter >= this.deadline && curProcess != null;
    }



}
