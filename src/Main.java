import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Main method for carrying out CPU scheduling algorithm simulation. Command line args are as follows:
 * algorithm[quantum] filename
 * I decided to implement this by keeping a CPU time count that iterates one by one instead of incrementing it based
 * on the current burst length. I figured that made more sense in my mind, thus was easier to implement. It sort of takes
 * on a more GA style simulation.
 * 
 * @author Jay Turnsek
 * @date 2022-11-09
 * 
 * @param algorithm Denotes algorithm to be used
 * @param quantum is time quantum to be used if round robin selected
 * @param filename is the name of the file with the job data ***MUST BE IN SAME DIRECTORY***
 */
public class Main {
    public static void main(String[] args) throws Exception {

        // checks command line args and sends them to parameter map
        HashMap<String, String> params = handleArgs(args);

        // get data from the text file
        Queue<PCB> jobQueue = getQueueFromFile(params.get("filename"));

        // init full report object
        Report report;
        if (params.get("algorithm").equals("RR")) {
            report = new Report(params.get("algorithm"), jobQueue.size(), Integer.parseInt(params.get("quantum")));
        } else {
            report = new Report(params.get("algorithm"), jobQueue.size());
        }

        // prints header of labels for intermediate reports (every 200 time steps)
        intermediateHeader();

        // select algorithm to be used
        switch (params.get("algorithm")) {
            case ("FCFS"):
                FCFS(jobQueue, report);
                break;
            case ("SJF"):
                SJF(jobQueue, report);
                break;
            case ("RR"):
                RR(jobQueue, report, Integer.parseInt(params.get("quantum")));
                break;
        }

        // prints aggregate report
        report.print();

    }

    /*
     * Simulates the First-Come-First-Serve scheduling algorithm, where processes
     * are sent to the CPU
     * in the order they arrive in. Jobs are held in a ready queue, then sent to CPU
     * to complete the current burst,
     * Then sent to a blocking queue where they wait to complete an I/O operation.
     * Reports are printed to the console
     * every 200 time steps, and when a process is finished completely.
     * 
     * @param processes Is the job queue of jobs that need to pass through the
     * simulation.
     * 
     * @param report Is the aggregate report object that holds final statistics
     * from the entire simulation.
     */
    static void FCFS(Queue<PCB> processes, Report report) {

        // Initialize queues and cpu
        FIFOReadyQueue readyQueue = new FIFOReadyQueue();
        BlockedQueue blockedQueue = new BlockedQueue();
        CPU cpu = new CPU();

        // populate jobQueue with first 10 processes
        for (int i = 0; i < 10; i++) {
            PCB cur = processes.remove();
            readyQueue.addProcess(cur);
        }

        // Repeat until all jobs processed and terminated.
        while (jobsPresent(readyQueue, blockedQueue, cpu)) {

            // checks if CPU free, then add next process in the queue
            if (cpu.isFree()) {
                loadProcess(readyQueue.getNext(), cpu);
            }

            // check if cpu is done current burst
            if (cpu.isComplete()) {

                // add to processing time
                cpu.curProcess.pc += cpu.curProcess.bursts[cpu.curProcess.curBurst];

                // Check if this was it's last burst
                if (cpu.curProcess.curBurst == cpu.curProcess.burstCount) {

                    // Process is complete, print the report and update the jobs completed
                    cpu.curProcess.handleReports(cpu, report);
                    cpu.jobsCompleted++;

                    // add another process to ready queue
                    populateQueue(processes, readyQueue, cpu);
                }

                // send to blockedqueue, for I/O
                else {
                    sendToBlockedQueue(cpu.curProcess, blockedQueue, cpu.getCounter());
                }

                // clear process
                cpu.curProcess = null;
            }

            // check that top of blocked queue is ready
            checkBlockedQueue(blockedQueue, readyQueue, cpu);

            // 200 level reports
            if (cpu.getCounter() % 200 == 0) {
                intermediateReport(readyQueue, blockedQueue, cpu);
            }

            // iterate processing counter
            cpu.timeStep();
        }

        // NOTE: do i keep? ask
        intermediateReport(readyQueue, blockedQueue, cpu);

        report.finalTime = cpu.getCounter() - 1;
    }

    /*
     * Simulates the Shortest-Job-First scheduling algorithm, where processes are
     * arranged in the readyQueue
     * in the order of shortest current burst first, then sent to CPU to complete
     * the current burst,
     * then sent to a blocking queue where they wait to complete an I/O operation.
     * Reports are printed to the console
     * every 200 time steps, and when a process is finished completely.
     * 
     * @param processes Is the job queue of jobs that need to pass through the
     * simulation.
     * 
     * @param report Is the aggregate report object that holds final statistics
     * from the entire simulation.
     */
    static void SJF(Queue<PCB> processes, Report report) {

        // Initialize queues and cpu
        SJFReadyQueue readyQueue = new SJFReadyQueue();
        BlockedQueue blockedQueue = new BlockedQueue();
        CPU cpu = new CPU();

        // populate jobQueue with first 10 processes
        for (int i = 0; i < 10; i++) {
            PCB cur = processes.remove();
            readyQueue.addProcess(cur);
        }

        // Repeat until all jobs processed and terminated.
        while (jobsPresent(readyQueue, blockedQueue, cpu)) {

            // checks if CPU free, then add next process in the queue
            if (cpu.isFree()) {
                if (readyQueue.getSize() > 0) {
                    loadProcess(readyQueue.getNext(), cpu);
                }

            }

            // check if cpu is done current burst
            if (cpu.isComplete()) {

                // add to processing time
                cpu.curProcess.pc += cpu.curProcess.bursts[cpu.curProcess.curBurst];

                if (cpu.curProcess.curBurst == cpu.curProcess.burstCount) {

                    // Process is complete, print the report and update the jobs completed
                    cpu.curProcess.handleReports(cpu, report);
                    cpu.jobsCompleted++;

                    // add another process to ready queue
                    populateQueue(processes, readyQueue, cpu);
                }

                // send to blockedqueue, for I/O
                else {
                    sendToBlockedQueue(cpu.curProcess, blockedQueue, cpu.getCounter());
                }

                // clear process
                cpu.curProcess = null;
            }

            // check that top of blocked queue is ready
            checkBlockedQueue(blockedQueue, readyQueue, cpu);

            // 200 level reports
            if (cpu.getCounter() % 200 == 0) {
                intermediateReport(readyQueue, blockedQueue, cpu);
            }

            // iterate processing counter
            cpu.timeStep();
        }

        // NOTE: do i keep? ask
        intermediateReport(readyQueue, blockedQueue, cpu);

        report.finalTime = cpu.getCounter() - 1;
    }

    /*
     * Simulates the round robin scheduling algorithm, where processes are arranged
     * in the ready queue
     * in FCFS, then allocated to the CPU for it's current burst until either the
     * time quantum has been reached
     * or the current burst has finished; whichever comes first. Then, the process
     * is sent to the blocked queue
     * to wait for an IO operation if it's current burst is finished, or sent to the
     * back of the ready queue if not.
     * This process repeats until the process is finished all bursts, then another
     * process is added from the job queue
     * until completed.
     * 
     * @param processes Is the job queue of jobs that need to pass through the
     * simulation.
     * 
     * @param report Is the aggregate report object that holds final statistics
     * from the entire simulation.
     * 
     * @param quantum Is the time quantum used to control CPU utilization time
     * periods.
     */
    static void RR(Queue<PCB> processes, Report report, int quantum) {

        // Initialize queues and cpu
        FIFOReadyQueue readyQueue = new FIFOReadyQueue();
        BlockedQueue blockedQueue = new BlockedQueue();
        CPU cpu = new CPU();
        int inc = 0;

        // populate jobQueue with first 10 processes
        for (int i = 0; i < 10; i++) {
            PCB cur = processes.remove();
            readyQueue.addProcess(cur);
        }

        // Repeat until all jobs processed and terminated.
        while (jobsPresent(readyQueue, blockedQueue, cpu)) {

            // checks if cpu is free and that there processes in ready queue, then sends to
            // processor
            if (cpu.isFree()) {
                if (readyQueue.getSize() > 0) {

                    // gets the processing time for current burst relative to quantum
                    // and loads process into cpu.
                    inc = loadProcessRR(readyQueue.getNext(), cpu, quantum);
                }
            }

            // checks if cpu completed current job
            if (cpu.isComplete()) {

                // add to processing time
                cpu.curProcess.pc += inc;
                // handle decrement based on time quantum
                cpu.curProcess.bursts[cpu.curProcess.curBurst] -= inc;

                // check to see if process has completed current burst
                if (cpu.curProcess.bursts[cpu.curProcess.curBurst] == 0) {
                    // process has completed all bursts, discard
                    if (cpu.curProcess.curBurst == cpu.curProcess.burstCount) {

                        // Process is complete, print the report and update the jobs completed
                        cpu.curProcess.handleReports(cpu, report);
                        cpu.jobsCompleted++;

                        // add another process from job queue
                        populateQueue(processes, readyQueue, cpu);
                    }
                    // process has not completed all bursts, send to blockedQueue
                    else {
                        sendToBlockedQueue(cpu.curProcess, blockedQueue, cpu.getCounter());
                    }
                } else {
                    // send to back of ready queue
                    readyQueue.addProcess(cpu.curProcess);
                }
                // clear process
                cpu.curProcess = null;

            }

            // check if top of blocked queue is ready to go back to readyQueue
            checkBlockedQueue(blockedQueue, readyQueue, cpu);

            // iterate processing counter
            cpu.timeStep();

            // 200 level reports
            if (cpu.getCounter() % 200 == 0) {
                intermediateReport(readyQueue, blockedQueue, cpu);
            }
        }

        // NOTE: do i keep? ask
        intermediateReport(readyQueue, blockedQueue, cpu);

        report.finalTime = cpu.getCounter() - 1;

    }

    /*
     * Prints report to console every 200 time units containing the current time,
     * processes in ready queue,
     * processes in blocked queue, and jobs completed.
     * 
     * @param ready is the ready queue
     * 
     * @param blocked is the blocked queue
     * 
     * @param cpu is the CPU object
     */
    static void intermediateReport(Object ready, BlockedQueue blocked, CPU cpu) {

        // i hate this.
        String size;
        if (ready instanceof FIFOReadyQueue) {
            size = Integer.toString(((FIFOReadyQueue) ready).getSize());
        } else if (ready instanceof SJFReadyQueue) {
            size = Integer.toString(((SJFReadyQueue) ready).getSize());
        } else {
            size = "";
        }

        // print report
        System.out.printf(
                "| %-7s | %-7s | %-7s | %-7s |%n",
                Integer.toString(cpu.getCounter()), size, Integer.toString(blocked.getSize()),
                Integer.toString(cpu.jobsCompleted));
    }

    /*
     * Prints the header for the intermediate reports on time, ready queue
     * processes, blocked
     * queue proccesses, and jobs completed.
     */
    static void intermediateHeader() {
        System.out.printf(
                "| %-7s | %-7s | %-7s | %-7s |%n",
                "TIME", "READY", "BLOCKED", "COMP");
    }

    /*
     * Converts text file of jobs to a job queue to be used in a scheduling
     * simulation.
     * 
     * @param fname is the name of the file to be used
     * 
     * @returns queue object holding the processes used in the simulation. In
     * numerical order from 0-n.
     */
    static Queue<PCB> getQueueFromFile(String fname) throws FileNotFoundException {
        Queue<PCB> queue = new LinkedList<>();

        // Read file
        File readObj = new File(fname);
        Scanner reader = new Scanner(readObj);
        while (reader.hasNextLine()) {

            // get data to integer array, send to PCB object
            String data = reader.nextLine();
            int[] out = Stream.of(data.split(" ")).mapToInt(Integer::parseInt).toArray();
            PCB cur = new PCB(out[0], out[1], out[2], Arrays.copyOfRange(out, 3, out.length));

            // add to list of processors
            queue.add(cur);
        }
        reader.close();

        return queue;
    }

    /*
     * Command line argument handler, to ensure legal arguments are entered in the
     * console.
     * 
     * @param args is the string args directly from the console
     * 
     * @returns out is the hashmap containing all the needed arguments to complete
     * the simulation.
     */
    static HashMap<String, String> handleArgs(String[] args) throws Exception {

        HashMap<String, String> out = new HashMap<String, String>();

        // This ensures that both that there is the right amount of arguments, and that
        // there is only 2 if it is not round
        // robin, and only 3 if it is round robin.
        if (args.length == 2) {
            if (args[0].equals("RR")) {
                throw new Exception("Illegal number of arguments. Input should be: algorithm[quantum] filename");
            }
            out.put("algorithm", args[0]);
            out.put("filename", args[1]);
        } else if (args.length == 3) {
            out.put("algorithm", args[0]);
            out.put("quantum", args[1]);
            out.put("filename", args[2]);
        } else {
            throw new Exception("Illegal number of arguments. Input should be: algorithm[quantum] filename");
        }

        // Ensures that a legal algorithm is entered in the algorithm field.
        if (!(args[0].equals("FCFS") | args[0].equals("SJF") | args[0].equals("RR"))) {
            throw new Exception("Illegal algorithm. Choose from FCFS, SJF, RR.");
        }

        return out;
    }

    /*
     * Checks that there are still jobs present in the ecosystem of ready queue,
     * blocked queue, and CPU.
     * Intended to be used to break the loop and confirm the simulation is fully
     * complete.
     * 
     * @param readyQueue is the ready queue
     * 
     * @param blockedQueue is the blocked queue
     * 
     * @param cpu is the cpu
     * 
     * @returns True if a job exists in at least one of the three places, False
     * otherwise.
     */
    static boolean jobsPresent(Object readyQueue, BlockedQueue blockedQueue, CPU cpu) {

        // handling the case of FCFS vs SJF as the queuing criteria
        if (readyQueue instanceof FIFOReadyQueue) {
            return !(((FIFOReadyQueue) readyQueue).getSize() == 0 && blockedQueue.getSize() == 0 && cpu.isFree());
        } else {
            return !(((SJFReadyQueue) readyQueue).getSize() == 0 && blockedQueue.getSize() == 0 && cpu.isFree());
        }
    }

    /*
     * Adds a process from the job queue to the ready queue. Used when a process has
     * completely
     * finished and left the simulation ecosystem.
     * 
     * @param jobQueue the job queue
     * 
     * @param readyQueue the ready queue
     * 
     * @param cpu the cpu
     */
    static void populateQueue(Queue<PCB> jobQueue, Object readyQueue, CPU cpu) {

        // checks that job queue is not empty and is ready to enter based on arrival
        // time
        if (jobQueue.size() > 0) {
            if (jobQueue.peek().arr <= cpu.getCounter()) {

                // handling the case of FCFS vs SJF as the queuing criteria,
                // then populating the ready queue with a new job from job queue
                if (readyQueue instanceof FIFOReadyQueue) {
                    ((FIFOReadyQueue) readyQueue).addProcess(jobQueue.remove());
                } else {
                    ((SJFReadyQueue) readyQueue).addProcess(jobQueue.remove());
                }
            }
        }
    }

    /*
     * Sends a process that has just finished a burst (but not its last) to the
     * blocked queue
     * to wait for I/O.
     * 
     * @param process is the process being sent to the blocked queue
     * 
     * @param blockedQueue is the blocked queue
     * 
     * @param time is the current time on the CPU.
     */
    static void sendToBlockedQueue(PCB process, BlockedQueue blockedQueue, int time) {

        // Update process parameters, to ensure it runs next burst and waits to complete
        // I/O operation.
        process.curBurst++;
        process.ioComp = time + 10;
        process.state = "Blocked";

        // adds to blocked queue
        blockedQueue.addProcess(process);
    }

    /*
     * Loads a process into the CPU in the FCFS and SJF algorithms.
     * 
     * @param process is the process to be loaded
     * 
     * @param cpu is the cpu that the process is being loaded into
     */
    static void loadProcess(PCB process, CPU cpu) {

        // push process to the cpu; update needed parameters
        cpu.pushProcess(process);
        cpu.curProcess.state = "Running";
        cpu.curProcess.cpuShots++;

        // Sets deadline to the completion time of the current burst
        cpu.setDeadline(cpu.counter + cpu.curProcess.bursts[cpu.curProcess.curBurst]);
    }

    /*
     * Loads a process into the CPU in the RR algorithm. Different to the above
     * function,
     * here we set the increment to the minimum of the time quantum and the time
     * left on the
     * current burst. The deadline will now be the current time + increment.
     * 
     * @param process is the process to be loaded
     * 
     * @param cpu is the cpu that the process is being loaded into
     * 
     * @param quantum is the time quantum of the RR algorithm being used.
     * 
     * @returns inc which is the increment of the deadline, representing the
     * processing time of this CPU shot.
     */
    static int loadProcessRR(PCB process, CPU cpu, int quantum) {

        int increment = Math.min(quantum, process.bursts[process.curBurst]);
        cpu.pushProcess(process);
        cpu.setDeadline(cpu.getCounter() + increment);
        cpu.curProcess.cpuShots++;
        return increment;

    }

    /*
     * Checks for available processes in the blocked queue that have completed I/O,
     * that
     * are ready to be sent back to the ready queue.
     * 
     * @param blockedQueue is the blocked queue
     * 
     * @param readyQueue is the ready queue
     * 
     * @param cpu is the cpu
     */
    static void checkBlockedQueue(BlockedQueue blockedQueue, Object readyQueue, CPU cpu) {

        // check if a process is available and ready from blocked queue
        if (blockedQueue.getSize() > 0) {
            if (blockedQueue.isReady(cpu.getCounter())) {

                // set state of process
                blockedQueue.peek().state = "Ready";

                // add to processing time from blockedQueue
                blockedQueue.peek().pc += 10;

                // add back to ready queue
                // handling the case of FCFS vs SJF as the queuing criteria
                if (readyQueue instanceof FIFOReadyQueue) {
                    ((FIFOReadyQueue) readyQueue).addProcess(blockedQueue.getNext());
                } else {
                    ((SJFReadyQueue) readyQueue).addProcess(blockedQueue.getNext());
                }

                // Set ioComp time for next process in line, if it exists.
                // This is important and I missed it on my first try, as the I/O job doesn't
                // take
                // place until a process has reached the top of the blocked queue.
                if (blockedQueue.getSize() > 0) {
                    blockedQueue.processes.peek().ioComp = cpu.getCounter() + 10;
                }
            }
        }
    }
}
