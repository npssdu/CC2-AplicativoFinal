package utils;

public class TreeNode {
    public Object data;
    public TreeNode left;
    public TreeNode right;
    public TreeNode parent;
    public int level;
    public String binaryPath;
    public int ascii;
    public int frequency;
    public boolean isLeaf;

    public TreeNode(Object data) {
        this.data = data;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.level = 0;
        this.binaryPath = "";
        this.frequency = 0;
        this.isLeaf = true;

        if (data != null && data instanceof Character) {
            this.ascii = ((Character) data).charValue();
        }
    }

    public TreeNode() {
        this(null);
    }

    public boolean hasChildren() {
        return left != null || right != null;
    }

    public boolean isComplete() {
        return left != null && right != null;
    }

    public String getBinaryRepresentation() {
        if (data instanceof Character) {
            return Integer.toBinaryString(ascii).substring(Math.max(0,
                    Integer.toBinaryString(ascii).length() - 5));
        }
        return "";
    }

    public String getHuffmanCode() {
        return binaryPath;
    }

    @Override
    public String toString() {
        if (data == null) return "null";
        if (frequency > 0) {
            return data + "(" + frequency + ")";
        }
        return data.toString();
    }
}
