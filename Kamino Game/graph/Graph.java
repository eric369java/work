package cpen221.mp2.graph;

import java.util.*;

/**
 * Represents a graph with vertices of type V.
 *
 * @param <V> represents a vertex type
 */
public class Graph<V extends Vertex, E extends Edge<V>> implements ImGraph<V, E>, IGraph<V, E> {

    private HashMap<V, ArrayList<E> > IGraph = new HashMap<>();
    /*
     Rep invariant is
     for(V vertex : all vertices) vertex != null && vertex id is unique.

     Abstraction function is
     vertex = a key in IGraph.
     list of all edges that the vertex is connected to = the list that the vertex is mapped to.
     graph = a hashmap of vertices to its connected edges.
     */
    ////////////////////////////////////////////// IGraph methods /////////////////////////////////////////////////////

    /**
     *Create an empty graph.
     */
    public Graph() {}

    /**
     * Add a vertex to the graph.
     *
     * @param v vertex to add
     * @return true if the vertex was added successfully and false otherwise.
     */
    public boolean addVertex(V v) {

        //if attempt to add key that already exists or is null return false;
        if(IGraph.containsKey(v) || v == null) return false;

        //else successfully add the vertex into graph and check.
        IGraph.put(v, new ArrayList<>());
        return this.vertex(v);
    }

    /**
     * Check if a vertex is part of the graph
     *
     * @param v vertex to check in the graph
     * @return true of v is part of the graph and false otherwise
     */
    public boolean vertex(V v) {
        return IGraph.containsKey(v);
    }

    /**
     * Add an edge of the graph.
     *
     * @param e the edge to add to the graph
     * @return true if the edge was successfully added and false otherwise
     */
    public boolean addEdge(E e) {

        // return false if vertices are not in the graph
        if (!this.vertex(e.v1()) || !this.vertex(e.v2())) return false;

        // add edge to its vertices
        this.IGraph.get(e.v1()).add(e);
        this.IGraph.get(e.v2()).add(e);

        //return false if edge was not added successfully.
        return IGraph.get(e.v1()).contains(e) && IGraph.get(e.v2()).contains(e);
    }

    /**
     * Check if an edge is part of the graph.
     *
     * @param e the edge to check in the graph
     * @return true if e is an edge in the graph and false otherwise
     */
    public boolean edge(E e) {
        return this.vertex(e.v1()) && this.vertex(e.v2()) &&
                IGraph.get(e.v1()).contains(e) && IGraph.get(e.v2()).contains(e);
    }

    /**
     * Check if v1-v2 is an edge in the graph
     *
     * @param v1 the first vertex of the edge
     * @param v2 the second vertex of the edge
     * @return true of the v1-v2 edge is part of the graph and false otherwise
     */
    public boolean edge(V v1, V v2) {
        if (!this.vertex(v1) || !this.vertex(v2) || v1.equals(v2)) return false;

        Edge<V> e = new Edge<>(v1, v2);

        boolean contained1 = IGraph.get(v1).contains(e);
        boolean contained2 = IGraph.get(v2).contains(e);

        return contained1 && contained2;
    }

    /**
     * Determine the length on an edge in the graph
     *
     * @param v1 the first vertex of the edge
     * @param v2 the second vertex of the edge
     * @return the length of the v1-v2 edge if this edge is part of the graph
     */
    public int edgeLength(V v1, V v2) throws NoSuchElementException {
        if (this.edge(v1, v2)) for (E e:IGraph.get(v1)) if (e.incident(v1) && e.incident(v2)) return e.length();
        throw new NoSuchElementException();
    }

    /**
     * Find the edge that connects two vertices if such an edge exists
     *
     * @param v1 one end of the edge
     * @param v2 the other end of the edge
     * @return the edge connecting v1 and v2
     */
    public E getEdge(V v1, V v2) throws NoSuchElementException {
        if (this.edge(v1, v2)) for (E e:IGraph.get(v1)) if (e.incident(v1) && e.incident(v2)) return e;
        throw new NoSuchElementException();
    }

    /**
     * Obtain the sum of the lengths of all edges in the graph
     *
     * @return the sum of the lengths of all edges in the graph
     */
    public int edgeLengthSum() {
        int sum = 0;
        for (E e:this.allEdges()) sum += e.length();
        return sum;
    }

    /**
     * Remove an edge from the graph
     *
     * @param e the edge to remove
     * @return true if e was successfully removed and false otherwise
     */
    public boolean remove(E e) {
        IGraph.get(e.v1()).remove(e);
        IGraph.get(e.v2()).remove(e);
        return !this.edge(e);
    }

    /**
     * Remove a vertex from the graph
     *
     * @param v the vertex to remove
     * @return true if v was successfully removed and false otherwise
     */
    public boolean remove(V v) {
        if (!this.vertex(v)) return false;
        for (E e:IGraph.get(v)) this.remove(e);
        this.IGraph.remove(v);
        return true;
    }

    /**
     * Obtain a set of all vertices in the graph.
     * Access to this set **should not** permit graph mutations.
     *
     * @return a set of all vertices in the graph
     */
    public Set<V> allVertices() {
        return IGraph.keySet();
    }

    /**
     * Obtain a set of all vertices incident on v.
     * Access to this set **should not** permit graph mutations.
     *
     * @param v the vertex of interest
     * @return all edges incident on v
     */
    public Set<E> allEdges(V v) {
        return new HashSet<>(IGraph.get(v));
    }

    /**
     * Obtain a set of all edges in the graph.
     * Access to this set **should not** permit graph mutations.
     *
     * @return all edges in the graph
     */
    public Set<E> allEdges() {
        Set<E> edges = new HashSet<>();
        for (ArrayList<E> list:this.IGraph.values()) edges.addAll(list);
        return edges;
    }

    /**
     * Obtain all the neighbours of vertex v.
     * Access to this map **should not** permit graph mutations.
     *
     * @param v is the vertex whose neighbourhood we want.
     * @return a map containing each vertex w that neighbors v and the edge between v and w
     */
    public Map<V, E> getNeighbours(V v) {
        Map<V, E> neighbours = new HashMap<>();
        for (E e:this.allEdges(v)) {
            if (e.v1().equals(v)) neighbours.put(e.v2(), e);
            if (e.v2().equals(v)) neighbours.put(e.v1(), e);
        }
        return neighbours;
    }

    ////////////////////////////////////////////// ImGraph methods ////////////////////////////////////////////////////

    /**
     * Compute the shortest path from source to sink
     *
     * @param source the start vertex
     * @param sink   the end vertex
     * @return the vertices, in order, on the shortest path from source to sink (both end points are part of the list)
     */
    public List<V> shortestPath(V source, V sink) {

        // if source or sink not in graph, return empty list
        if (!this.vertex(source) || !this.vertex(sink)) return new ArrayList<>();

        // list of all vertex
        List<V> vertex = new ArrayList<>(this.allVertices());

        // array of min distance to source vertex initialized to max
        int[] minDistance = new int[vertex.size()];
        Arrays.fill(minDistance, Integer.MAX_VALUE);

        // array of previous vertex of the shorted path to source
        List<V> preVertex = new ArrayList<>();
        for (int i = 0; i < vertex.size(); i++) preVertex.add(null);

        // set of unvisited vertex
        Set<Vertex> unvisited = new HashSet<>(this.allVertices());

        // set distance from source to source to zero
        minDistance[vertex.indexOf(source)] = 0;

        // set initial current vertex to source
        V currentVertex = source;
        int currentIndex = vertex.indexOf(source);

        while (unvisited.size() > 0) {

            // for each unvisited neighbour of current vertex
            for (Map.Entry<V, E> entry:this.getNeighbours(currentVertex).entrySet()) {
                if (!unvisited.contains(entry.getKey())) continue;

                // calculate distance from source
                int distance = minDistance[currentIndex] + entry.getValue().length();

                // if new distance less than new distance
                if (distance < minDistance[vertex.indexOf(entry.getKey())]) {

                    // update min distance from source
                    minDistance[vertex.indexOf(entry.getKey())] = distance;

                    // update previous vertex with current vertex
                    preVertex.set(vertex.indexOf(entry.getKey()), currentVertex);
                }
            }

            // remove current vertex from unvisited
            unvisited.remove(currentVertex);

            // find the min distance unvisited vertex
            int nextDistance = Integer.MAX_VALUE;
            int nextIndex = -1;
            for (int i = 0; i < minDistance.length; i++)
                if (unvisited.contains(vertex.get(i)) && minDistance[i] < nextDistance) {
                    nextDistance = minDistance[i];
                    nextIndex = i;
                }

            // update current vertex, break if no unvisited vertex exist
            if (nextIndex == -1) break;
            currentIndex = nextIndex;
            currentVertex = vertex.get(currentIndex);
        }

        // create path
        List<V> path = new ArrayList<>();
        path.add(sink);

        // populate path from result list
        try {
            while (!path.get(path.size() - 1).equals(source))
                path.add(preVertex.get(vertex.indexOf(path.get(path.size() - 1))));
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }

        // reverse path to get from source to sink
        Collections.reverse(path);
        return path;
    }

    /**
     * Compute the minimum spanning tree of the graph.
     * See https://en.wikipedia.org/wiki/Minimum_spanning_tree
     *
     * @return a list of edges that forms a minimum spanning tree of the graph
     */
    public List<E> minimumSpanningTree() {

        // list of all edged in ascending length
        List<E> allE = new LinkedList<>(this.allEdges());
        allE.sort(Comparator.comparingInt(Edge::length));

        // return list of edges in ascending length
        List<E> shortestSpanningTree = new ArrayList<>();

        // create groups with each vertex as its own group
        List<Set<V>> groups = new ArrayList<>();
        for (V v:this.allVertices()) {
            Set<V> initial = new HashSet<>();
            initial.add(v);
            groups.add(initial);
        }

        for (E e:allE) {
            int index1 = 0, index2 = 0;

            // fetch group number for each vertex
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).contains(e.v1())) index1 = i;
                if (groups.get(i).contains(e.v2())) index2 = i;
            }

            // move on if a cycle would be created if vertex are in the same group
            if (index1 == index2) continue;

            // add edge to MST
            shortestSpanningTree.add(e);

            // merge groups
            groups.get(index1).addAll(groups.get(index2));
            groups.remove(index2);
        }

        return shortestSpanningTree;
    }

    /**
     * Compute the length of a given path
     *
     * @param path indicates the vertices on the given path
     * @return the length of path
     */
    public int pathLength(List<V> path) {
        int length = 0;
        for (int i = 0; i < path.size() - 1; i++) length += this.edgeLength(path.get(i), path.get(i + 1));
        return length;
    }

    /**
     * Obtain all vertices w that are no more than a <em>path distance</em> of range from v.
     *
     * @param v     the vertex to start the search from.
     * @param range the radius of the search.
     * @return a set of vertices that are within range of v (this set does not contain v).
     */
    public Set<V> search(V v, int range) {
        Set<V> all = new HashSet<>();
        for (V i:this.allVertices()) if (!i.equals(v)
                && this.pathLength(this.shortestPath(v, i)) <= range
                && this.pathLength(this.shortestPath(v, i)) > 0) all.add(i);
        return all;
    }

    /**
     * Compute the diameter of the graph.
     * <ul>
     * <li>The diameter of a graph is the length of the longest shortest path in the graph.</li>
     * <li>If a graph has multiple components then we will define the diameter
     * as the diameter of the largest component.</li>
     * </ul>
     *
     * @return the diameter of the graph.
     */
    public int diameter() {
        int diameter = 0;
        int maxSize = 0;

        List<V> all = new ArrayList<>(this.allVertices());

        // return 0 if there are no edges
        if (this.allEdges().size() == 0) return 0;

        // compute diameter of largest component
        for (int i = 0; i < all.size(); i++) for (int j = i; j < all.size(); j++) {

            // continue if two vertices are not in the same component
            if (!this.component(all.get(i)).equals(this.component(all.get(j)))) continue;

            // continue if vertices are in a smaller component
            if (this.component(all.get(i)).size() < maxSize) continue;

            List<V> path = this.shortestPath(all.get(i), all.get(j));
            int length = this.pathLength(path);

            // update diameter regardless if component is larger
            if (this.component(all.get(i)).size() > maxSize) {
                diameter = length;
                maxSize = this.component(all.get(i)).size();

            // compare and update diameter if component is the same size
            } else if (length > diameter) {
                diameter = length;
                maxSize = this.component(all.get(i)).size();
            }
        }

        return diameter;
    }

    /**
     * Find all vertices connected to a root vertex in the graph
     *
     * @param root the root vertex
     * @return all vertices connected to root in the same component, empty if none are connected to it
     */
    public Set<V> component(V root) {
        return this.searchRoot(root, new HashSet<>());
    }

    /**
     * A helper method that finds all vertices connected to a root vertex in the graph
     *
     * @param current the vertex in focus
     * @param visited the vertices already visited
     * @return all unvisited vertices connected to the current vertex
     */
    private Set<V> searchRoot(V current, Set<V> visited) {
        Set<V> vertices = new HashSet<>();
        vertices.add(current);
        visited.add(current);

        // find all unvisited neighbours
        Set<V> neighbours = new HashSet<>();
        for (V n:this.getNeighbours(current).keySet()) if (!visited.contains(n)) neighbours.add(n);

        // return current vertex is there is no unvisited neighbours
        if (neighbours.isEmpty()) return vertices;

        // use recursion to search down the branch for all vertices
        for (V neighbour:neighbours) vertices.addAll(this.searchRoot(neighbour, visited));
        return vertices;
    }

    ///////////////////////////////////////// add all new code above this line ////////////////////////////////////////

    /**
     * This method removes some edges at random while preserving connectivity
     * <p>
     * DO NOT CHANGE THIS METHOD
     * </p>
     * <p>
     * You will need to implement allVertices() and allEdges(V v) for this
     * method to run correctly
     *</p>
     * <p><strong>requires:</strong> this graph is connected</p>
     *
     * @param rng random number generator to select edges at random
     */
    public void pruneRandomEdges(Random rng) {
        class VEPair {
            V v;
            E e;

            public VEPair(V v, E e) {
                this.v = v;
                this.e = e;
            }
        }
        /* Visited Nodes */
        Set<V> visited = new HashSet<>();
        /* Nodes to visit and the cpen221.mp2.graph.Edge used to reach them */
        Deque<VEPair> stack = new LinkedList<VEPair>();
        /* Edges that could be removed */
        ArrayList<E> candidates = new ArrayList<>();
        /* Edges that must be kept to maintain connectivity */
        Set<E> keep = new HashSet<>();

        V start = null;
        for (V v : this.allVertices()) {
            start = v;
            break;
        }
        if (start == null) {
            // nothing to do
            return;
        }
        stack.push(new VEPair(start, null));
        while (!stack.isEmpty()) {
            VEPair pair = stack.pop();
            if (visited.add(pair.v)) {
                keep.add(pair.e);
                for (E e : this.allEdges(pair.v)) {
                    stack.push(new VEPair(e.distinctVertex(pair.v), e));
                }
            } else if (!keep.contains(pair.e)) {
                candidates.add(pair.e);
            }
        }
        // randomly trim some candidate edges
        int iterations = rng.nextInt(candidates.size());
        for (int count = 0; count < iterations; ++count) {
            int end = candidates.size() - 1;
            int index = rng.nextInt(candidates.size());
            E trim = candidates.get(index);
            candidates.set(index, candidates.get(end));
            candidates.remove(end);
            remove(trim);
        }
    }
}
