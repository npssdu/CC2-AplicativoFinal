package grafos;

import java.util.*;

/**
 * Clase que representa un grafo con nodos y aristas.
 * Utilizada para operaciones de teoría de grafos.
 */
public class Graph {
    private List<String> nodes;
    private List<String[]> edges;

    /**
     * Constructor que crea un grafo con nodos y aristas específicos.
     * @param nodes Lista de nodos del grafo
     * @param edges Lista de aristas del grafo (cada arista es un array de 2 elementos)
     */
    public Graph(List<String> nodes, List<String[]> edges) {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
    }

    /**
     * Constructor que crea un grafo vacío.
     */
    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * Obtiene la lista de nodos del grafo.
     * @return Lista de nodos
     */
    public List<String> getNodes() {
        return new ArrayList<>(nodes);
    }

    /**
     * Obtiene la lista de aristas del grafo.
     * @return Lista de aristas
     */
    public List<String[]> getEdges() {
        return new ArrayList<>(edges);
    }

    /**
     * Establece los nodos del grafo.
     * @param nodes Nueva lista de nodos
     */
    public void setNodes(List<String> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    /**
     * Establece las aristas del grafo.
     * @param edges Nueva lista de aristas
     */
    public void setEdges(List<String[]> edges) {
        this.edges = new ArrayList<>(edges);
    }

    /**
     * Agrega un nodo al grafo.
     * @param node Nodo a agregar
     */
    public void addNode(String node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    /**
     * Agrega una arista al grafo.
     * @param edge Arista a agregar (array de 2 elementos)
     */
    public void addEdge(String[] edge) {
        if (edge.length == 2 && nodes.contains(edge[0]) && nodes.contains(edge[1])) {
            edges.add(edge);
        }
    }

    /**
     * Verifica si el grafo contiene un nodo específico.
     * @param node Nodo a verificar
     * @return true si el grafo contiene el nodo, false en caso contrario
     */
    public boolean containsNode(String node) {
        return nodes.contains(node);
    }

    /**
     * Verifica si el grafo contiene una arista específica.
     * @param edge Arista a verificar
     * @return true si el grafo contiene la arista, false en caso contrario
     */
    public boolean containsEdge(String[] edge) {
        if (edge.length != 2) return false;

        for (String[] existingEdge : edges) {
            if ((existingEdge[0].equals(edge[0]) && existingEdge[1].equals(edge[1])) ||
                    (existingEdge[0].equals(edge[1]) && existingEdge[1].equals(edge[0]))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el número de nodos del grafo.
     * @return Número de nodos
     */
    public int getNodeCount() {
        return nodes.size();
    }

    /**
     * Obtiene el número de aristas del grafo.
     * @return Número de aristas
     */
    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Verifica si el grafo está vacío (sin nodos).
     * @return true si el grafo está vacío, false en caso contrario
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Crea una copia del grafo.
     * @return Nueva instancia de Graph con los mismos nodos y aristas
     */
    public Graph copy() {
        List<String> newNodes = new ArrayList<>(nodes);
        List<String[]> newEdges = new ArrayList<>();

        for (String[] edge : edges) {
            newEdges.add(new String[]{edge[0], edge[1]});
        }

        return new Graph(newNodes, newEdges);
    }

    /**
     * Obtiene una representación en cadena del grafo.
     * @return Representación en cadena del grafo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph{");
        sb.append("nodes=").append(nodes);
        sb.append(", edges=[");

        for (int i = 0; i < edges.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("(").append(edges.get(i)[0]).append("-").append(edges.get(i)[1]).append(")");
        }

        sb.append("]}");
        return sb.toString();
    }

    /**
     * Convierte las aristas a un conjunto de cadenas para facilitar comparaciones.
     * @return Conjunto de cadenas que representan las aristas
     */
    public Set<String> getEdgeSet() {
        Set<String> edgeSet = new HashSet<>();
        for (String[] edge : edges) {
            // Normalizar la arista ordenando los nodos
            String[] sorted = {edge[0], edge[1]};
            Arrays.sort(sorted);
            edgeSet.add(sorted[0] + "-" + sorted[1]);
        }
        return edgeSet;
    }

    /**
     * Obtiene los vecinos de un nodo específico.
     * @param node Nodo del cual obtener los vecinos
     * @return Lista de nodos vecinos
     */
    public List<String> getNeighbors(String node) {
        List<String> neighbors = new ArrayList<>();
        for (String[] edge : edges) {
            if (edge[0].equals(node) && !neighbors.contains(edge[1])) {
                neighbors.add(edge[1]);
            } else if (edge[1].equals(node) && !neighbors.contains(edge[0])) {
                neighbors.add(edge[0]);
            }
        }
        return neighbors;
    }

    /**
     * Verifica si dos grafos son iguales.
     * @param obj Objeto a comparar
     * @return true si los grafos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Graph graph = (Graph) obj;

        // Comparar nodos
        if (!new HashSet<>(nodes).equals(new HashSet<>(graph.nodes))) {
            return false;
        }

        // Comparar aristas
        return getEdgeSet().equals(graph.getEdgeSet());
    }

    /**
     * Calcula el hash del grafo.
     * @return Hash del grafo
     */
    @Override
    public int hashCode() {
        return Objects.hash(new HashSet<>(nodes), getEdgeSet());
    }
}
