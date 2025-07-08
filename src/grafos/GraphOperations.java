package grafos;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase que implementa las operaciones entre grafos.
 * Incluye operaciones de conjuntos y productos.
 */
public class GraphOperations {

    /**
     * Calcula las operaciones de conjuntos entre dos grafos.
     * @param g1 Primer grafo
     * @param g2 Segundo grafo
     * @return Mapa con los resultados de las operaciones
     */
    public Map<String, Graph> calculateSetOperations(Graph g1, Graph g2) {
        Map<String, Graph> results = new HashMap<>();

        results.put("union", calculateUnion(g1, g2));
        results.put("intersection", calculateIntersection(g1, g2));
        results.put("ringSum", calculateRingSum(g1, g2));

        return results;
    }

    /**
     * Calcula las operaciones de producto entre dos grafos.
     * @param g1 Primer grafo
     * @param g2 Segundo grafo
     * @return Mapa con los resultados de las operaciones
     */
    public Map<String, Graph> calculateProductOperations(Graph g1, Graph g2) {
        Map<String, Graph> results = new HashMap<>();

        results.put("cartesian", calculateCartesianProduct(g1, g2));
        results.put("tensor", calculateTensorProduct(g1, g2));
        results.put("composition", calculateComposition(g1, g2));

        return results;
    }

    /**
     * Calcula la unión de dos grafos.
     * La unión contiene todos los nodos y aristas de ambos grafos.
     * @param g1 Primer grafo
     * @param g2 Segundo grafo
     * @return Grafo resultado de la unión
     */
    private Graph calculateUnion(Graph g1, Graph g2) {
        Set<String> unionNodes = new HashSet<>();
        unionNodes.addAll(g1.getNodes());
        unionNodes.addAll(g2.getNodes());

        Set<String> unionEdgesSet = new HashSet<>();
        unionEdgesSet.addAll(g1.getEdgeSet());
        unionEdgesSet.addAll(g2.getEdgeSet());

        List<String[]> unionEdges = unionEdgesSet.stream()
                .map(edge -> edge.split("-"))
                .collect(Collectors.toList());

        return new Graph(new ArrayList<>(unionNodes), unionEdges);
    }

    /**
     * Calcula la intersección de dos grafos.
     * La intersección contiene solo los nodos y aristas comunes.
     * @param g1 Primer grafo
     * @param g2 Segundo grafo
     * @return Grafo resultado de la intersección
     */
    private Graph calculateIntersection(Graph g1, Graph g2) {
        Set<String> intersectionNodes = new HashSet<>(g1.getNodes());
        intersectionNodes.retainAll(g2.getNodes());

        Set<String> g1EdgeSet = g1.getEdgeSet();
        Set<String> g2EdgeSet = g2.getEdgeSet();
        Set<String> intersectionEdgesSet = new HashSet<>(g1EdgeSet);
        intersectionEdgesSet.retainAll(g2EdgeSet);

        List<String[]> intersectionEdges = intersectionEdgesSet.stream()
                .map(edge -> edge.split("-"))
                .filter(edge -> intersectionNodes.contains(edge[0]) && intersectionNodes.contains(edge[1]))
                .collect(Collectors.toList());

        return new Graph(new ArrayList<>(intersectionNodes), intersectionEdges);
    }

    /**
     * Calcula la suma anillo (diferencia simétrica) de dos grafos.
     * Contiene nodos y aristas que están en uno u otro grafo, pero no en ambos.
     * @param g1 Primer grafo
     * @param g2 Segundo grafo
     * @return Grafo resultado de la suma anillo
     */
    private Graph calculateRingSum(Graph g1, Graph g2) {
        Set<String> unionNodes = new HashSet<>();
        unionNodes.addAll(g1.getNodes());
        unionNodes.addAll(g2.getNodes());

        Set<String> g1EdgeSet = g1.getEdgeSet();
        Set<String> g2EdgeSet = g2.getEdgeSet();
        Set<String> ringSumEdgesSet = new HashSet<>();

        // Aristas en g1 pero no en g2
        for (String edge : g1EdgeSet) {
            if (!g2EdgeSet.contains(edge)) {
                ringSumEdgesSet.add(edge);
            }
        }

        // Aristas en g2 pero no en g1
        for (String edge : g2EdgeSet) {
            if (!g1EdgeSet.contains(edge)) {
                ringSumEdgesSet.add(edge);
            }
        }

        List<String[]> ringSumEdges = ringSumEdgesSet.stream()
                .map(edge -> edge.split("-"))
                .collect(Collectors.toList());

        return new Graph(new ArrayList<>(unionNodes), ringSumEdges);
    }

    /**
     * Calcula el producto cartesiano de dos grafos.
     * Los nodos del resultado son pares ordenados de los nodos originales.
     * @param g1 Primer grafo
     * @param g2 Segundo grafo
     * @return Grafo resultado del producto cartesiano
     */
    private Graph calculateCartesianProduct(Graph g1, Graph g2) {
        List<String> resultNodes = new ArrayList<>();

        // Crear nodos del producto cartesiano
        for (String n1 : g1.getNodes()) {
            for (String n2 : g2.getNodes()) {
                resultNodes.add(n1 + n2);
            }
        }

        List<String[]> resultEdges = new ArrayList<>();

        // Aristas del tipo (u1, v2) - (u1, w2) donde v2-w2 es arista en g2
        for (String u1 : g1.getNodes()) {
            for (String[] edge2 : g2.getEdges()) {
                String v2 = edge2[0];
                String w2 = edge2[1];
                resultEdges.add(new String[]{u1 + v2, u1 + w2});
            }
        }

        // Aristas del tipo (u1, v2) - (w1, v2) donde u1-w1 es arista en g1
        for (String v2 : g2.getNodes()) {
            for (String[] edge1 : g1.getEdges()) {
                String u1 = edge1[0];
                String w1 = edge1[1];
                resultEdges.add(new String[]{u1 + v2, w1 + v2});
            }
        }

        // Eliminar aristas duplicadas
        Set<String> uniqueEdges = new HashSet<>();
        List<String[]> finalEdges = new ArrayList<>();

        for (String[] edge : resultEdges) {
            String[] sorted = {edge[0], edge[1]};
            Arrays.sort(sorted);
            String edgeKey = sorted[0] + "-" + sorted[1];

            if (!uniqueEdges.contains(edgeKey)) {
                uniqueEdges.add(edgeKey);
                finalEdges.add(edge);
            }
        }

        return new Graph(resultNodes, finalEdges);
    }

    /**
     * Calcula el producto tensorial de dos grafos.
     * Las aristas existen solo si hay aristas correspondientes en ambos grafos.
     * @param g1 Primer grafo
     * @param g2 Segundo grafo
     * @return Grafo resultado del producto tensorial
     */
    private Graph calculateTensorProduct(Graph g1, Graph g2) {
        List<String> resultNodes = new ArrayList<>();

        // Crear nodos del producto tensorial
        for (String n1 : g1.getNodes()) {
            for (String n2 : g2.getNodes()) {
                resultNodes.add(n1 + n2);
            }
        }

        List<String[]> resultEdges = new ArrayList<>();

        // Aristas del tipo (u1, v2) - (w1, x2) donde u1-w1 es arista en g1 y v2-x2 es arista en g2
        for (String[] edge1 : g1.getEdges()) {
            String u1 = edge1[0];
            String w1 = edge1[1];

            for (String[] edge2 : g2.getEdges()) {
                String v2 = edge2[0];
                String x2 = edge2[1];

                resultEdges.add(new String[]{u1 + v2, w1 + x2});
                resultEdges.add(new String[]{u1 + x2, w1 + v2});
            }
        }

        // Eliminar aristas duplicadas
        Set<String> uniqueEdges = new HashSet<>();
        List<String[]> finalEdges = new ArrayList<>();

        for (String[] edge : resultEdges) {
            String[] sorted = {edge[0], edge[1]};
            Arrays.sort(sorted);
            String edgeKey = sorted[0] + "-" + sorted[1];

            if (!uniqueEdges.contains(edgeKey)) {
                uniqueEdges.add(edgeKey);
                finalEdges.add(edge);
            }
        }

        return new Graph(resultNodes, finalEdges);
    }

    /**
     * Calcula la composición de dos grafos.
     * Combina las aristas del producto cartesiano con conexiones adicionales.
     * @param g1 Primer grafo
     * @param g2 Segundo grafo
     * @return Grafo resultado de la composición
     */
    private Graph calculateComposition(Graph g1, Graph g2) {
        // Empezar con el producto cartesiano
        Graph cartesian = calculateCartesianProduct(g1, g2);
        List<String[]> resultEdges = new ArrayList<>(cartesian.getEdges());

        // Agregar aristas adicionales de la composición
        for (String[] edge1 : g1.getEdges()) {
            String u1 = edge1[0];
            String w1 = edge1[1];

            for (String n2a : g2.getNodes()) {
                for (String n2b : g2.getNodes()) {
                    if (!n2a.equals(n2b)) {
                        resultEdges.add(new String[]{u1 + n2a, w1 + n2b});
                    }
                }
            }
        }

        // Eliminar aristas duplicadas
        Set<String> uniqueEdges = new HashSet<>();
        List<String[]> finalEdges = new ArrayList<>();

        for (String[] edge : resultEdges) {
            String[] sorted = {edge[0], edge[1]};
            Arrays.sort(sorted);
            String edgeKey = sorted[0] + "-" + sorted[1];

            if (!uniqueEdges.contains(edgeKey)) {
                uniqueEdges.add(edgeKey);
                finalEdges.add(edge);
            }
        }

        return new Graph(cartesian.getNodes(), finalEdges);
    }
}
