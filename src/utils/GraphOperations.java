package utils;

import java.util.*;

public class GraphOperations {

    public static class Graph {
        private Set<String> vertices;
        private Set<Edge> edges;
        private String name;

        public Graph(String name) {
            this.name = name;
            this.vertices = new HashSet<>();
            this.edges = new HashSet<>();
        }

        public void addVertex(String vertex) {
            vertices.add(vertex);
        }

        public void addEdge(String from, String to) {
            vertices.add(from);
            vertices.add(to);
            edges.add(new Edge(from, to));
        }

        public void addEdge(Edge edge) {
            vertices.add(edge.from);
            vertices.add(edge.to);
            edges.add(edge);
        }

        public Set<String> getVertices() { return new HashSet<>(vertices); }
        public Set<Edge> getEdges() { return new HashSet<>(edges); }
        public String getName() { return name; }

        public boolean hasVertex(String vertex) {
            return vertices.contains(vertex);
        }

        public boolean hasEdge(String from, String to) {
            return edges.contains(new Edge(from, to));
        }

        public void clear() {
            vertices.clear();
            edges.clear();
        }

        @Override
        public String toString() {
            return String.format("Grafo %s: V=%s, E=%s", name, vertices, edges);
        }
    }

    public static class Edge {
        public String from;
        public String to;

        public Edge(String from, String to) {
            this.from = from;
            this.to = to;
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
            return from + "-" + to;
        }
    }

    public static class OperationResult {
        public Graph resultGraph;
        public String operationName;
        public List<String> steps;
        public String description;

        public OperationResult(String operationName, String description) {
            this.operationName = operationName;
            this.description = description;
            this.resultGraph = new Graph("Resultado");
            this.steps = new ArrayList<>();
        }
    }

    // Operaciones de Conjuntos
    public static OperationResult union(Graph g1, Graph g2) {
        OperationResult result = new OperationResult("Unión",
                "G₁ ∪ G₂: Grafo con todos los vértices y aristas de ambos grafos");

        result.steps.add("Iniciando operación de unión entre " + g1.getName() + " y " + g2.getName());

        // Unión de vértices
        Set<String> unionVertices = new HashSet<>(g1.getVertices());
        unionVertices.addAll(g2.getVertices());
        result.steps.add("Vértices de " + g1.getName() + ": " + g1.getVertices());
        result.steps.add("Vértices de " + g2.getName() + ": " + g2.getVertices());
        result.steps.add("Unión de vértices: " + unionVertices);

        for (String vertex : unionVertices) {
            result.resultGraph.addVertex(vertex);
        }

        // Unión de aristas
        Set<Edge> unionEdges = new HashSet<>(g1.getEdges());
        unionEdges.addAll(g2.getEdges());
        result.steps.add("Aristas de " + g1.getName() + ": " + g1.getEdges());
        result.steps.add("Aristas de " + g2.getName() + ": " + g2.getEdges());
        result.steps.add("Unión de aristas: " + unionEdges);

        for (Edge edge : unionEdges) {
            result.resultGraph.addEdge(edge);
        }

        result.steps.add("Resultado: " + result.resultGraph.toString());
        return result;
    }

    public static OperationResult intersection(Graph g1, Graph g2) {
        OperationResult result = new OperationResult("Intersección",
                "G₁ ∩ G₂: Grafo con vértices y aristas comunes a ambos grafos");

        result.steps.add("Iniciando operación de intersección entre " + g1.getName() + " y " + g2.getName());

        // Intersección de vértices
        Set<String> intersectionVertices = new HashSet<>(g1.getVertices());
        intersectionVertices.retainAll(g2.getVertices());
        result.steps.add("Vértices comunes: " + intersectionVertices);

        for (String vertex : intersectionVertices) {
            result.resultGraph.addVertex(vertex);
        }

        // Intersección de aristas (solo si ambos vértices están en la intersección)
        Set<Edge> intersectionEdges = new HashSet<>();
        for (Edge edge : g1.getEdges()) {
            if (g2.getEdges().contains(edge) &&
                    intersectionVertices.contains(edge.from) &&
                    intersectionVertices.contains(edge.to)) {
                intersectionEdges.add(edge);
                result.resultGraph.addEdge(edge);
            }
        }
        result.steps.add("Aristas comunes: " + intersectionEdges);
        result.steps.add("Resultado: " + result.resultGraph.toString());

        return result;
    }

    public static OperationResult ringSum(Graph g1, Graph g2) {
        OperationResult result = new OperationResult("Suma Anillo",
                "G₁ ⊕ G₂: (G₁ ∪ G₂) - (G₁ ∩ G₂) - Diferencia simétrica");

        result.steps.add("Iniciando operación de suma anillo entre " + g1.getName() + " y " + g2.getName());

        // Calcular unión e intersección
        OperationResult unionResult = union(g1, g2);
        OperationResult intersectionResult = intersection(g1, g2);

        result.steps.add("Paso 1: Calcular unión");
        result.steps.add("Unión: " + unionResult.resultGraph.toString());
        result.steps.add("Paso 2: Calcular intersección");
        result.steps.add("Intersección: " + intersectionResult.resultGraph.toString());

        // Diferencia simétrica de vértices
        Set<String> ringSumVertices = new HashSet<>(unionResult.resultGraph.getVertices());
        ringSumVertices.removeAll(intersectionResult.resultGraph.getVertices());

        // Agregar vértices que están solo en uno de los grafos
        Set<String> onlyInG1 = new HashSet<>(g1.getVertices());
        onlyInG1.removeAll(g2.getVertices());
        Set<String> onlyInG2 = new HashSet<>(g2.getVertices());
        onlyInG2.removeAll(g1.getVertices());

        ringSumVertices.addAll(onlyInG1);
        ringSumVertices.addAll(onlyInG2);

        result.steps.add("Vértices solo en " + g1.getName() + ": " + onlyInG1);
        result.steps.add("Vértices solo en " + g2.getName() + ": " + onlyInG2);
        result.steps.add("Vértices en suma anillo: " + ringSumVertices);

        for (String vertex : ringSumVertices) {
            result.resultGraph.addVertex(vertex);
        }

        // Diferencia simétrica de aristas
        Set<Edge> ringSumEdges = new HashSet<>(unionResult.resultGraph.getEdges());
        ringSumEdges.removeAll(intersectionResult.resultGraph.getEdges());

        for (Edge edge : ringSumEdges) {
            if (result.resultGraph.hasVertex(edge.from) && result.resultGraph.hasVertex(edge.to)) {
                result.resultGraph.addEdge(edge);
            }
        }

        result.steps.add("Resultado: " + result.resultGraph.toString());
        return result;
    }

    // Operaciones de Producto
    public static OperationResult cartesianProduct(Graph g1, Graph g2) {
        OperationResult result = new OperationResult("Producto Cartesiano",
                "G₁ × G₂: Cada vértice de G₁ conectado con todos los vértices de G₂");

        result.steps.add("Iniciando producto cartesiano entre " + g1.getName() + " y " + g2.getName());

        // Crear vértices del producto cartesiano
        for (String v1 : g1.getVertices()) {
            for (String v2 : g2.getVertices()) {
                String newVertex = "(" + v1 + "," + v2 + ")";
                result.resultGraph.addVertex(newVertex);
            }
        }
        result.steps.add("Vértices del producto: " + result.resultGraph.getVertices());

        // Crear aristas del producto cartesiano
        for (String v1 : g1.getVertices()) {
            for (String v2 : g2.getVertices()) {
                for (String u1 : g1.getVertices()) {
                    for (String u2 : g2.getVertices()) {
                        String vertex1 = "(" + v1 + "," + v2 + ")";
                        String vertex2 = "(" + u1 + "," + u2 + ")";

                        // Arista si (v1=u1 y v2-u2 es arista en G2) o (v1-u1 es arista en G1 y v2=u2)
                        if ((v1.equals(u1) && g2.hasEdge(v2, u2)) ||
                                (v2.equals(u2) && g1.hasEdge(v1, u1))) {
                            result.resultGraph.addEdge(vertex1, vertex2);
                        }
                    }
                }
            }
        }

        result.steps.add("Aristas del producto: " + result.resultGraph.getEdges());
        result.steps.add("Resultado: " + result.resultGraph.toString());
        return result;
    }

    public static OperationResult tensorProduct(Graph g1, Graph g2) {
        OperationResult result = new OperationResult("Producto Tensorial",
                "G₁ ⊗ G₂: Producto con aristas solo cuando hay aristas en ambos grafos");

        result.steps.add("Iniciando producto tensorial entre " + g1.getName() + " y " + g2.getName());

        // Crear vértices del producto tensorial
        for (String v1 : g1.getVertices()) {
            for (String v2 : g2.getVertices()) {
                String newVertex = "(" + v1 + "," + v2 + ")";
                result.resultGraph.addVertex(newVertex);
            }
        }
        result.steps.add("Vértices del producto: " + result.resultGraph.getVertices());

        // Crear aristas del producto tensorial
        for (Edge e1 : g1.getEdges()) {
            for (Edge e2 : g2.getEdges()) {
                String vertex1 = "(" + e1.from + "," + e2.from + ")";
                String vertex2 = "(" + e1.to + "," + e2.to + ")";
                result.resultGraph.addEdge(vertex1, vertex2);
            }
        }

        result.steps.add("Aristas del producto tensorial: " + result.resultGraph.getEdges());
        result.steps.add("Resultado: " + result.resultGraph.toString());
        return result;
    }

    public static OperationResult composition(Graph g1, Graph g2) {
        OperationResult result = new OperationResult("Composición",
                "G₁ ∘ G₂: Composición de grafos con conectividad transitiva");

        result.steps.add("Iniciando composición entre " + g1.getName() + " y " + g2.getName());

        // Crear vértices de la composición
        for (String v1 : g1.getVertices()) {
            for (String v2 : g2.getVertices()) {
                String newVertex = "(" + v1 + "," + v2 + ")";
                result.resultGraph.addVertex(newVertex);
            }
        }
        result.steps.add("Vértices de la composición: " + result.resultGraph.getVertices());

        // Crear aristas de la composición
        for (String v1 : g1.getVertices()) {
            for (String v2 : g2.getVertices()) {
                for (String u1 : g1.getVertices()) {
                    for (String u2 : g2.getVertices()) {
                        String vertex1 = "(" + v1 + "," + v2 + ")";
                        String vertex2 = "(" + u1 + "," + u2 + ")";

                        // Arista si v1-u1 en G1 o (v1=u1 y v2-u2 en G2)
                        if (g1.hasEdge(v1, u1) || (v1.equals(u1) && g2.hasEdge(v2, u2))) {
                            result.resultGraph.addEdge(vertex1, vertex2);
                        }
                    }
                }
            }
        }

        result.steps.add("Aristas de la composición: " + result.resultGraph.getEdges());
        result.steps.add("Resultado: " + result.resultGraph.toString());
        return result;
    }
}
