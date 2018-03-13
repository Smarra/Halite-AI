import hlt.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Tamagocchi");

        // We now have 1 full minute to analyse the initial map.
        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);

        final ArrayList<Move> moveList = new ArrayList<>();
        final ArrayList<Planet> destinations = new ArrayList<>();
        for (;;) {
            moveList.clear();
            destinations.clear();
            networking.updateMap(gameMap);

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }

                ArrayList<Planet> sortedPlanets = new ArrayList<>(gameMap.getAllPlanets().values());
                Collections.sort(sortedPlanets, new Comparator<Planet>(){

                    public int compare(final Planet a, final Planet b) {
                        if( ship.getDistanceTo(a) - ship.getDistanceTo(b) < 0 )
                            return -1;
                        return 1;
                    }
                });
                boolean shipHasDestination = false;
                for (final Planet planet : sortedPlanets) {
                    if (planet.isOwned()) {
                        continue;
                    }

                    if (ship.canDock(planet)) {
                        shipHasDestination = true;
                        moveList.add(new DockMove(ship, planet));
                        break;
                    }

                    if (destinations.contains(planet)) {
                        continue;
                    }

                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
                    if (newThrustMove != null) {
                        destinations.add(planet);
                        shipHasDestination = true;
                        moveList.add(newThrustMove);
                    }

                    break;
                }

                if( true== false ) {
                    for (final Planet planet : sortedPlanets) {
                        if (planet.isOwned()) {
                            continue;
                        }

                        final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
                        if (newThrustMove != null) {
                            moveList.add(newThrustMove);
                        }

                        break;
                    }
                }
            }
            Networking.sendMoves(moveList);
        }
    }
}
