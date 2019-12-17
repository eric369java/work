package cpen221.mp2.spaceship;

import cpen221.mp2.controllers.GathererStage;
import cpen221.mp2.controllers.HunterStage;
import cpen221.mp2.controllers.Spaceship;
import cpen221.mp2.graph.*;
import cpen221.mp2.models.Link;
import cpen221.mp2.models.Planet;
import cpen221.mp2.models.PlanetStatus;
import cpen221.mp2.util.Heap;

import java.util.*;

/**
 * An instance implements the methods needed to complete the mission.
 */
public class MillenniumFalcon implements Spaceship {
    long startTime = System.nanoTime();


    /**
     * Searches the universe for Kamino and direct the Millennium Falcon to travel there.
     * @param state of the hunter stage.
     */
    @Override
    public void hunt(HunterStage state) {
        Stack<Integer> pathStack = new Stack<>();
        Set<Integer> visited = new HashSet<>();

        while (!state.onKamino()) {
            visited.add(state.currentID());
            pathStack.push(state.currentID());

            // find unvisited neighbours
            List<PlanetStatus> neighbours = new ArrayList<>();
            for (PlanetStatus n:state.neighbors()) if (!visited.contains(n.id())) neighbours.add(n);

            // sort unvisited neighbours by signal level
            neighbours.sort(Comparator.comparingDouble(PlanetStatus::signal));
            Collections.reverse(neighbours);

            // return at dead end or move to max signal unvisited neighbor
            if (neighbours.isEmpty()) {
                pathStack.pop();
                state.moveTo(pathStack.pop());
            } else state.moveTo(neighbours.get(0).id());
        }
    }

    /**
     *Visit planets to collect spices in order to maximize spice collection. Returns to Earth when remaining is low.
     * @param state of the gather stage.
     */
    @Override
    public void gather(GathererStage state) {

        // create a list of planet in descending spice level
        List<Planet> spicyList = new ArrayList<>(state.planets());
        spicyList.sort(Comparator.comparingInt(Planet::spice));
        Collections.reverse(spicyList);

        // record visited planets
        List<Planet> visited = new ArrayList<>();

        // try to reach the spiciest planets
        for (Planet next:spicyList) {

            // skip next planet if it is already visited
            if (visited.contains(next)) continue;
            else visited.add(next);

            // calculate path length from current to next and from next to earth
            List<Planet> nextPath = state.planetGraph().shortestPath(state.currentPlanet(), next);
            List<Planet> nextToEarth = state.planetGraph().shortestPath(next, state.earth());
            int nextDistance = state.planetGraph().pathLength(nextPath);
            int nextToEarthDistance = state.planetGraph().pathLength(nextToEarth);

            // return if there is not enough fuel to return to earth after visiting the next spiciest planet
            if (state.fuelRemaining() - nextDistance < nextToEarthDistance) break;

            // visit the next spiciest planet
            for (int i = 1; i < nextPath.size(); i++) state.moveTo(nextPath.get(i));
        }

        // return to earth
        List<Planet> earthPath = state.planetGraph().shortestPath(state.currentPlanet(), state.earth());
        for (int i = 1; i < earthPath.size(); i++) state.moveTo(earthPath.get(i));
    }
}