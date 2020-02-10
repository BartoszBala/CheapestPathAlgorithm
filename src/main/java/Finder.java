import java.awt.*;
import java.util.*;
import java.util.List;


/*INSTRUCTIONS

Your task is to find the path through the field which has the lowest cost to go through.

As input you will receive:
1) a toll_map matrix (as variable t) which holds data about how expensive it is to go through the given field coordinates
2) a start coordinate (tuple) which holds information about your starting position
3) a finish coordinate (tuple) which holds information about the position you have to get to

As output you should return:
1) the directions list

CLARIFICATIONS

1) the start and finish tuples have the (x, y) format which means start = (x_1, y_1) and finish = (x_2, y_2), start_pos = field[x_1][y_1] and finish_pos = field[x_2][y_2]
2) the total cost is increased after leaving the matrix coordinate, not entering it
3) the field will be rectangular, not necessarily a square
4) the field will always be of correct shape
5) the actual tests will check total_cost based on your returned directions list, not the directions themselves, so you shouldn't worry about having multiple possible solutions

*/

    public class Finder {

        int[][] board;
        Point start;
        Point finish;
        Map<Point, KeyGraph> graph = new HashMap<>();
        Map<Point, CostObject> cost = new HashMap();
        Map<Point, Point> parents = new HashMap<>();
        PriorityQueue<CostObject> queueOfCost = new PriorityQueue<CostObject>((o1, o2) -> o1.cost-o2.cost);
        Set<Point> checkedNodes = new HashSet<>();


        public class KeyGraph {
            Map<Point, Integer> neighbourMap;
            boolean processed;

            public KeyGraph(Map<Point, Integer> mapa, boolean processed) {
                this.neighbourMap = mapa;
                this.processed = processed;

            }
        }

        public class CostObject  {
            boolean processed;
            Integer cost;
            Point point;

            public CostObject(Point point, boolean processed, Integer cost) {
                this.processed = processed;
                this.cost = cost;
                this.point = point;
            }


            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof CostObject)) return false;
                CostObject that = (CostObject) o;
                return processed == that.processed &&
                        Objects.equals(cost, that.cost) &&
                        Objects.equals(point, that.point);
            }

            @Override
            public int hashCode() {
                return Objects.hash(processed, cost, point);
            }
        }

        public Finder(int[][] board, Point start, Point finish) {
            this.board = board;
            this.start = start;
            this.finish = finish;

            Map<Point, Integer> neighbours;

            for (int i = 0; i < board.length; i++) {

                for (int j = 0; j < board[0].length; j++) {
                    neighbours = new HashMap<>();


                    if (i != board.length - 1) {
                        neighbours.put(new Point(i + 1, j), board[i][j]);
                    }
                    if (i != 0) {
                        neighbours.put(new Point(i - 1, j), board[i][j]);
                    }
                    if (j != board[0].length - 1) {
                        neighbours.put(new Point(i, j + 1), board[i][j]);
                    }
                    if (j != 0) {
                        neighbours.put(new Point(i, j - 1), board[i][j]);
                    }


                    if (i == start.x && j != board[0].length - 1 && j == start.y - 1) {
                        queueOfCost.add(new CostObject(new Point(i, j), true, board[i][j + 1]));
                        checkedNodes.add(new Point(i, j));
                        cost.put(new Point(i, j), new CostObject(new Point(i, j), true, board[i][j + 1]));
                        parents.put(new Point(i, j), new Point(i, j + 1));
                    } else if (i == start.x && j != 0 && j == start.y + 1) {
                        queueOfCost.add(new CostObject(new Point(i, j), true, board[i][j - 1]));
                        checkedNodes.add(new Point(i, j));
                        cost.put(new Point(i, j), new CostObject(new Point(i, j), true, board[i][j - 1]));
                        parents.put(new Point(i, j), new Point(i, j - 1));
                    } else if (j == start.y && i != board.length - 1 && i == start.x - 1) {
                        queueOfCost.add(new CostObject(new Point(i, j), true, board[i + 1][j]));
                        cost.put(new Point(i, j), new CostObject(new Point(i, j), true, board[i + 1][j]));
                        checkedNodes.add(new Point(i, j));
                        parents.put(new Point(i, j), new Point(i + 1, j));
                    } else if (j == start.y && i != 0 && i == start.x + 1) {
                        queueOfCost.add(new CostObject(new Point(i, j), true, board[i - 1][j]));
                        cost.put(new Point(i, j), new CostObject(new Point(i, j), true, board[i - 1][j]));
                        checkedNodes.add(new Point(i, j));
                        parents.put(new Point(i, j), new Point(i - 1, j));
                    } else if (i == start.x && j == start.y) {
//                    queueOfCost.add(new CostObject(new Point(i,j),false,0));
                        parents.put(new Point(i, j), null);
                        cost.replace(start, new CostObject(new Point(i, j), false, 0));
                    } else {
                        cost.put(new Point(i, j), new CostObject(new Point(i, j), true, Integer.MAX_VALUE));
                        parents.put(new Point(i, j), null);
                    }
                    KeyGraph keyGraph = new KeyGraph(neighbours, true);

                    graph.put(new Point(i, j), keyGraph);

                }

            }

            graph.get(start).processed = false;
        }

        java.util.List<String> metoda() {

            if (start.equals(finish))
                return Collections.emptyList();

            Point point;

            for (int i = 0; i < board.length * board[0].length - 1; i++) {

                CostObject currentCostObject = queueOfCost.remove();

                point = currentCostObject.point;

                for (Map.Entry<Point, Integer> entry : graph.get(point).neighbourMap.entrySet()) { //wszyscy sasiedzi pointa
                    int koszt = cost.get(point).cost;
                    Point punkt = entry.getKey();
                    if (!(punkt.x == start.x && punkt.y == start.y) && cost.get(punkt).processed && !checkedNodes.contains(punkt)) {        //jeżeli był przed procesowaniem to dajemy go do kolejki ale juz jest wkolejce
                        queueOfCost.add(new CostObject(punkt, true, koszt + entry.getValue()));
                        checkedNodes.add(punkt);
                    }
                    if (!(punkt.x == start.x && punkt.y == start.y) && koszt + entry.getValue() < cost.get(punkt).cost) {

                        if (queueOfCost.contains(new CostObject(punkt, true, cost.get(punkt).cost))) {
                            queueOfCost.remove(new CostObject(punkt, true, cost.get(punkt).cost));
                            queueOfCost.add(new CostObject(punkt, true, koszt + entry.getValue()));
                        }
                        cost.replace(punkt, new CostObject(punkt, true, koszt + entry.getValue()));
                        parents.replace(punkt, point);

                        Point checkPoint = point;
                        Point previousPoint = point;
                        java.util.List<Point> listOfMovements = new ArrayList<>();
                        listOfMovements.add(point);

                        do {

                            checkPoint = updateParent(checkPoint);

                            if (checkPoint.equals(previousPoint)) {

                                if (listOfMovements.size() > 1) {

                                    listOfMovements.remove(listOfMovements.size() - 1);

                                    checkPoint = listOfMovements.get(listOfMovements.size() - 1);

                                } else {
                                    break;
                                }
                            } else {
                                listOfMovements.add(checkPoint);
                            }
                            previousPoint = checkPoint;
                        }
                        while (!point.equals(checkPoint));

                    }
                }

                graph.get(point).processed = false;
                cost.get(point).processed = false;

            }

            java.util.List<String> result = new ArrayList<>();

            Point childrenPoint = finish;


            createFinalListOfMovements(result, childrenPoint);


            return result;

        }

        private void createFinalListOfMovements(java.util.List<String> result, Point childrenPoint) {
            while (!childrenPoint.equals(start)) {

                int ver = parents.get(childrenPoint).x - childrenPoint.x;
                int hor = parents.get(childrenPoint).y - childrenPoint.y;

                if (ver == 1) {
                    result.add("up");
                } else if (ver == -1) {
                    result.add("down");
                }
                if (hor == 1) {
                    result.add("left");
                } else if (hor == -1) {
                    result.add("right");
                }

                childrenPoint = parents.get(childrenPoint);

            }
        }

        Point updateParent(Point point) {

            for (Map.Entry<Point, Integer> entry : graph.get(point).neighbourMap.entrySet()) {
                int koszt = cost.get(point).cost;
                Point punkt = entry.getKey();
                if (!(punkt.x == start.x && punkt.y == start.y) && cost.get(punkt).cost != Integer.MAX_VALUE && entry.getValue() + koszt < cost.get(punkt).cost) {
                    if (queueOfCost.contains(new CostObject(punkt, true, cost.get(punkt).cost))) {
                        queueOfCost.remove(new CostObject(punkt, true, cost.get(punkt).cost));
                        queueOfCost.add(new CostObject(punkt, true, koszt + entry.getValue()));
                    }
                    cost.replace(punkt, new CostObject(punkt, true, koszt + entry.getValue()));
                    parents.replace(punkt, point);

                    return punkt;
                }
            }
            return point;
        }

        public static java.util.List<String> cheapestPath(int[][] t, java.awt.Point start, java.awt.Point finish) {


            Finder finder = new Finder(t, start, finish);
            List<String> result = finder.metoda();
            Collections.reverse(result);

            return result;
        }


    }


