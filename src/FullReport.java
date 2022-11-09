/*
 * Data holding object for providing aggregate data the summarize simulation statistics.
 */
public class FullReport {
        String algo;
        int finalTime;
        long totalProc;
        long totalWait;
        long totalTurnaround;
        int totalShots;
        int totalJobs;

        /*
         * @param algoType Is the algorithm type used
         * 
         * @param numJobs is the number of jobs in the job queue to be finished.
         * 
         * @param quantum The time quantum used, if applicable (RR only)
         */
        public FullReport(String algoType, int numJobs) {
                algo = algoType;
                totalProc = 0;
                totalWait = 0;
                totalTurnaround = 0;
                totalShots = 0;
                totalJobs = numJobs;
        }

        public FullReport(String algoType, int numJobs, int quantum) {
                algo = algoType + " (q = " + Integer.toString(quantum) + ")";
                totalProc = 0;
                totalWait = 0;
                totalTurnaround = 0;
                totalShots = 0;
                totalJobs = numJobs;
        }

        /*
         * Adds data from a completed process to the totals held by the report.
         * 
         * @param report A report object held by the process that just completed.
         */
        public void addData(PCBReport report) {
                totalProc += report.procTime;
                totalWait += report.waitTime;
                totalTurnaround += report.turnaroundTime;
                totalShots += report.cpuShots;
        }

        /*
         * Prints a nicely formatted table summarizing the simulation statistics,
         * including:
         * algorithm used, total time, average processing time, average wait time,
         * average turnaround time, average cpu shots.
         */
        public void print() {

                // gets averages of each parameter
                int avgProc = (int) (totalProc / totalJobs);
                int avgWait = (int) (totalWait / totalJobs);
                int avgTurnaround = (int) (totalTurnaround / totalJobs);
                int avgShots = (int) (totalShots / totalJobs);

                // prints into nicely formatted table
                System.out.println();
                System.out.printf(
                                "===========================================================================================%n");
                System.out.printf(
                                "|                                      FINAL REPORT:                                      |%n");
                System.out.printf(
                                "===========================================================================================%n");
                System.out.printf(
                                "| %-12s | %-12s | %-12s | %-12s | %-12s | %-12s |%n",
                                "Algorithm", "Total Time", "Avg Proc", "Avg Wait", "Avg Turn", "Avg Shots");
                System.out.printf(
                                "===========================================================================================%n");
                System.out.printf(
                                "| %-12s | %-12s | %-12s | %-12s | %-12s | %-12s |%n",
                                this.algo, Integer.toString(this.finalTime), Integer.toString(avgProc),
                                Integer.toString(avgWait),
                                Integer.toString(avgTurnaround), Integer.toString(avgShots));
                System.out.printf(
                                "===========================================================================================%n");
        }
}
