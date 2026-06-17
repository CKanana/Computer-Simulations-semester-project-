# Bank Queue Simulation
### ICS 4106 — Computer Simulation & Modelling

A single-server bank queue simulator built in Java Swing.  
Generates inter-arrival times and service times from **Uniform distributions**, runs an event-by-event simulation for up to 500 customers, and reports full queue statistics with a colour-coded results table.

---

## Project Structure

```
BankQueueSim/
├── Main.java                  ← Entry point — launches the application
├── SimulationEngine.java      ← Pure simulation logic (no UI)
├── SimulationResult.java      ← Data model — holds all per-customer & aggregate results
├── InputWindow.java           ← Input form JFrame
├── OutputWindow.java          ← Results JFrame (tabbed)
├── SummaryPanel.java          ← Tab 1: metric cards (queue statistics)
└── SimulationTablePanel.java  ← Tab 2: full per-customer simulation table
```

### Class responsibilities

| Class | Responsibility |
|---|---|
| `Main` | Sets system look-and-feel, launches `InputWindow` on the EDT |
| `SimulationEngine` | Stateless simulation engine — takes parameters, returns a `SimulationResult` |
| `SimulationResult` | Immutable data model; stores arrays and computed statistics |
| `InputWindow` | Collects & validates user inputs; calls `SimulationEngine.run()` |
| `OutputWindow` | Hosts the tabbed results pane; receives a `SimulationResult` |
| `SummaryPanel` | Renders 8 queue-statistic metric cards |
| `SimulationTablePanel` | Renders the scrollable per-customer data table |

---

## Simulation Model

### Distributions

| Variable | Distribution | Default range |
|---|---|---|
| Inter-arrival time (IAT) | Uniform(a, b) | U(1, 8) minutes |
| Service time | Uniform(a, b) | U(1, 6) minutes |

Both ranges are fully configurable from the input form.

### Queue model

- **Type:** Single-server, first-come-first-served (FCFS)
- **Queue discipline:** Infinite capacity, no balking or reneging
- **First customer:** Arrives at time 0; subsequent customers have random IATs
- **Server rule:** Customer enters service immediately if server is free; otherwise waits

### Event logic (per customer `i`)

```
IAT[i]          = 0                           (i = 0)
                  Uniform(iatLow, iatHigh)     (i > 0)

ArrivalTime[i]  = ArrivalTime[i-1] + IAT[i]
ServiceTime[i]  = Uniform(svcLow, svcHigh)
ServiceStart[i] = max(ArrivalTime[i], ServiceEnd[i-1])
ServiceEnd[i]   = ServiceStart[i] + ServiceTime[i]
WaitTime[i]     = ServiceStart[i] - ArrivalTime[i]
TimeInSystem[i] = ServiceEnd[i]   - ArrivalTime[i]
```

### Statistics computed

| Statistic | Formula |
|---|---|
| Avg wait (all customers) | Σ WaitTime / n |
| Avg wait (those who waited) | Σ WaitTime / count(WaitTime > 0) |
| Avg service time | Σ ServiceTime / n |
| Avg time in system | Σ TimeInSystem / n |
| Server utilisation % | (SimEnd − TotalIdle) / SimEnd × 100 |
| Server idle % | TotalIdle / SimEnd × 100 |
| % customers who waited | count(WaitTime > 0) / n × 100 |
| Total simulation time | ServiceEnd[last customer] |

---

## How to Run

### Prerequisites
- Java 11 or later
- IntelliJ IDEA (Community or Ultimate)

### Steps

1. Open IntelliJ → `File → New Project → Java` (no frameworks needed)
2. Delete the default `src/Main.java` that IntelliJ generates
3. For each of the 7 `.java` files:
    - Right-click `src/` → `New → Java Class`
    - Name it exactly as the file (e.g. `SimulationEngine`) — IntelliJ creates the `.java` file
    - Paste in the contents of the corresponding file
4. Right-click `Main.java` → `Run 'Main.main()'`

> All 7 classes share the **default package** — no `package` declaration is needed.  
> IntelliJ compiles them all together automatically.

### Command line (alternative)

```bash
# From the directory containing all .java files
javac *.java
java Main
```

---

## Using the Application

### Input Window

Fill in the simulation parameters:

| Field | Description | Default |
|---|---|---|
| Number of customers | How many customers to simulate | 100 |
| IAT lower bound a | Minimum inter-arrival time (minutes) | 1.0 |
| IAT upper bound b | Maximum inter-arrival time (minutes) | 8.0 |
| Service lower bound a | Minimum service time (minutes) | 1.0 |
| Service upper bound b | Maximum service time (minutes) | 6.0 |
| Fixed seed | Tick for reproducible results | ✓ (seed 42) |

Click **▶ Run Simulation** to open the Output Window.  
Click **↺ Reset Defaults** to restore all fields to their default values.

### Output Window — Tab 1: Queue Statistics

Eight metric cards showing the key queue statistics for the run.  
Cards are colour-coded by category:
- **Blue** — waiting time metrics
- **Green** — service time metrics
- **Purple** — server utilisation
- **Amber** — proportion who waited
- **Grey** — simulation duration

### Output Window — Tab 2: Simulation Table

A full 100-row (or n-row) table with one row per customer showing all 8 time values.  
**Amber-highlighted rows** indicate customers who had to wait before being served.

---

## Design Decisions

- **Separation of concerns** — `SimulationEngine` and `SimulationResult` have zero Swing imports; they can be reused in a CLI or web context without change.
- **Immutable result model** — `SimulationResult` stores final arrays set once at construction, preventing accidental mutation from the UI layer.
- **Fixed seed toggle** — allows reproducible runs for assignment submission while still supporting truly random runs for experimentation.
- **EDT safety** — all Swing construction happens inside `SwingUtilities.invokeLater()` in `Main`.

---

## Team

**Group Project — ICS 4106: Computer Simulation & Modelling**  
Bachelor of Science in Information Technology — ICS 4A  
Strathmore University

| Name | GitHub                          |
|---|---------------------------------|
| Crystal Kanana | [@ckanana](https://github.com/CKanana) ||
| Donell Bikketi | [@donellbikketi](https://github.com/D0nell) |
| Emmanuel Douglas Ouma | [@emmanueldouglas](https://github.com/DiggaDouglas) |
| Gloria Kendi | [@gloriakendi](https://github.com/gkendi2) |
| Andrew Kigoundu | [@andrewkigosundu](https://github.com/Kigondinho) |

> All members are collaborators on the project GitHub repository.
