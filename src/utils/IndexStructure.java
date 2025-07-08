package utils;

import java.util.*;

public class IndexStructure {
    private List<IndexEntry> entries;
    private String indexType;
    private int maxEntriesPerLevel;
    private List<List<IndexEntry>> levels;
    private boolean isMultilevel;

    public static class IndexEntry {
        public Object key;
        public Object value;
        public int blockPointer;
        public List<Integer> recordPointers;
        public int level;

        public IndexEntry(Object key, Object value, int blockPointer) {
            this.key = key;
            this.value = value;
            this.blockPointer = blockPointer;
            this.recordPointers = new ArrayList<>();
            this.level = 0;
        }

        public IndexEntry(Object key, int blockPointer) {
            this(key, null, blockPointer);
        }

        @Override
        public String toString() {
            if (recordPointers.isEmpty()) {
                return String.format("%s -> B%d", key, blockPointer);
            } else {
                return String.format("%s -> %s", key, recordPointers.toString());
            }
        }
    }

    public static class SearchResult {
        public final boolean found;
        public final IndexEntry entry;
        public final int level;
        public final int position;
        public final List<SearchStep> steps;

        public SearchResult(boolean found, IndexEntry entry, int level, int position, List<SearchStep> steps) {
            this.found = found;
            this.entry = entry;
            this.level = level;
            this.position = position;
            this.steps = steps;
        }
    }

    public static class SearchStep {
        public final int level;
        public final int position;
        public final Object key;
        public final String action;

        public SearchStep(int level, int position, Object key, String action) {
            this.level = level;
            this.position = position;
            this.key = key;
            this.action = action;
        }
    }

    public IndexStructure(String indexType, int maxEntriesPerLevel) {
        this.indexType = indexType;
        this.maxEntriesPerLevel = maxEntriesPerLevel;
        this.entries = new ArrayList<>();
        this.levels = new ArrayList<>();
        this.isMultilevel = indexType.contains("MULTINIVEL");

        if (isMultilevel) {
            levels.add(new ArrayList<>()); // Nivel 0 (hoja)
        }
    }

    public void insert(Object key, Object value, int blockPointer) throws Exception {
        IndexEntry newEntry = new IndexEntry(key, value, blockPointer);

        if (indexType.contains("SECUNDARIO")) {
            insertSecondaryIndex(newEntry);
        } else {
            insertPrimaryIndex(newEntry);
        }

        if (isMultilevel) {
            checkMultilevelReorganization();
        }
    }

    private void insertPrimaryIndex(IndexEntry entry) throws Exception {
        // Verificar duplicados en índice primario
        for (IndexEntry existing : entries) {
            if (existing.key.equals(entry.key)) {
                throw new Exception("Clave primaria '" + entry.key + "' ya existe");
            }
        }

        entries.add(entry);
        Collections.sort(entries, (a, b) -> compareKeys(a.key, b.key));

        if (isMultilevel && !levels.isEmpty()) {
            levels.get(0).clear();
            levels.get(0).addAll(entries);
        }
    }

    private void insertSecondaryIndex(IndexEntry entry) {
        // En índice secundario, pueden existir claves duplicadas
        IndexEntry existing = findExistingSecondaryKey(entry.key);

        if (existing != null) {
            // Agregar puntero a registro existente
            existing.recordPointers.add(entry.blockPointer);
        } else {
            // Nueva clave secundaria
            entry.recordPointers.add(entry.blockPointer);
            entries.add(entry);
            Collections.sort(entries, (a, b) -> compareKeys(a.key, b.key));
        }

        if (isMultilevel && !levels.isEmpty()) {
            levels.get(0).clear();
            levels.get(0).addAll(entries);
        }
    }

    private IndexEntry findExistingSecondaryKey(Object key) {
        for (IndexEntry entry : entries) {
            if (entry.key.equals(key)) {
                return entry;
            }
        }
        return null;
    }

    private void checkMultilevelReorganization() {
        if (levels.isEmpty()) return;

        // Verificar si el nivel 0 excede el máximo
        if (levels.get(0).size() > maxEntriesPerLevel) {
            reorganizeMultilevel();
        }
    }

    private void reorganizeMultilevel() {
        int currentLevel = 0;

        while (currentLevel < levels.size() && levels.get(currentLevel).size() > maxEntriesPerLevel) {
            List<IndexEntry> currentLevelEntries = levels.get(currentLevel);

            // Crear siguiente nivel si no existe
            if (currentLevel + 1 >= levels.size()) {
                levels.add(new ArrayList<>());
            }

            List<IndexEntry> nextLevelEntries = levels.get(currentLevel + 1);
            nextLevelEntries.clear();

            // Crear entradas del siguiente nivel
            for (int i = 0; i < currentLevelEntries.size(); i += maxEntriesPerLevel) {
                int endIndex = Math.min(i + maxEntriesPerLevel, currentLevelEntries.size());
                IndexEntry representativeEntry = currentLevelEntries.get(endIndex - 1);

                IndexEntry nextLevelEntry = new IndexEntry(
                        representativeEntry.key,
                        null,
                        currentLevel * 1000 + i / maxEntriesPerLevel
                );
                nextLevelEntry.level = currentLevel + 1;
                nextLevelEntries.add(nextLevelEntry);
            }

            currentLevel++;
        }
    }

    public SearchResult search(Object key) {
        List<SearchStep> steps = new ArrayList<>();

        if (isMultilevel) {
            return searchMultilevel(key, steps);
        } else {
            return searchSingleLevel(key, steps);
        }
    }

    private SearchResult searchSingleLevel(Object key, List<SearchStep> steps) {
        for (int i = 0; i < entries.size(); i++) {
            IndexEntry entry = entries.get(i);
            steps.add(new SearchStep(0, i, entry.key, "Comparando con " + entry.key));

            if (entry.key.equals(key)) {
                steps.add(new SearchStep(0, i, entry.key, "¡Encontrado!"));
                return new SearchResult(true, entry, 0, i, steps);
            }
        }

        return new SearchResult(false, null, 0, -1, steps);
    }

    private SearchResult searchMultilevel(Object key, List<SearchStep> steps) {
        // Comenzar desde el nivel más alto
        int currentLevel = levels.size() - 1;

        while (currentLevel >= 0) {
            List<IndexEntry> levelEntries = levels.get(currentLevel);
            steps.add(new SearchStep(currentLevel, -1, key, "Buscando en nivel " + currentLevel));

            // Buscar en el nivel actual
            IndexEntry foundEntry = null;
            int position = -1;

            for (int i = 0; i < levelEntries.size(); i++) {
                IndexEntry entry = levelEntries.get(i);
                steps.add(new SearchStep(currentLevel, i, entry.key, "Comparando con " + entry.key));

                if (compareKeys(key, entry.key) <= 0) {
                    foundEntry = entry;
                    position = i;
                    break;
                }
            }

            if (currentLevel == 0) {
                // Nivel hoja
                if (foundEntry != null && foundEntry.key.equals(key)) {
                    steps.add(new SearchStep(currentLevel, position, foundEntry.key, "¡Encontrado en nivel hoja!"));
                    return new SearchResult(true, foundEntry, currentLevel, position, steps);
                } else {
                    return new SearchResult(false, null, currentLevel, -1, steps);
                }
            } else {
                // Nivel interno - continuar búsqueda
                currentLevel--;
            }
        }

        return new SearchResult(false, null, 0, -1, steps);
    }

    public boolean remove(Object key) {
        IndexEntry toRemove = null;

        for (IndexEntry entry : entries) {
            if (entry.key.equals(key)) {
                toRemove = entry;
                break;
            }
        }

        if (toRemove != null) {
            entries.remove(toRemove);

            if (isMultilevel) {
                rebuildMultilevelStructure();
            }

            return true;
        }

        return false;
    }

    private void rebuildMultilevelStructure() {
        levels.clear();
        levels.add(new ArrayList<>(entries));
        checkMultilevelReorganization();
    }

    private int compareKeys(Object a, Object b) {
        try {
            return Integer.compare(Integer.parseInt(a.toString()), Integer.parseInt(b.toString()));
        } catch (NumberFormatException e) {
            return a.toString().compareTo(b.toString());
        }
    }

    public void reset() {
        entries.clear();
        levels.clear();
        if (isMultilevel) {
            levels.add(new ArrayList<>());
        }
    }

    // Getters
    public List<IndexEntry> getEntries() { return new ArrayList<>(entries); }
    public String getIndexType() { return indexType; }
    public int getMaxEntriesPerLevel() { return maxEntriesPerLevel; }
    public List<List<IndexEntry>> getLevels() { return levels; }
    public boolean isMultilevel() { return isMultilevel; }
    public int getEntryCount() { return entries.size(); }
    public int getLevelCount() { return isMultilevel ? levels.size() : 1; }
}
