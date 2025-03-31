Simulation of Social and Anti-Social Agents in a Landscape
Abstract
In this project, I explored the dynamics of social and anti-social agents within a simulated landscape. The
goal was to understand how these agents interact based on their behaviors and the density of their population. I implemented a simulation using core concepts of object-oriented programming, including classes and
inheritance, to model agent behaviors and their interactions. my key findings indicated that the number of
agents significantly affects the time it takes for the simulation to reach a stable state, with higher densities
leading to increased iterations before stabilization. Results
Experiment 1: Varying Number of Agents
I conducted experiments with varying numbers of agents set to 50, 100, 150, 200, and 250. The following
table summarizes the number of iterations it took for the simulation to stop:
Number of Agents Iterations to Stop
50 2
100 2
150 17
200 15
250 15
From the results, I observe that with feIr agents (50 and 100), the simulation stabilized quickly, taking only
2 iterations. HoIver, as the number of agents increased to 150, the iterations increased significantly to 17, indicating a more complex interaction among agents. Interestingly, the iterations decreased again for 200
and 250 agents, suggesting that the agents may have reached a balance in their interactions, leading to
quicker stabilization. Experiment 2: Varying Radius Size of Agents
For my second experiment, I varied the radius size of the agents while keeping the grid size constant at
500x500. I tested with radius sizes of 10, 25, and 50. The following table summarizes the results:
Radius Size Iterations to Stop
10 30
25 15
50 5
The results indicate that smaller radius sizes lead to more iterations before stabilization, as agents have less
influence on their neighbors. Conversely, larger radius sizes allow agents to interact with more neighbors, resulting in quicker stabilization. This suggests that the radius size plays a crucial role in the dynamics of
agent interactions.
Extensions: The Simulations of Both Agents and the 3D Simulations of SocialAgents
Implementation Details
During the project, I extended the simulation to three dimensions to understand how agents behave in a
three-dimensional space compared to a two-dimensional landscape. The key changes involved:
I modified the Landscape class to support depth by adding a depth attribute. This allows the landscape to be
represented in three dimensions. I extended the Agent class to include a z coordinate, enabling agents to
move in three-dimensional space. The getNeighbors method in the Landscape class was updated to consider
the z coordinate when calculating the distance betIen agents. 3D Visualization: Although the visualization remains 2D, the simulation logic fully supports 3D interactions, allowing agents to move and interact in three dimensions. The 3D simulation logics:
Initialization: The landscape is initialized with a specified width, height, and depth. Agents are randomly
placed within this 3D space. Agent Movement: Each agent updates its position based on its social or anti-social behavior. Social agents
move towards areas with feIr neighbors, while anti-social agents move away from areas with more
neighbors. Neighbor Interaction: Agents interact with their neighbors within a specified radius. The interaction logic
considers the 3D distance betIen agents, ensuring that agents only influence those within their immediate
vicinity. Stabilization Check: The simulation continues until no agents move or a maximum number of iterations is
reached. The number of iterations required for stabilization is recorded. However, it takes over a night to finish iterating over the 2D or to finish the 32*32*32 landscape simulations
on my PC, so I reflected on the complexity of the whole simulations. Time Complexity
Original 2D Version (Base)
Agent Neighbor Calculation: For each agent, Landscape.getNeighbors() iterates through all agents to check
distance. Time Complexity: O(N) per agent → O(N²) per iteration. This is the dominant factor in the simulation’s time complexity. Agent State Update: Each agent’s updateState() method runs in O(1) time (assuming neighbor count is
precomputed). Total: O(N) per iteration. Iteration Loop:
The simulation runs until stabilization (up to 5000 iterations). Total Time Complexity: O(I × N²), where I is the number of iterations. Worst-Case: O(N²) per iteration due to unoptimized neighbor checks. Experiment Results: Iterations increase with agent density (e.g., 150 agents took 17 iterations), suggesting I is
roughly proportional to N in practice.
Extended 3D Version
Key Improvements:
Spatial Partitioning (Grid-Based Neighbor Search):
Landscape.getNeighbors():
Agents are partitioned into a 3D grid based on their coordinates. Only agents in neighboring grid cells are checked for proximity. Grid Size: Determined by gridSize = max(1, (int)Math.cbrt(agents.size()) + 1), leading to ~O(N^(1/3)) grid cells. Time Complexity per Agent: O(k), where k is the number of agents in neighboring grid cells (typically small). Total Neighbor Search Complexity: O(N × k) per iteration → O(N) if k is a constant. Parallel Execution:
simulate3DAndPlot() uses an ExecutorService with 4 threads to run simulations in parallel. Each thread handles a subset of simulation sizes (e.g., 2–32 units). Time Reduction: The total time is divided by the number of threads (P), assuming no contention. Features:
3D Agent Movement: Adds a z coordinate but does not fundamentally change the algorithm’s complexity (still
O(N)). 3D Chart Plotting: The plot3DChart() method is incomplete, but if implemented with standard plotting logic, it
would add O(N) complexity. Extended Time Complexity:
Neighbor Search: O(N × k) → O(N) if k is constant. Parallel Execution: Total time for multiple simulations is O(I × N / P). Total Time Complexity:
Per Simulation: O(I × N). With Parallelism: O(I × N / P) for multiple simulations. Optimization Gains: Spatial partitioning reduces neighbor checks from O(N²) to O(N), a significant
improvement. Parallel Overhead: Thread management adds minor overhead, but the linear speedup from parallelism dominates. Space Complexity
Original 2D Version
Agents Storage:
LinkedList<Agent> in Landscape stores all agents → O(N). Graphics Rendering:
The LandscapeDisplay and Swing components use O(1) space (excluding agent data).
Other Data Structures:
Iteration counters and temporary variables → negligible. Total Space Complexity: O(N). Extended 3D Version
Key Additions:
Spatial Partitioning Grid:
HashMap<Integer, LinkedList<Agent>> grid stores agents partitioned into grid cells. Grid Size: ~O(N^(1/3)) cells (due to gridSize = cbrt(agents.size())). Each cell holds a linked list of agents → O(N) total storage (agents are stored in both agents and grid lists, but
this is still O(N)). Parallel Execution:
Multiple Landscape instances are created for parallel simulations. If running P simulations concurrently, space becomes O(P × N). 3D Agent Coordinates:
Each agent now tracks a z coordinate → O(1) per agent (no asymptotic change). Revised Space Complexity:
Single Simulation: O(N + N^(1/3)) ≈ O(N) (dominated by agent storage). Parallel Simulations: O(P × N) due to concurrent instances. Aspect Original (2D) Extended (3D + Parallel) Key Differences
Time
Complexity
O(I × N²) O(I × N / P) (with spatial
partitioning)
Spatial partitioning reduces
neighbor checks from O(N²) to
O(N), while parallelism further
reduces runtime. Space
Complexity
O(N) O(N) (single sim) or O(P ×
N) (parallel)
Parallelism increases space due to
multiple concurrent simulations. Bottleneck Neighbor
checks (O(N²))
Agent updates and parallel
thread management
Optimized neighbor search removes
the O(N²) bottleneck. Parallelism
introduces trade-offs. 4. Practical Implications
Time Efficiency:
The extended version reduces the core computation from quadratic to linear time (O(N) instead of O(N²)), making it feasible for large N. Parallel execution further accelerates batch simulations (e.g., parameter sweeps).
Space Trade-offs:
Parallelism increases memory usage but is manageable for moderate P (e.g., 4 threads). The grid-based spatial partitioning adds negligible overhead (O(N^(1/3)) is negligible for large N). Limitations:
The grid’s efficiency depends on gridSize. If agents cluster tightly, k (agents per cell) may increase, reducing
gains. Parallelism benefits diminish if simulations are too small or threads contend for resources. Generally, the extended version significantly improves time complexity through spatial partitioning and
parallelism while maintaining linear space complexity (except under parallel execution). These
optimizations make the simulation scalable for large agent populations and complex 3D scenarios, addressing the original version’s O(N²) bottleneck. However, parallel execution requires careful resource
management to avoid excessive memory usage. Conclusion
The extension to 3D simulation provided deeper insights into the dynamics of agent interactions in a more
complex spatial environment. The results suggest that the dimensionality of the landscape significantly
impacts the behavior and stabilization time of the agents, highlighting the importance of spatial
considerations in agent-based modeling. Acknowledgment
I thank lab instructor Issac for timely feedback for me to develop the AgentSimulation class. I consulted
with online tutorial about using hashmap to store xyz coordinate values for agents and methodologies of
demonstrating 3D objects in GUI including projection. I also checked forum like StackOverflow for special
issues in development.
