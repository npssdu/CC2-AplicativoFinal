package utils;

import java.util.*;

public class DataStructure {
    private Object[] data;
    private int size;
    private int keyLength;
    private boolean initialized;

    public DataStructure(int size, int keyLength) {
        this.size = size;
        this.keyLength = keyLength;
        this.data = new Object[size];
        this.initialized = false;
    }

    public void initialize() {
        Arrays.fill(data, null);
        this.initialized = true;
    }

    public int insert(Object key) throws Exception {
        if (!initialized) {
            throw new Exception("Estructura no inicializada");
        }

        if (isDuplicate(key)) {
            throw new Exception("Clave '" + key + "' ya existe");
        }

        int index = findEmptySlot();
        if (index == -1) {
            throw new Exception("Estructura llena");
        }

        data[index] = key;
        return index + 1; // Indexación desde 1
    }

    public int insertAt(Object key, int index) throws Exception {
        if (!initialized) {
            throw new Exception("Estructura no inicializada");
        }

        if (index < 0 || index >= size) {
            throw new Exception("Índice fuera de rango");
        }

        if (data[index] != null) {
            throw new Exception("Posición ocupada");
        }

        data[index] = key;
        return index + 1;
    }

    public boolean remove(Object key) {
        int index = search(key);
        if (index != -1) {
            data[index] = null;
            return true;
        }
        return false;
    }

    public int search(Object key) {
        for (int i = 0; i < data.length; i++) {
            if (Objects.equals(data[i], key)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isDuplicate(Object key) {
        return search(key) != -1;
    }

    public int findEmptySlot() {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void sort() {
        Object[] nonNullElements = Arrays.stream(data)
                .filter(Objects::nonNull)
                .toArray();

        Arrays.sort(nonNullElements, (a, b) -> {
            try {
                return Integer.compare(Integer.parseInt(a.toString()),
                        Integer.parseInt(b.toString()));
            } catch (NumberFormatException e) {
                return a.toString().compareTo(b.toString());
            }
        });

        Arrays.fill(data, null);
        System.arraycopy(nonNullElements, 0, data, 0, nonNullElements.length);
    }

    public boolean isSorted() {
        Object[] nonNullElements = Arrays.stream(data)
                .filter(Objects::nonNull)
                .toArray();

        for (int i = 1; i < nonNullElements.length; i++) {
            try {
                int prev = Integer.parseInt(nonNullElements[i-1].toString());
                int curr = Integer.parseInt(nonNullElements[i].toString());
                if (prev > curr) return false;
            } catch (NumberFormatException e) {
                String prev = nonNullElements[i-1].toString();
                String curr = nonNullElements[i].toString();
                if (prev.compareTo(curr) > 0) return false;
            }
        }
        return true;
    }

    public void reset() {
        Arrays.fill(data, null);
        initialized = false;
    }

    // Getters
    public Object[] getData() { return data.clone(); }
    public int getSize() { return size; }
    public int getKeyLength() { return keyLength; }
    public boolean isInitialized() { return initialized; }
    public Object getAt(int index) {
        return (index >= 0 && index < size) ? data[index] : null;
    }
}
