import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println(args[0]);

        HashMap<String, String> params = handleArgs(args);

        // get data from the text file
        Queue<PCB> jobQueue = getQueueFromFile(params.get("filename"));
        
        // init full report object
        FullReport fr;
        if (params.get("algorithm").equals("RR")) {
            fr = new FullReport(params.get("algorithm"), jobQueue.size(), Integer.parseInt(params.get("quantum")));
        }
        else {
            fr = new FullReport(params.get("algorithm"), jobQueue.size());
        }
        
        // prints header of labels for intermediate reports
        intermediateHeader();

        // select algorithm to be used
        switch (params.get("algorithm")) {
            case ("FCFS"): 
                FCFS(jobQueue, fr);
                break;
            case ("SJF"):
                SJF(jobQueue, fr);
                break;
            case ("RR"):
                RR(jobQueue, fr, Integer.parseInt(params.get("quantum")));
                break;
            default:
                throw new Exception("Illegal scheduling algorithm.");
        }

        // prints aggregate report
        fr.print();

    }
    
    static void FCFS(Queue<PCB> processes, FullReport fullReport) {
        
        // Initialize queues and cpu
        FCFSReadyQueue readyQueue = new FCFSReadyQueue();
        BlockedQueue blockedQueue = new BlockedQueue();
        CPU cpu = new CPU();

        // populate jobQueue with first 10 processes
        for(int i = 0; i < 10; i++) {
            PCB cur = processes.remove();
            readyQueue.addProcess(cur);
        }

        // Repeat until all jobs processed and terminated.
        while (!(readyQueue.getSize() == 0 && blockedQueue.getSize() == 0 && cpu.isFree())) {

            //checks if CPU free, then add next process in the queue
            if (cpu.isFree()) {
                cpu.pushProcess(readyQueue.getNext());
                cpu.setDeadline(cpu.counter + cpu.curProcess.bursts[cpu.curProcess.curBurst]);

                // add to cpu shot count
                cpu.curProcess.report.cpuShots++;
                
            }
            
            // check if cpu is done current burst
            if (cpu.isComplete()) {

                // add to processing time
                cpu.curProcess.report.procTime += cpu.curProcess.bursts[cpu.curProcess.curBurst];

                if (cpu.curProcess.curBurst == cpu.curProcess.burstCount) {

                    // Process has finished execution, generate report and discard
                    cpu.curProcess.report.prepReport(cpu);
                    cpu.curProcess.report.print();
                    
                    // add to fullReport object
                    fullReport.addData(cpu.curProcess.report);

                    cpu.jobsCompleted++;

                    // add another process to ready queue
                    if (processes.size() > 0) {
                        if (processes.peek().arr <= cpu.counter) {
                            readyQueue.addProcess(processes.remove());
                        }
                    }
                }

                // send to blockedqueue, for I/O
                else {                 
                    cpu.curProcess.curBurst++;
                    cpu.curProcess.ioComp = cpu.getCounter() + 10;
                    blockedQueue.addProcess(cpu.curProcess);
                }

                // clear process
                cpu.curProcess = null;
            }

            // check that top of blocked queue is ready
            if (blockedQueue.getSize() > 0) {
                if (blockedQueue.isReady(cpu.getCounter())) {

                    // add to processing time from blockedQueue
                    blockedQueue.processes.peek().report.procTime += 10;
                    
                    // add back to ready queue
                    readyQueue.addProcess(blockedQueue.getNext());

                    // set ioComp time for next process in line, if it exists
                    if (blockedQueue.getSize() > 0) {
                        blockedQueue.processes.peek().ioComp = cpu.getCounter() + 10;
                    } 
                }
            }

            // 200 level reports
            if (cpu.getCounter() % 200 == 0) {
                intermediateReport(readyQueue, blockedQueue, cpu);
            }

            // iterate processing counter
            cpu.timeStep();
        }

        // NOTE: do i keep? ask
        intermediateReport(readyQueue, blockedQueue, cpu);

        fullReport.finalTime = cpu.getCounter();
    }

    static void SJF(Queue<PCB> processes, FullReport fullReport) {
                
        // Initialize queues and cpu
        SJFReadyQueue readyQueue = new SJFReadyQueue();
        BlockedQueue blockedQueue = new BlockedQueue();
        CPU cpu = new CPU();

        // populate jobQueue with first 10 processes
        for(int i = 0; i < 10; i++) {
            PCB cur = processes.remove();
            readyQueue.addProcess(cur);
        }

        // Repeat until all jobs processed and terminated.
        while (!(readyQueue.getSize() == 0 && blockedQueue.getSize() == 0 && cpu.isFree())) {

            //checks if CPU free, then add next process in the queue
            if (cpu.isFree()) {
                if (readyQueue.getSize() > 0) {
                    cpu.pushProcess(readyQueue.getNext());
                    cpu.setDeadline(cpu.counter + cpu.curProcess.bursts[cpu.curProcess.curBurst]);

                    // add to cpu shot count
                    cpu.curProcess.report.cpuShots++;
                }

            }
            
            // check if cpu is done current burst
            if (cpu.isComplete()) {

                // add to processing time
                cpu.curProcess.report.procTime += cpu.curProcess.bursts[cpu.curProcess.curBurst];

                if (cpu.curProcess.curBurst == cpu.curProcess.burstCount) {

                    // Process has finished execution, generate report and discard
                    cpu.curProcess.report.prepReport(cpu);
                    cpu.curProcess.report.print();
                    
                    // add to fullReport object
                    fullReport.addData(cpu.curProcess.report);

                    cpu.jobsCompleted++;

                    // add another process to ready queue
                    if (processes.size() > 0) {
                        if (processes.peek().arr <= cpu.counter) {
                            readyQueue.addProcess(processes.remove());
                        }
                    }
                }

                // send to blockedqueue, for I/O
                else {                 
                    cpu.curProcess.curBurst++;
                    cpu.curProcess.ioComp = cpu.getCounter() + 10;
                    blockedQueue.addProcess(cpu.curProcess);
                }

                // clear process
                cpu.curProcess = null;
            }
            
            // check that top of blocked queue is ready
            if (blockedQueue.getSize() > 0) {
                if (blockedQueue.isReady(cpu.getCounter())) {

                    // add to processing time from blockedQueue
                    blockedQueue.processes.peek().report.procTime += 10;
                    
                    // add back to ready queue
                    readyQueue.addProcess(blockedQueue.getNext());

                    // set ioComp time for next process in line, if it exists
                    if (blockedQueue.getSize() > 0) {
                        blockedQueue.processes.peek().ioComp = cpu.getCounter() + 10;
                    } 
                }
            }

            // 200 level reports
            if (cpu.getCounter() % 200 == 0) {
                intermediateReport(readyQueue, blockedQueue, cpu);
            }

            // iterate processing counter
            cpu.timeStep();
        }

        // NOTE: do i keep? ask
        intermediateReport(readyQueue, blockedQueue, cpu);

        fullReport.finalTime = cpu.getCounter();
    }

    static void RR(Queue<PCB> processes, FullReport fullReport, int quantum) {

        // Initialize queues and cpu
        FCFSReadyQueue readyQueue = new FCFSReadyQueue();
        BlockedQueue blockedQueue = new BlockedQueue();
        CPU cpu = new CPU();
        int inc = 0;

        // populate jobQueue with first 10 processes
        for(int i = 0; i < 10; i++) {
            PCB cur = processes.remove();
            readyQueue.addProcess(cur);
        }

        // Repeat until all jobs processed and terminated.
        while (!(readyQueue.getSize() == 0 && blockedQueue.getSize() == 0 && cpu.isFree())) {

            // checks if cpu is free and that there processes in ready queue, then sends to processor
            if(cpu.isFree()) {
                if (readyQueue.getSize() > 0) {

                    // in this case, we take the minimum of our time quantum and 
                    // current burst. the cpu deadline is set to this; then the
                    // current burst is decremented to reflect the current partition of burst.
                    cpu.pushProcess(readyQueue.getNext());
                    inc = Math.min(quantum, cpu.curProcess.bursts[cpu.curProcess.curBurst]);
                    cpu.setDeadline(cpu.counter + inc);
                    cpu.curProcess.report.cpuShots++;
                }
            }

            // checks if cpu completed current job
            if (cpu.isComplete()) {
                // add to processing time
                cpu.curProcess.report.procTime += inc;
                cpu.curProcess.bursts[cpu.curProcess.curBurst] -= inc;

                // check to see if process has completed current burst
                if (cpu.curProcess.bursts[cpu.curProcess.curBurst] == 0) {
                    // process has completed all bursts, discard
                    if (cpu.curProcess.curBurst == cpu.curProcess.burstCount) {
                        cpu.curProcess.report.prepReport(cpu);
                        cpu.curProcess.report.print();

                        // add to full report object
                        fullReport.addData(cpu.curProcess.report);

                        cpu.jobsCompleted++;

                        // add another process from job queue
                        if (processes.size() > 0) {
                            if (processes.peek().arr <= cpu.counter) {
                                readyQueue.addProcess(processes.remove());
                            }
                        }
                    }
                    // process has not completed all bursts, send to blockedQueue
                    else {
                        cpu.curProcess.curBurst++;
                        cpu.curProcess.ioComp = cpu.getCounter() + 10;
                        blockedQueue.addProcess(cpu.curProcess);
                    }
                }
                else {
                    // send to back of ready queue
                    readyQueue.addProcess(cpu.curProcess);
                }
                // clear process
                cpu.curProcess = null;
                
            }

            // check if top of blocked queue is ready to go back to readyQueue
            if (blockedQueue.getSize() > 0) {
                if (blockedQueue.isReady(cpu.getCounter())) {

                    // add to processing time from blockedQueue
                    blockedQueue.processes.peek().report.procTime += 10;

                    // add back to ready queue
                    readyQueue.addProcess(blockedQueue.getNext());

                    // set ioComp time for next process in line; if exists
                    if (blockedQueue.getSize() > 0) {
                        blockedQueue.processes.peek().ioComp = cpu.getCounter() + 10;
                    }
                }
            }

            // iterate processing counter
            cpu.timeStep();

            // 200 level reports
            if (cpu.getCounter() % 200 == 0) {
                intermediateReport(readyQueue, blockedQueue, cpu);
            }
        }

        // NOTE: do i keep? ask
        intermediateReport(readyQueue, blockedQueue, cpu);

        fullReport.finalTime = cpu.getCounter();
 
    }

    // reports queue sizes and jobs processed for each 200 time block
    static void intermediateReport(Object ready, BlockedQueue blocked, CPU cpu) {

        // i hate this.
        String size;
        if (ready instanceof FCFSReadyQueue) {
            size = Integer.toString(((FCFSReadyQueue) ready).getSize());
        }
        else if (ready instanceof SJFReadyQueue) {
            size = Integer.toString(((SJFReadyQueue) ready).getSize());
        }
        else {
            size = "";
        }

        System.out.printf(
            "| %-7s | %-7s | %-7s | %-7s |%n",
            Integer.toString(cpu.getCounter()), size, Integer.toString(blocked.getSize()), Integer.toString(cpu.jobsCompleted)
        );
    }

    static void intermediateHeader() {
        System.out.printf(
            "| %-7s | %-7s | %-7s | %-7s |%n",
            "TIME", "READY", "BLOCKED", "COMP"
        );
    }

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

    static HashMap<String, String> handleArgs(String[] args) throws Exception {
        if (!(args[0].equals("FCFS") |  args[0].equals("SJF") | args[0].equals("RR"))) {
            throw new Exception("Illegal algorithm. Choose from FCFS, SJF, RR.");
        }

        HashMap<String, String> out = new HashMap<String, String>();
        if (args.length == 2) {
            if (args[0].equals("RR")) {
                throw new Exception("Illegal number of arguments. Input should be: algorithm[quantum] filename");
            }
            out.put("algorithm", args[0]);
            out.put("filename", args[1]);
        }
        else if (args.length == 3) {
            out.put("algorithm", args[0]);
            out.put("quantum", args[1]);
            out.put("filename", args[2]);
        }
        else {
            throw new Exception("Illegal number of arguments. Input should be: algorithm[quantum] filename");
        }

        return out;
    }
}
