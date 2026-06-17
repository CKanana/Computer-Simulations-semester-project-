/**
 * SimulationResult.java
 *
 * Pure data model — holds every per-customer record and the
 * aggregate queue statistics produced by SimulationEngine.
 *
 * No Swing, no I/O — just plain Java fields and accessors.
 */
public class SimulationResult {

    // ── Per-customer arrays (length == numCustomers) ──────────────────────────

    private final int      numCustomers;
    private final double[] arrivalTime;   // clock time customer arrives
    private final double[] iat;           // inter-arrival time (0 for first customer)
    private final double[] serviceTime;   // random service duration
    private final double[] serviceStart;  // when server actually starts serving
    private final double[] serviceEnd;    // when server finishes serving
    private final double[] waitTime;      // time spent waiting in queue
    private final double[] timeInSystem;  // total time from arrival to departure

    // ── Aggregate statistics ──────────────────────────────────────────────────

    private final double avgWaitAll;       // average wait across ALL customers
    private final double avgWaitWhoWaited; // average wait for customers who actually waited
    private final double avgServiceTime;   // average service duration
    private final double avgTimeInSystem;  // average total sojourn time
    private final double serverIdlePct;    // server idle percentage
    private final double serverUtilPct;    // server utilisation percentage
    private final double pctCustomersWaited; // percentage of customers who waited
    private final double simulationEndTime;  // clock time when last customer finishes
    private final int    numWhoWaited;       // count of customers who waited

    // ── Constructor (called only by SimulationEngine) ─────────────────────────

    SimulationResult(int numCustomers,
                     double[] arrivalTime, double[] iat,
                     double[] serviceTime, double[] serviceStart,
                     double[] serviceEnd,  double[] waitTime,
                     double[] timeInSystem,
                     double avgWaitAll, double avgWaitWhoWaited,
                     double avgServiceTime, double avgTimeInSystem,
                     double serverIdlePct, double serverUtilPct,
                     double pctCustomersWaited, double simulationEndTime,
                     int numWhoWaited) {

        this.numCustomers        = numCustomers;
        this.arrivalTime         = arrivalTime;
        this.iat                 = iat;
        this.serviceTime         = serviceTime;
        this.serviceStart        = serviceStart;
        this.serviceEnd          = serviceEnd;
        this.waitTime            = waitTime;
        this.timeInSystem        = timeInSystem;
        this.avgWaitAll          = avgWaitAll;
        this.avgWaitWhoWaited    = avgWaitWhoWaited;
        this.avgServiceTime      = avgServiceTime;
        this.avgTimeInSystem     = avgTimeInSystem;
        this.serverIdlePct       = serverIdlePct;
        this.serverUtilPct       = serverUtilPct;
        this.pctCustomersWaited  = pctCustomersWaited;
        this.simulationEndTime   = simulationEndTime;
        this.numWhoWaited        = numWhoWaited;
    }

    // ── Per-customer accessors ─────────────────────────────────────────────────

    public int    getNumCustomers()           { return numCustomers; }
    public double getArrivalTime(int i)       { return arrivalTime[i]; }
    public double getIAT(int i)               { return iat[i]; }
    public double getServiceTime(int i)       { return serviceTime[i]; }
    public double getServiceStart(int i)      { return serviceStart[i]; }
    public double getServiceEnd(int i)        { return serviceEnd[i]; }
    public double getWaitTime(int i)          { return waitTime[i]; }
    public double getTimeInSystem(int i)      { return timeInSystem[i]; }
    public boolean customerWaited(int i)      { return waitTime[i] > 0; }

    // ── Aggregate accessors ────────────────────────────────────────────────────

    public double getAvgWaitAll()             { return avgWaitAll; }
    public double getAvgWaitWhoWaited()       { return avgWaitWhoWaited; }
    public double getAvgServiceTime()         { return avgServiceTime; }
    public double getAvgTimeInSystem()        { return avgTimeInSystem; }
    public double getServerIdlePct()          { return serverIdlePct; }
    public double getServerUtilPct()          { return serverUtilPct; }
    public double getPctCustomersWaited()     { return pctCustomersWaited; }
    public double getSimulationEndTime()      { return simulationEndTime; }
    public int    getNumWhoWaited()           { return numWhoWaited; }
}