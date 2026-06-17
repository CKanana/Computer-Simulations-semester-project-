import java.util.Random;

/**
 * SimulationEngine.java
 *
 * Contains all queue simulation logic.
 * No Swing dependencies — purely computational.
 *
 * Models a single-server queue where:
 *   Inter-arrival times ~ Uniform(iatLow, iatHigh)
 *   Service times       ~ Uniform(svcLow, svcHigh)
 */
public class SimulationEngine {

    // ── Random number generation ───────────────────────────────────────────────

    /**
     * Draws one value from Uniform(a, b).
     */
    private static double randUniform(double a, double b, Random rng) {
        return a + (b - a) * rng.nextDouble();
    }

    // ── Main simulation method ─────────────────────────────────────────────────

    /**
     * Runs the single-server queue simulation and returns a SimulationResult.
     *
     * @param n         number of customers to simulate
     * @param iatLow    lower bound of inter-arrival time distribution (minutes)
     * @param iatHigh   upper bound of inter-arrival time distribution (minutes)
     * @param svcLow    lower bound of service time distribution (minutes)
     * @param svcHigh   upper bound of service time distribution (minutes)
     * @param seed      random seed (pass -1 for a truly random run)
     */
    public static SimulationResult run(int n,
                                       double iatLow, double iatHigh,
                                       double svcLow, double svcHigh,
                                       long seed) {

        Random rng = (seed >= 0) ? new Random(seed) : new Random();

        // ── Per-customer arrays ──
        double[] arrivalTime  = new double[n];
        double[] iat          = new double[n];
        double[] serviceTime  = new double[n];
        double[] serviceStart = new double[n];
        double[] serviceEnd   = new double[n];
        double[] waitTime     = new double[n];
        double[] timeInSystem = new double[n];

        // ── Event-by-event simulation ──
        double clock      = 0;   // current arrival clock
        double serverFree = 0;   // time at which server becomes free

        for (int i = 0; i < n; i++) {

            // First customer arrives at time 0; others have a random IAT
            iat[i]          = (i == 0) ? 0 : randUniform(iatLow, iatHigh, rng);
            clock          += iat[i];
            arrivalTime[i]  = clock;

            serviceTime[i]  = randUniform(svcLow, svcHigh, rng);

            // Customer enters service immediately if server is free; otherwise waits
            serviceStart[i] = Math.max(clock, serverFree);
            serviceEnd[i]   = serviceStart[i] + serviceTime[i];
            serverFree      = serviceEnd[i];

            waitTime[i]     = serviceStart[i] - arrivalTime[i];
            timeInSystem[i] = serviceEnd[i]   - arrivalTime[i];
        }

        // ── Aggregate statistics ──
        double totalWait   = 0;
        double totalSvc    = 0;
        double totalInSys  = 0;
        double totalIdle   = 0;
        int    waitedCount = 0;

        double prevServerFree = 0;   // track idle gaps between customers
        for (int i = 0; i < n; i++) {
            totalWait  += waitTime[i];
            totalSvc   += serviceTime[i];
            totalInSys += timeInSystem[i];

            // Server is idle when next customer arrives before server finishes previous job
            double idleGap = Math.max(0, arrivalTime[i] - prevServerFree);
            totalIdle += idleGap;
            prevServerFree = serviceEnd[i];

            if (waitTime[i] > 0) waitedCount++;
        }

        double simEnd = serviceEnd[n - 1];

        double avgWaitAll          = totalWait  / n;
        double avgWaitWhoWaited    = (waitedCount > 0) ? totalWait / waitedCount : 0;
        double avgServiceTime      = totalSvc   / n;
        double avgTimeInSystem     = totalInSys / n;
        double serverIdlePct       = (totalIdle / simEnd) * 100.0;
        double serverUtilPct       = 100.0 - serverIdlePct;
        double pctCustomersWaited  = ((double) waitedCount / n) * 100.0;

        return new SimulationResult(
                n,
                arrivalTime, iat, serviceTime,
                serviceStart, serviceEnd,
                waitTime, timeInSystem,
                avgWaitAll, avgWaitWhoWaited,
                avgServiceTime, avgTimeInSystem,
                serverIdlePct, serverUtilPct,
                pctCustomersWaited, simEnd,
                waitedCount
        );
    }
}