package utils;

import java.util.*;

public class BlockStructure {
    private Object[][] blocks;
    private int totalElements;
    private int numBlocks;
    private int elementsPerBlock;
    private int keyLength;
    private boolean initialized;

    public BlockStructure(int totalElements, int keyLength) {
        this.totalElements = totalElements;
        this.keyLength = keyLength;
        this.numBlocks = (int) Math.floor(Math.sqrt(totalElements));
        this.elementsPerBlock = totalElements / numBlocks;
        this.blocks = new Object[numBlocks][elementsPerBlock];
        this.initialized = false;
    }

    public void initialize() {
        for (int i = 0; i < numBlocks; i++) {
            Arrays.fill(blocks[i], null);
        }
        this.initialized = true;
    }

    public void insertAt(Object key, int blockIndex, int position) throws Exception {
        if (!initialized) {
            throw new Exception("Estructura de bloques no inicializada");
        }

        if (blockIndex < 0 || blockIndex >= numBlocks) {
            throw new Exception("Índice de bloque fuera de rango");
        }

        if (position < 0 || position >= elementsPerBlock) {
            throw new Exception("Posición en bloque fuera de rango");
        }

        if (blocks[blockIndex][position] != null) {
            throw new Exception("Posición ocupada");
        }

        blocks[blockIndex][position] = key;
    }

    public Object getAt(int blockIndex, int position) {
        if (blockIndex < 0 || blockIndex >= numBlocks ||
                position < 0 || position >= elementsPerBlock) {
            return null;
        }
        return blocks[blockIndex][position];
    }

    public boolean remove(Object key) {
        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < elementsPerBlock; j++) {
                if (Objects.equals(blocks[i][j], key)) {
                    blocks[i][j] = null;
                    return true;
                }
            }
        }
        return false;
    }

    public SearchResult linearSearch(Object key) {
        List<SearchStep> steps = new ArrayList<>();

        for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
            steps.add(new SearchStep(SearchStep.Type.BLOCK_ACCESS, blockIndex, -1,
                    "Accediendo bloque " + (blockIndex + 1)));

            for (int position = 0; position < elementsPerBlock; position++) {
                steps.add(new SearchStep(SearchStep.Type.ELEMENT_ACCESS, blockIndex, position,
                        "Comparando con " + blocks[blockIndex][position]));

                if (Objects.equals(blocks[blockIndex][position], key)) {
                    return new SearchResult(true, blockIndex, position, steps);
                }
            }
        }

        return new SearchResult(false, -1, -1, steps);
    }

    public SearchResult binarySearch(Object key) throws Exception {
        // Verificar que esté ordenado
        if (!isSorted()) {
            throw new Exception("La estructura debe estar ordenada para búsqueda binaria");
        }

        List<SearchStep> steps = new ArrayList<>();

        // Búsqueda binaria entre bloques
        int leftBlock = 0;
        int rightBlock = numBlocks - 1;
        int targetBlock = -1;

        while (leftBlock <= rightBlock) {
            int midBlock = (leftBlock + rightBlock) / 2;
            steps.add(new SearchStep(SearchStep.Type.BLOCK_ACCESS, midBlock, -1,
                    String.format("Búsqueda binaria: bloque %d (entre %d y %d)",
                            midBlock + 1, leftBlock + 1, rightBlock + 1)));

            Object firstElement = getFirstNonNull(midBlock);
            Object lastElement = getLastNonNull(midBlock);

            if (firstElement == null) {
                rightBlock = midBlock - 1;
                continue;
            }

            int cmpFirst = compareKeys(key, firstElement);
            int cmpLast = lastElement != null ? compareKeys(key, lastElement) : cmpFirst;

            if (cmpFirst >= 0 && cmpLast <= 0) {
                targetBlock = midBlock;
                break;
            } else if (cmpFirst < 0) {
                rightBlock = midBlock - 1;
            } else {
                leftBlock = midBlock + 1;
            }
        }

        if (targetBlock == -1) {
            return new SearchResult(false, -1, -1, steps);
        }

        // Búsqueda binaria dentro del bloque
        return binarySearchInBlock(targetBlock, key, steps);
    }

    private SearchResult binarySearchInBlock(int blockIndex, Object key, List<SearchStep> steps) {
        List<Integer> validPositions = new ArrayList<>();
        for (int i = 0; i < elementsPerBlock; i++) {
            if (blocks[blockIndex][i] != null) {
                validPositions.add(i);
            }
        }

        if (validPositions.isEmpty()) {
            return new SearchResult(false, -1, -1, steps);
        }

        int left = 0;
        int right = validPositions.size() - 1;

        while (left <= right) {
            int mid = (left + right) / 2;
            int actualPos = validPositions.get(mid);
            Object midValue = blocks[blockIndex][actualPos];

            steps.add(new SearchStep(SearchStep.Type.ELEMENT_ACCESS, blockIndex, actualPos,
                    String.format("Búsqueda binaria en bloque: posición %d", actualPos + 1)));

            int cmp = compareKeys(key, midValue);

            if (cmp == 0) {
                return new SearchResult(true, blockIndex, actualPos, steps);
            } else if (cmp < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return new SearchResult(false, -1, -1, steps);
    }

    private Object getFirstNonNull(int blockIndex) {
        for (int i = 0; i < elementsPerBlock; i++) {
            if (blocks[blockIndex][i] != null) {
                return blocks[blockIndex][i];
            }
        }
        return null;
    }

    private Object getLastNonNull(int blockIndex) {
        for (int i = elementsPerBlock - 1; i >= 0; i--) {
            if (blocks[blockIndex][i] != null) {
                return blocks[blockIndex][i];
            }
        }
        return null;
    }

    public boolean isSorted() {
        Object lastValue = null;

        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < elementsPerBlock; j++) {
                Object current = blocks[i][j];
                if (current != null) {
                    if (lastValue != null && compareKeys(current, lastValue) < 0) {
                        return false;
                    }
                    lastValue = current;
                }
            }
        }
        return true;
    }

    public void sort() {
        // Recopilar todos los elementos no nulos
        List<Object> allElements = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < elementsPerBlock; j++) {
                if (blocks[i][j] != null) {
                    allElements.add(blocks[i][j]);
                }
            }
        }

        // Ordenar
        allElements.sort((a, b) -> compareKeys(a, b));

        // Limpiar estructura
        initialize();

        // Redistribuir elementos ordenados
        int elementIndex = 0;
        for (int i = 0; i < numBlocks && elementIndex < allElements.size(); i++) {
            for (int j = 0; j < elementsPerBlock && elementIndex < allElements.size(); j++) {
                blocks[i][j] = allElements.get(elementIndex++);
            }
        }
    }

    private int compareKeys(Object a, Object b) {
        try {
            return Integer.compare(Integer.parseInt(a.toString()), Integer.parseInt(b.toString()));
        } catch (NumberFormatException e) {
            return a.toString().compareTo(b.toString());
        }
    }

    public BlockPosition getHashPosition(int hashValue) {
        int absolutePosition = hashValue - 1; // Convertir a base 0
        int blockIndex = absolutePosition / elementsPerBlock;
        int position = absolutePosition % elementsPerBlock;

        // Asegurar que esté dentro de los límites
        blockIndex = Math.max(0, Math.min(blockIndex, numBlocks - 1));
        position = Math.max(0, Math.min(position, elementsPerBlock - 1));

        return new BlockPosition(blockIndex, position);
    }

    public void reset() {
        initialize();
        initialized = false;
    }

    // Getters
    public Object[][] getBlocks() { return blocks; }
    public int getTotalElements() { return totalElements; }
    public int getNumBlocks() { return numBlocks; }
    public int getElementsPerBlock() { return elementsPerBlock; }
    public int getKeyLength() { return keyLength; }
    public boolean isInitialized() { return initialized; }

    // Clases auxiliares
    public static class SearchResult {
        public final boolean found;
        public final int blockIndex;
        public final int position;
        public final List<SearchStep> steps;

        public SearchResult(boolean found, int blockIndex, int position, List<SearchStep> steps) {
            this.found = found;
            this.blockIndex = blockIndex;
            this.position = position;
            this.steps = steps;
        }
    }

    public static class SearchStep {
        public enum Type { BLOCK_ACCESS, ELEMENT_ACCESS }

        public final Type type;
        public final int blockIndex;
        public final int position;
        public final String description;

        public SearchStep(Type type, int blockIndex, int position, String description) {
            this.type = type;
            this.blockIndex = blockIndex;
            this.position = position;
            this.description = description;
        }
    }

    public static class BlockPosition {
        public final int blockIndex;
        public final int position;

        public BlockPosition(int blockIndex, int position) {
            this.blockIndex = blockIndex;
            this.position = position;
        }
    }
}
