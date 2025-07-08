package utils;

import java.util.*;

public class GraphStructure {
    private Map<String, Set<Edge>> adjacencyList;
    private boolean isDirected;
    private boolean isWeighted;
    private List<String> vertices;
    private List<Edge> edges;

    public static class Edge implements Comparable<Edge> {
        public String from;
        public String to;
        public double weight;
        public boolean isDirected;

        public Edge(String from, String to, double weight, boolean isDirected) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.isDirected = isDirected;
        }

        public Edge(String from, String to) {
            this(from, to, 1.0, false);
        }

        @Override
        public int compareTo(Edge other) {
            return Double.compare(this.weight, other.weight);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Edge edge = (Edge) obj;
            return from.equals(edge.from) && to.equals(edge.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public String toString() {
            if (weight == 1.0) {
                return String.format("%s -> %s", from, to);
            } else {
                return String.format("%s -> %s (%.1f)", from, to, weight);
            }
        }
    }

    public static class TraversalResult {
        public List<String> visitOrder;
        public Map<String, String> parent;
        public Map<String, Integer> distance;
        public List<TraversalStep> steps;

        public TraversalResult() {
            this.visitOrder = new ArrayList<>();
            this.parent = new HashMap<>();
            this.distance = new HashMap<>();
            this.steps = new ArrayList<>();
        }
    }

    public static class TraversalStep {
        public enum Type { VISIT, DISCOVER, FINISH, ENQUEUE, DEQUEUE }

        public Type type;
        public String vertex;
        public String from;
        public String description;
        public int step;

        public TraversalStep(Type type, String vertex, String from, String description, int step) {
            this.type = type;
            this.vertex = vertex;
            this.from = from;
            this.description = description;
            this.step = step;
        }
    }

    public static class ShortestPathResult {
        public Map<String, Double> distances;
        public Map<String, String> previous;
        public List<String> path;
        public List<ShortestPathStep> steps;

        public ShortestPathResult() {
            this.distances = new HashMap<>();
            this.previous = new HashMap<>();
            this.path = new ArrayList<>();
            this.steps = new ArrayList<>();
        }
    }

    public static class ShortestPathStep {
        public String vertex;
        public String neighbor;
        public double distance;
        public String operation;
        public int step;

        public ShortestPathStep(String vertex, String neighbor, double distance, String operation, int step) {
            this.vertex = vertex;
            this.neighbor = neighbor;
            this.distance = distance;
            this.operation = operation;
            this.step = step;
        }
    }

    public static class MST {
        public List<Edge> edges;
        public double totalWeight;
        public List<MSTStep> steps;

        public MST() {
            this.edges = new ArrayList<>();
            this.totalWeight = 0.0;
            this.steps = new ArrayList<>();
        }
    }

    public static class MSTStep {
        public Edge edge;
        public String operation;
        public double totalWeight;
        public int step;
        public boolean accepted;

        public MSTStep(Edge edge, String operation, double totalWeight, int step, boolean accepted) {
            this.edge = edge;
            this.operation = operation;
            this.totalWeight = totalWeight;
            this.step = step;
            this.accepted = accepted;
        }
    }

    public GraphStructure(boolean isDirected, boolean isWeighted) {
        this.isDirected = isDirected;
        this.isWeighted = isWeighted;
        this.adjacencyList = new HashMap<>();
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void addVertex(String vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
            adjacencyList.put(vertex, new HashSet<>());
        }
    }

    public void addEdge(String from, String to, double weight) {
        addVertex(from);
        addVertex(to);

        Edge edge = new Edge(from, to, weight, isDirected);

        adjacencyList.get(from).add(edge);
        if (!isDirected) {
            Edge reverseEdge = new Edge(to, from, weight, false);
            adjacencyList.get(to).add(reverseEdge);
        }

        edges.add(edge);
    }

    public void addEdge(String from, String to) {
        addEdge(from, to, 1.0);
    }

    public boolean removeVertex(String vertex) {
        if (!vertices.contains(vertex)) return false;

        // Remover todas las aristas que involucran este vértice
        vertices.remove(vertex);
        adjacencyList.remove(vertex);

        // Remover aristas hacia este vértice
        for (String v : vertices) {
            Set<Edge> edgesToRemove = new HashSet<>();
            for (Edge edge : adjacencyList.get(v)) {
                if (edge.to.equals(vertex)) {
                    edgesToRemove.add(edge);
                }
            }
            adjacencyList.get(v).removeAll(edgesToRemove);
        }

        // Actualizar lista de aristas
        edges.removeIf(edge -> edge.from.equals(vertex) || edge.to.equals(vertex));

        return true;
    }

    public boolean removeEdge(String from, String to) {
        if (!adjacencyList.containsKey(from)) return false;

        boolean removed = false;
        Set<Edge> edgesToRemove = new HashSet<>();

        for (Edge edge : adjacencyList.get(from)) {
            if (edge.to.equals(to)) {
                edgesToRemove.add(edge);
                removed = true;
            }
        }

        adjacencyList.get(from).removeAll(edgesToRemove);

        if (!isDirected && adjacencyList.containsKey(to)) {
            edgesToRemove.clear();
            for (Edge edge : adjacencyList.get(to)) {
                if (edge.to.equals(from)) {
                    edgesToRemove.add(edge);
                }
            }
            adjacencyList.get(to).removeAll(edgesToRemove);
        }

        edges.removeIf(edge ->
                (edge.from.equals(from) && edge.to.equals(to)) ||
                        (!isDirected && edge.from.equals(to) && edge.to.equals(from))
        );

        return removed;
    }

    public TraversalResult dfs(String startVertex) {
        TraversalResult result = new TraversalResult();
        Set<String> visited = new HashSet<>();
        int stepCounter = 1;

        dfsHelper(startVertex, visited, result, stepCounter);

        return result;
    }

    private void dfsHelper(String vertex, Set<String> visited, TraversalResult result, int stepCounter) {
        visited.add(vertex);
        result.visitOrder.add(vertex);
        result.steps.add(new TraversalStep(
                TraversalStep.Type.VISIT, vertex, null,
                "Visitando vértice " + vertex, stepCounter++
        ));

        if (adjacencyList.containsKey(vertex)) {
            for (Edge edge : adjacencyList.get(vertex)) {
                if (!visited.contains(edge.to)) {
                    result.parent.put(edge.to, vertex);
                    result.steps.add(new TraversalStep(
                            TraversalStep.Type.DISCOVER, edge.to, vertex,
                            "Descubriendo " + edge.to + " desde " + vertex, stepCounter++
                    ));
                    dfsHelper(edge.to, visited, result, stepCounter);
                }
            }
        }

        result.steps.add(new TraversalStep(
                TraversalStep.Type.FINISH, vertex, null,
                "Terminando procesamiento de " + vertex, stepCounter++
        ));
    }

    public TraversalResult bfs(String startVertex) {
        TraversalResult result = new TraversalResult();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        int stepCounter = 1;

        queue.add(startVertex);
        visited.add(startVertex);
        result.distance.put(startVertex, 0);

        result.steps.add(new TraversalStep(
                TraversalStep.Type.ENQUEUE, startVertex, null,
                "Encolando vértice inicial " + startVertex, stepCounter++
        ));

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.visitOrder.add(current);

            result.steps.add(new TraversalStep(
                    TraversalStep.Type.DEQUEUE, current, null,
                    "Desencolando y visitando " + current, stepCounter++
            ));

            if (adjacencyList.containsKey(current)) {
                for (Edge edge : adjacencyList.get(current)) {
                    if (!visited.contains(edge.to)) {
                        visited.add(edge.to);
                        queue.add(edge.to);
                        result.parent.put(edge.to, current);
                        result.distance.put(edge.to, result.distance.get(current) + 1);

                        result.steps.add(new TraversalStep(
                                TraversalStep.Type.ENQUEUE, edge.to, current,
                                "Encolando " + edge.to + " desde " + current, stepCounter++
                        ));
                    }
                }
            }
        }

        return result;
    }

    public ShortestPathResult dijkstra(String startVertex) {
        ShortestPathResult result = new ShortestPathResult();
        Set<String> visited = new HashSet<>();
        PriorityQueue<String> pq = new PriorityQueue<>((a, b) ->
                Double.compare(result.distances.getOrDefault(a, Double.MAX_VALUE),
                        result.distances.getOrDefault(b, Double.MAX_VALUE)));

        int stepCounter = 1;

        // Inicializar distancias
        for (String vertex : vertices) {
            result.distances.put(vertex, Double.MAX_VALUE);
        }
        result.distances.put(startVertex, 0.0);
        pq.add(startVertex);

        while (!pq.isEmpty()) {
            String current = pq.poll();

            if (visited.contains(current)) continue;
            visited.add(current);

            result.steps.add(new ShortestPathStep(
                    current, null, result.distances.get(current),
                    "Visitando " + current + " con distancia " + result.distances.get(current),
                    stepCounter++
            ));

            if (adjacencyList.containsKey(current)) {
                for (Edge edge : adjacencyList.get(current)) {
                    if (!visited.contains(edge.to)) {
                        double newDistance = result.distances.get(current) + edge.weight;

                        if (newDistance < result.distances.get(edge.to)) {
                            result.distances.put(edge.to, newDistance);
                            result.previous.put(edge.to, current);
                            pq.add(edge.to);

                            result.steps.add(new ShortestPathStep(
                                    current, edge.to, newDistance,
                                    "Actualizando distancia a " + edge.to + " = " + newDistance,
                                    stepCounter++
                            ));
                        }
                    }
                }
            }
        }

        return result;
    }

    public MST kruskal() {
        MST result = new MST();
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Edge::compareTo);

        UnionFind uf = new UnionFind(vertices);
        int stepCounter = 1;

        for (Edge edge : sortedEdges) {
            if (!uf.connected(edge.from, edge.to)) {
                uf.union(edge.from, edge.to);
                result.edges.add(edge);
                result.totalWeight += edge.weight;

                result.steps.add(new MSTStep(
                        edge, "Agregando arista " + edge.toString(),
                        result.totalWeight, stepCounter++, true
                ));
            } else {
                result.steps.add(new MSTStep(
                        edge, "Rechazando arista " + edge.toString() + " (forma ciclo)",
                        result.totalWeight, stepCounter++, false
                ));
            }
        }

        return result;
    }

    public MST prim(String startVertex) {
        MST result = new MST();
        Set<String> inMST = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>();

        int stepCounter = 1;
        inMST.add(startVertex);

        // Agregar todas las aristas del vértice inicial
        if (adjacencyList.containsKey(startVertex)) {
            pq.addAll(adjacencyList.get(startVertex));
        }

        while (!pq.isEmpty() && inMST.size() < vertices.size()) {
            Edge minEdge = pq.poll();

            if (inMST.contains(minEdge.to)) {
                result.steps.add(new MSTStep(
                        minEdge, "Rechazando arista " + minEdge.toString() + " (ambos vértices en MST)",
                        result.totalWeight, stepCounter++, false
                ));
                continue;
            }

            // Agregar arista al MST
            result.edges.add(minEdge);
            result.totalWeight += minEdge.weight;
            inMST.add(minEdge.to);

            result.steps.add(new MSTStep(
                    minEdge, "Agregando arista " + minEdge.toString(),
                    result.totalWeight, stepCounter++, true
            ));

            // Agregar nuevas aristas candidatas
            if (adjacencyList.containsKey(minEdge.to)) {
                for (Edge edge : adjacencyList.get(minEdge.to)) {
                    if (!inMST.contains(edge.to)) {
                        pq.add(edge);
                    }
                }
            }
        }

        return result;
    }

    public List<String> getPath(String start, String end, Map<String, String> previous) {
        List<String> path = new ArrayList<>();
        String current = end;

        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        if (!path.get(0).equals(start)) {
            return new ArrayList<>(); // No hay camino
        }

        return path;
    }

    public void reset() {
        adjacencyList.clear();
        vertices.clear();
        edges.clear();
    }

    // Getters
    public List<String> getVertices() { return new ArrayList<>(vertices); }
    public List<Edge> getEdges() { return new ArrayList<>(edges); }
    public Set<Edge> getNeighbors(String vertex) {
        return adjacencyList.getOrDefault(vertex, new HashSet<>());
    }
    public boolean isDirected() { return isDirected; }
    public boolean isWeighted() { return isWeighted; }
    public int getVertexCount() { return vertices.size(); }
    public int getEdgeCount() { return edges.size(); }

    // Clase auxiliar para Union-Find (Kruskal)
    private static class UnionFind {
        private Map<String, String> parent;
        private Map<String, Integer> rank;

        public UnionFind(List<String> vertices) {
            parent = new HashMap<>();
            rank = new HashMap<>();

            for (String vertex : vertices) {
                parent.put(vertex, vertex);
                rank.put(vertex, 0);
            }
        }

        public String find(String vertex) {
            if (!parent.get(vertex).equals(vertex)) {
                parent.put(vertex, find(parent.get(vertex)));
            }
            return parent.get(vertex);
        }

        public void union(String a, String b) {
            String rootA = find(a);
            String rootB = find(b);

            if (!rootA.equals(rootB)) {
                if (rank.get(rootA) < rank.get(rootB)) {
                    parent.put(rootA, rootB);
                } else if (rank.get(rootA) > rank.get(rootB)) {
                    parent.put(rootB, rootA);
                } else {
                    parent.put(rootB, rootA);
                    rank.put(rootA, rank.get(rootA) + 1);
                }
            }
        }

        public boolean connected(String a, String b) {
            return find(a).equals(find(b));
        }
    }
}
