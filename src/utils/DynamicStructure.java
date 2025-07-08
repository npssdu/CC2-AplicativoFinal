package utils;

import java.util.*;

public class DynamicStructure {
    private List<List<Object>> buckets;
    private int initialSize;
    private int keyLength;
    private int currentLevel;
    private int totalElements;
    private String expansionType;
    private List<ExpansionEvent> expansionHistory;

    public static class ExpansionEvent {
        public final int level;
        public final int oldSize;
        public final int newSize;
        public final String trigger;
        public final long timestamp;

        public ExpansionEvent(int level, int oldSize, int newSize, String trigger) {
            this.level = level;
            this.oldSize = oldSize;
            this.newSize = newSize;
            this.trigger = trigger;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public DynamicStructure(int initialSize, int keyLength, String expansionType) {
        this.initialSize = initialSize;
        this.keyLength = keyLength;
        this.expansionType = expansionType;
        this.currentLevel = 0;
        this.totalElements = 0;
        this.expansionHistory = new ArrayList<>();
        initializeBuckets();
    }

    private void initializeBuckets() {
        buckets = new ArrayList<>();
        int currentSize = getCurrentTableSize();

        for (int i = 0; i < currentSize; i++) {
            buckets.add(new ArrayList<>());
        }
    }

    public int getCurrentTableSize() {
        if (expansionType.equals("TOTAL")) {
            return (int) (initialSize * Math.pow(2, currentLevel));
        } else { // PARCIAL
            return (int) (initialSize * Math.pow(1.5, currentLevel));
        }
    }

    public void insert(Object key) throws Exception {
        if (key.toString().length() != keyLength) {
            throw new Exception(String.format("La clave debe tener %d dígitos", keyLength));
        }

        // Verificar si ya existe
        if (search(key) != -1) {
            throw new Exception("Clave '" + key + "' ya existe");
        }

        // Calcular bucket
        int bucketIndex = calculateBucket(key);

        // Insertar en bucket
        buckets.get(bucketIndex).add(key);
        totalElements++;

        // Verificar necesidad de expansión
        checkExpansion();
    }

    private int calculateBucket(Object key) {
        int numericKey = Integer.parseInt(key.toString());
        return numericKey % buckets.size();
    }

    private void checkExpansion() {
        double loadFactor = (double) totalElements / buckets.size();

        // Expandir si el factor de carga supera 0.75
        if (loadFactor > 0.75) {
            performExpansion("Factor de carga > 0.75");
        }
    }

    private void performExpansion(String trigger) {
        int oldSize = buckets.size();
        currentLevel++;
        int newSize = getCurrentTableSize();

        // Registrar evento de expansión
        expansionHistory.add(new ExpansionEvent(currentLevel, oldSize, newSize, trigger));

        // Guardar elementos existentes
        List<Object> allElements = new ArrayList<>();
        for (List<Object> bucket : buckets) {
            allElements.addAll(bucket);
        }

        // Reinicializar con nuevo tamaño
        buckets.clear();
        for (int i = 0; i < newSize; i++) {
            buckets.add(new ArrayList<>());
        }

        // Redistribuir elementos
        for (Object element : allElements) {
            int newBucket = calculateBucket(element);
            buckets.get(newBucket).add(element);
        }
    }

    public int search(Object key) {
        int bucketIndex = calculateBucket(key);
        List<Object> bucket = buckets.get(bucketIndex);

        for (int i = 0; i < bucket.size(); i++) {
            if (bucket.get(i).equals(key)) {
                return bucketIndex;
            }
        }
        return -1;
    }

    public boolean remove(Object key) {
        int bucketIndex = calculateBucket(key);
        List<Object> bucket = buckets.get(bucketIndex);

        if (bucket.remove(key)) {
            totalElements--;
            return true;
        }
        return false;
    }

    public void reset() {
        currentLevel = 0;
        totalElements = 0;
        expansionHistory.clear();
        initializeBuckets();
    }

    public double getLoadFactor() {
        return buckets.isEmpty() ? 0 : (double) totalElements / buckets.size();
    }

    public int getBucketCount(int bucketIndex) {
        if (bucketIndex >= 0 && bucketIndex < buckets.size()) {
            return buckets.get(bucketIndex).size();
        }
        return 0;
    }

    public List<Object> getBucketElements(int bucketIndex) {
        if (bucketIndex >= 0 && bucketIndex < buckets.size()) {
            return new ArrayList<>(buckets.get(bucketIndex));
        }
        return new ArrayList<>();
    }

    // Getters
    public List<List<Object>> getBuckets() { return buckets; }
    public int getInitialSize() { return initialSize; }
    public int getKeyLength() { return keyLength; }
    public int getCurrentLevel() { return currentLevel; }
    public int getTotalElements() { return totalElements; }
    public String getExpansionType() { return expansionType; }
    public List<ExpansionEvent> getExpansionHistory() { return expansionHistory; }
}
