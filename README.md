The AgentSimulation project is a Java-based simulation framework designed to model the behavior of social and anti-social agents in a 3D landscape. The project allows for the exploration of how different parameters such as agent count, social radius, and social count influence the movement and interaction of agents within a defined space.

- **Agent Types**: Supports two types of agents - `SocialAgent` and `AntiSocialAgent`.
- **Landscape**: A 3D grid-based landscape where agents can move and interact.
- **Simulation**: Runs multiple simulations with varying parameters to study agent behavior.
- **Visualization**: Generates line charts to visualize the results of the simulations.

## Classes and Their Roles
1. **Agent**: Abstract base class for all agents. Defines common properties and methods.
2. **SocialAgent**: A type of agent that moves if it has fewer neighbors than its social count.
3. **AntiSocialAgent**: A type of agent that moves if it has more neighbors than its social count.
4. **Landscape**: Manages the 3D grid and the agents within it. Provides methods to add agents, get neighbors, and update the state of agents.
5. **AgentSimulation**: The main class that orchestrates the simulation. It runs multiple experiments with different parameters and generates charts to visualize the results.

## How to Run the Simulation
1. **Clone the Repository**: Ensure you have the project files on your local machine.
2. **Compile the Code**: Use a Java compiler to compile all the `.java` files.
   ```bash
   javac *.java
   ```

3. **Run the Simulation**: Execute the `AgentSimulation` class.
   ```bash
   java AgentSimulation
   ```

4. **View Results**: The simulation will generate charts in the `charts` directory. These charts visualize the minimum, maximum, and average iterations for different combinations of parameters.

## Parameters
- **Width, Height, Depth**: Dimensions of the 3D landscape.
- **Social Radius**: The radius within which a social agent considers other agents as neighbors.
- **Anti Radius**: The radius within which an anti-social agent considers other agents as neighbors.
- **Social Count**: The threshold number of neighbors that determine the movement of social and anti-social agents.

## Time and Space Complexity Analysis

### Time Complexity
1. **Agent Movement (`updateState` method)**:
   - **SocialAgent/AntiSocialAgent**: The time complexity is `O(N)`, where `N` is the number of neighbors within the radius. This is because the method iterates over all neighbors to count them.
   
2. **Landscape Neighbor Search (`getNeighbors` method)**:
   - The time complexity is `O(1)` for accessing the grid cell, but `O(M)` for checking agents within the cell, where `M` is the number of agents in the cell. Since the grid size is proportional to the radius, the overall complexity is `O(M)`.

3. **Simulation (`runExperiments` method)**:
   - The time complexity is `O(T * S * A)`, where `T` is the number of trials, `S` is the number of social agent positions, and `A` is the number of anti-social agent positions. This is because the method runs multiple experiments for each combination of agent positions.

4. **Combination Generation (`generateCombinations` method)**:
   - The time complexity is `O(C(n, k))`, where `n` is the total number of coordinates and `k` is the number of agents. This is because the method generates all possible combinations of size `k` from `n` coordinates.

### Space Complexity
1. **Agent Storage**:
   - The space complexity is `O(N)`, where `N` is the total number of agents in the landscape.

2. **Grid Storage**:
   - The space complexity is `O(G)`, where `G` is the number of grid cells. Each cell stores a list of agents, so the overall space complexity is `O(G * M)`, where `M` is the average number of agents per cell.

3. **Simulation Data**:
   - The space complexity is `O(T * S * A)` for storing the results of all experiments, where `T` is the number of trials, `S` is the number of social agent positions, and `A` is the number of anti-social agent positions.

## Main
submit different landscapes to different processors and iterate over social and antiSocial radius and social and antiSocial counts:
```java
public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            for (int width = 1; width < SIDE_LENGTH; width++) {
                for (int height = 1; height < width; height++) {
                    for (int depth = 1; depth < height; depth++) {
                        Landscape scape = new Landscape(width, height, depth);
                        int finalVolume = width * height * depth;
                        int finalMaxRadius = (int) (Math.cbrt(finalVolume) / 2);

                        executor.submit(() -> {
                            for (double socialRadius = 1; socialRadius < finalMaxRadius; socialRadius += 0.25) {
                                for (double antiRadius = 1; antiRadius < finalMaxRadius; antiRadius += 0.25) {
                                    for (int socialSocialCount = 1; socialSocialCount < finalVolume; socialSocialCount++) {
                                        for (int antiSocialCount = 1; antiSocialCount < finalVolume; antiSocialCount++) {
                                            processCombination(scape, finalVolume, socialRadius, antiRadius, socialSocialCount, antiSocialCount);
                                        }
                                    }
                                }
                            }
                        } );
                    }
                }
            }
            executor.shutdown(); // make sure executor services are shut down, while try with resources statement automatically shuts down executor services
        }
    }

```


## Output
The simulation generates PNG images of line charts in the `charts` directory. Each chart represents the results of a specific combination of parameters, showing the minimum, maximum, and average iterations required for the agents to stabilize.

## Dependencies
- Java 8 or higher
- `java.awt` for graphics and visualization
- `javax.swing` for chart panel creation
- `java.util.concurrent` for managing simulation threads


## Contributing
Contributions are welcome! Please fork the repository and submit a pull request with your changes

## Contact
For any questions or suggestions, please contact the project maintainer at txiao28@colby.edu or xtp20210316@163.com
