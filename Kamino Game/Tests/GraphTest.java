package cpen221.mp2;

import cpen221.mp2.controllers.Kamino;
import cpen221.mp2.graph.Edge;
import cpen221.mp2.graph.Graph;
import cpen221.mp2.graph.Vertex;
import cpen221.mp2.spaceship.MillenniumFalcon;
import cpen221.mp2.views.QuietView;
import org.junit.Test;

import java.util.*;

import org.junit.Assert;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GraphTest {

    @Test
    public void testShortestPath() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 6);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 5);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 1);
        Edge<Vertex> e4 = new Edge<>(v2, v4, 2);
        Edge<Vertex> e5 = new Edge<>(v2, v5, 2);
        Edge<Vertex> e6 = new Edge<>(v4, v5, 1);
        Edge<Vertex> e7 = new Edge<>(v3, v5, 5);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);

        List<Vertex> expected = new ArrayList<>();
        expected.add(v1);
        expected.add(v4);
        expected.add(v5);
        expected.add(v3);

        assertEquals(expected, g.shortestPath(v1, v3));
    }

    @Test
    public void testShortestPathNoPath() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(4, "E");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 6);
        Edge<Vertex> e2 = new Edge<>(v4, v5, 1);
        Edge<Vertex> e3 = new Edge<>(v3, v5, 5);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);

        List<Vertex> expected = new ArrayList<>();

        assertEquals(expected, g.shortestPath(v1, v3));
    }

    @Test
    public void testPathLength() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(4, "E");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 6);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 5);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 1);
        Edge<Vertex> e4 = new Edge<>(v2, v4, 2);
        Edge<Vertex> e5 = new Edge<>(v2, v5, 2);
        Edge<Vertex> e6 = new Edge<>(v4, v5, 1);
        Edge<Vertex> e7 = new Edge<>(v3, v5, 5);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);

        assertEquals(7, g.pathLength(g.shortestPath(v1, v3)));
    }

    @Test
    public void testMST() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 2);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 4);
        Edge<Vertex> e3 = new Edge<>(v3, v4, 3);
        Edge<Vertex> e4 = new Edge<>(v4, v1, 5);
        Edge<Vertex> e5 = new Edge<>(v2, v5, 6);
        Edge<Vertex> e6 = new Edge<>(v1, v6, 5);
        Edge<Vertex> e7 = new Edge<>(v4, v6, 1);
        Edge<Vertex> e8 = new Edge<>(v3, v5, 7);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);

        //Graph with a loop. Compute Tree.
        List<Edge> expected = new ArrayList<>();
        expected.add(e1);
        expected.add(e3);
        expected.add(e2);
        expected.add(e5);

        assertEquals(expected, g.minimumSpanningTree());

        //remove edge e2. Compute new tree.
        g.remove(e2);
        expected.clear();
        expected.add(e1);
        expected.add(e3);
        expected.add(e4);
        expected.add(e5);
        assertEquals(expected, g.minimumSpanningTree());

        //add e6 same length as e4. Compute new tree.
        g.addVertex(v6);
        g.addEdge(e6);
        expected.remove(e5);
        expected.add(e6);
        expected.add(e5);
        assertEquals(expected, g.minimumSpanningTree());

        //Triple Loop Graph.
        g.addEdge(e2);
        g.addEdge(e7);
        g.addEdge(e8);
        expected.clear();
        expected.add(e7);
        expected.add(e1);
        expected.add(e3);
        expected.add(e2);
        expected.add(e5);
        assertEquals(expected, g.minimumSpanningTree());
    }

    @Test
    public void testDiameter() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 1);
        Edge<Vertex> e2 = new Edge<>(v1, v3, 2);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 3);
        Edge<Vertex> e4 = new Edge<>(v1, v5, 4);

        Edge<Vertex> e5 = new Edge<>(v2, v3, 5);
        Edge<Vertex> e6 = new Edge<>(v3, v4, 6);
        Edge<Vertex> e7 = new Edge<>(v4, v5, 7);
        Edge<Vertex> e8 = new Edge<>(v5, v2, 8);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);
        g.addEdge(e8);

        //kite graph.
        assertEquals(7, g.diameter());
        //remove edge force a longer shortest path.
        g.remove(e2);
        assertEquals(10, g.diameter());
        //remove all 't' edges in the middle. Path only on the perimeter.
        g.remove(e1);
        g.remove(e3);
        g.remove(e4);
        g.remove(v1);
        assertEquals(13, g.diameter());
    }

    @Test
    public void testDiameterComp() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");
        Vertex v7 = new Vertex(7, "G");
        Vertex v8 = new Vertex(8, "H");
        Vertex v9 = new Vertex(9, "I");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 1);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 1);
        Edge<Vertex> e3 = new Edge<>(v2, v4, 1);
        Edge<Vertex> e4 = new Edge<>(v4, v5, 1);

        Edge<Vertex> e5 = new Edge<>(v6, v7, 5);
        Edge<Vertex> e6 = new Edge<>(v7, v8, 5);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addVertex(v7);
        g.addVertex(v8);
        g.addVertex(v9);

        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);

        // test component
        assertEquals(5, g.component(v1).size());
        assertEquals(5, g.component(v2).size());
        assertEquals(5, g.component(v3).size());
        assertEquals(5, g.component(v4).size());
        assertEquals(5, g.component(v5).size());
        assertEquals(3, g.component(v6).size());
        assertEquals(3, g.component(v7).size());
        assertEquals(3, g.component(v8).size());
        assertEquals(1, g.component(v9).size());

        // test diameter
        assertEquals(3, g.diameter());

        // remove edge and test diameter
        g.remove(e3);
        assertEquals(10, g.diameter());
    }

    @Test
    public void testSearch() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 9);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 11);
        Edge<Vertex> e3 = new Edge<>(v3, v4, 17);
        Edge<Vertex> e4 = new Edge<>(v4, v1, 4);
        Edge<Vertex> e5 = new Edge<>(v1, v5, 2);
        Edge<Vertex> e6 = new Edge<>(v4, v5, 6);
        Edge<Vertex> shortcut = new Edge<>(v1, v3, 18);

        //House shaped graph.
        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);

        HashSet<Vertex> inRange = new HashSet<>();
        inRange.add(v1);
        inRange.add(v2);
        inRange.add(v4);

        //V3 too far from V5
        assertEquals(inRange, g.search(v5, 20));

        //Give V3 a shortcut from V5
        g.addEdge(shortcut);
        inRange.add(v3);
        assertEquals(inRange, g.search(v5, 20));
    }

    @Test
    public void testGraph() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");

        v6.updateName("G");
        assertTrue(v1.name().equals("A"));
        assertTrue(v1.id() == 1);

        Edge<Vertex> e1 = new Edge<>(v1, v2, 200);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 300);
        Edge<Vertex> e3 = new Edge<>(v3, v4, 250);
        Edge<Vertex> e4 = new Edge<>(v4, v1, 150);
        Edge<Vertex> e6 = new Edge<>(v1, v6, 50);


        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);

        //edge case for add vertex
        assertTrue(!g.addVertex(null));
        assertTrue(!g.addVertex(v1));

        //edge case for add edge
        assertTrue(!g.addEdge(e6));

        //test edge.
        assertTrue(g.edge(v1, v2));
        assertTrue(g.edge(v2, v1));

        //test edge length sum
        assertEquals(900, g.edgeLengthSum());

        //test get edge.
        assertEquals(e1, g.getEdge(v1, v2));
        try{
            g.getEdge(v1, v5);
            System.out.println("failed");
        }
        catch (NoSuchElementException e) {
            System.out.println("passed get edge test");
        }

        //test edge length
        try{
            g.edgeLength(v1, v5);
            System.out.println("failed");
        }
        catch (NoSuchElementException e) {
            System.out.println("passed edge length test.");
        }

        //testing disjointed graph search
        g.remove(e3);
        g.remove(e4);
        Edge<Vertex> e5 = new Edge<>(v4, v5, 100);
        g.addVertex(v6);
        g.addEdge(e5);
        g.addEdge(e6);
        Set<Vertex> range = new HashSet<>();
        range.add(v2);
        range.add(v3);
        range.add(v6);
        assertEquals(range, g.search(v1, 500));
    }

    @Test
    public void testFalcon() {
        Kamino test = new Kamino(6645529480803663035L, new MillenniumFalcon(), new QuietView());
        String[] arg = {"--quiet"};
        test.main(arg);

        Assert.assertTrue(test.huntSucceeded());
        Assert.assertTrue(test.gatherSucceeded());

        Kamino test1 = new Kamino(-2477641376863858799L, new MillenniumFalcon(), new QuietView());
        String[] arg2 = {"--quiet"};
        test1.main(arg);
        Assert.assertTrue(test1.huntSucceeded());
        Assert.assertTrue(test1.gatherSucceeded());

        Kamino test2 = new Kamino(3307011078434949678L, new MillenniumFalcon(), new QuietView());
        String[] arg3 = {"--quiet"};
        test1.main(arg3);
        Assert.assertTrue(test2.huntSucceeded());
        Assert.assertTrue(test2.gatherSucceeded());

        Kamino test3 = new Kamino(-7610621359446545191L, new MillenniumFalcon(), new QuietView());
        String[] arg4 = {"--quiet"};
        test1.main(arg4);
        Assert.assertTrue(test3.huntSucceeded());
        Assert.assertTrue(test3.gatherSucceeded());

    }

}
