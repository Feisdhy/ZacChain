package blockchain.memory;

import java.math.BigInteger;

public class MemoryTrie {
    public Node root;

    private static final BigInteger one = new BigInteger("1");
    public BigInteger branchCount, extensionCount, leafCount;
    public int[] extensionMap;

    public MemoryTrie() {
        root = null;
        branchCount = new BigInteger("0");
        extensionCount = new BigInteger("0");
        leafCount = new BigInteger("0");
        extensionMap = new int[41];
    }

    public static String findCommonPrefix(String str1, String str2) {
        int minLength = Math.min(str1.length(), str2.length());
        StringBuilder commonPrefix = new StringBuilder();
        for (int i = 0; i < minLength; i++) {
            if (str1.charAt(i) != str2.charAt(i)) break;
            commonPrefix.append(str1.charAt(i));
        }
        return commonPrefix.toString();
    }

    public static int getIndex(char c) {
        if (Character.isDigit(c)) return c - '0';
        else if (Character.isLowerCase(c)) return c - 'a' + 10;
        else return c - 'A' + 10;
    }

    /**
     * if (node == null) return new LeafNode(restKey, value);
     *         else {
     *             if (node instanceof BranchNode branchNode) {
     *                 // 如果遍历到扩展节点，首先检查restKey对应的children是否为空
     *                 // 如果为空则为其分配一个LeafNode
     *                 // 如果不为空则继续遍历
     *                 // 但是无论是不是空都需要保证branchNode的children引用正确
     *                 int idx = getIndex(restKey.charAt(0));
     *                 branchNode.children[idx] = insert(branchNode.children[idx], restKey.substring(1), value);
     *                 return branchNode;
     *             }
     *             else if (node instanceof ExtensionNode extensionNode) {
     *                 // 如果遍历到扩展结点，首先检查restKey是否完全覆盖key
     *                 // 如果全部覆盖则继续遍历
     *                 // 如果完全没有覆盖则一定会生成一个新的branchNode和一个extensionNode，需要插入的键值对会沿着branchNode继续插入
     *                 // 如果覆盖了一部分则一定会生成一个新的extensionNode和一个branchNode，需要插入的键值对会沿着branchNode继续插入
     *                 String prefix = findCommonPrefix(restKey, extensionNode.key);
     *                 if (prefix.isEmpty()) {
     *                     BranchNode branchNode = new BranchNode();
     *                     int idx1 = getIndex(restKey.charAt(0)), idx2 = getIndex(extensionNode.key.charAt(0));
     *                     branchNode.children[idx1] = insert(branchNode.children[idx1], restKey.substring(1), value);
     *                     // 这里需要进行特殊判断当前extension是否只有一个字符
     *                     // 如果只有一个字符则将branchNode指向extensionNode的孩子节点
     *                     // 如果有多个字符则新生成一个extensionNode指向原本extensionNode的孩子节点
     *                     if (extensionNode.key.substring(1).isEmpty()) branchNode.children[idx2] = extensionNode.child;
     *                     else {
     *                         ExtensionNode extensionNode1 = new ExtensionNode(extensionNode.key.substring(1));
     *                         extensionNode1.child = extensionNode.child;
     *                         branchNode.children[idx2] = extensionNode1;
     *                     }
     *                     return branchNode;
     *                 }
     *                 else {
     *                     if (restKey.startsWith(extensionNode.key)) {
     *                         extensionNode.child = insert(extensionNode.child, restKey.substring(extensionNode.key.length()), value);
     *                         return extensionNode;
     *                     }
     *                     else {
     *                         ExtensionNode extensionNode1 = new ExtensionNode(prefix);
     *                         BranchNode branchNode = new BranchNode();
     *                         int idx = prefix.length(), idx1 = getIndex(restKey.charAt(idx)), idx2 = getIndex(extensionNode.key.charAt(idx));
     *                         branchNode.children[idx1] = insert(branchNode.children[idx1], restKey.substring(idx + 1), value);
     *                         // 这里需要进行特殊判断即prefix + branchNode的一个字符是否等于原始的extensionNode
     *                         // 如果是的话那就不需要额外生成一个extensionNode
     *                         // 如果不是那么就需要额外生成一个extensionNode并将其指向原始extensionNode的孩子节点
     *                         if (extensionNode.key.substring(idx + 1).isEmpty()) branchNode.children[idx2] = extensionNode.child;
     *                         else {
     *                             ExtensionNode extensionNode2 = new ExtensionNode(extensionNode.key.substring(idx + 1));
     *                             extensionNode2.child = extensionNode.child;
     *                             branchNode.children[idx2] = extensionNode2;
     *                         }
     *                         extensionNode1.child = branchNode;
     *                         return extensionNode1;
     *                     }
     *                 }
     *             }
     *             else if (node instanceof LeafNode leafNode) {
     *                 // 如果遍历到叶子节点，首先检查restKey是否和node的key相同
     *                 // 如果相同，则更新node的值
     *                 // 如果不同，则需要计算公共前缀
     *                 if (restKey.equals(leafNode.key)) {
     *                     leafNode.value = value;
     *                     return leafNode;
     *                 }
     *                 else {
     *                     // 比较公共前缀
     *                     // 如果存在公共前缀，则分配一个ExtensionNode,一个BranchNode，两个LeafNode
     *                     // 如果不存在公共前缀,一个BranchNode，两个LeafNode
     *                     String prefix = findCommonPrefix(restKey, leafNode.key);
     *                     if (prefix.isEmpty()) {
     *                         BranchNode branchNode = new BranchNode();
     *                         int idx1 = getIndex(restKey.charAt(0)), idx2 = getIndex(leafNode.key.charAt(0));
     *                         branchNode.children[idx1] = new LeafNode(restKey.substring(1), value);
     *                         branchNode.children[idx2] = new LeafNode(leafNode.key.substring(1), leafNode.value);
     *                         return branchNode;
     *                     }
     *                     else {
     *                         ExtensionNode extensionNode = new ExtensionNode(prefix);
     *                         BranchNode branchNode = new BranchNode();
     *                         int idx = prefix.length(), idx1 = getIndex(restKey.charAt(idx)), idx2 = getIndex(leafNode.key.charAt(idx));
     *                         branchNode.children[idx1] = new LeafNode(restKey.substring(idx + 1), value);
     *                         branchNode.children[idx2] = new LeafNode(leafNode.key.substring(idx + 1), leafNode.value);
     *                         extensionNode.child = branchNode;
     *                         return extensionNode;
     *                     }
     *                 }
     *             }
     *             else {
     *                 System.err.println("遍历过程中出现节点种类错误！");
     *                 throw new RuntimeException("节点种类异常");
     *             }
     *         }
     * */
    private Node insert(Node node, String restKey, String value) {
        if (node == null) {
            leafCount = leafCount.add(one);
            return new LeafNode(restKey, value);
        }
        else {
            if (node instanceof BranchNode branchNode) {
                // 如果遍历到扩展节点，首先检查restKey对应的children是否为空
                // 如果为空则为其分配一个LeafNode
                // 如果不为空则继续遍历
                // 但是无论是不是空都需要保证branchNode的children引用正确
                int idx = getIndex(restKey.charAt(0));
                branchNode.children[idx] = insert(branchNode.children[idx], restKey.substring(1), value);
                return branchNode;
            }
            else if (node instanceof ExtensionNode extensionNode) {
                // 如果遍历到扩展结点，首先检查restKey是否完全覆盖key
                // 如果全部覆盖则继续遍历
                // 如果完全没有覆盖则一定会生成一个新的branchNode和一个extensionNode，需要插入的键值对会沿着branchNode继续插入
                // 如果覆盖了一部分则一定会生成一个新的extensionNode和一个branchNode，需要插入的键值对会沿着branchNode继续插入
                String prefix = findCommonPrefix(restKey, extensionNode.key);
                if (prefix.isEmpty()) {
                    BranchNode branchNode = new BranchNode();
                    int idx1 = getIndex(restKey.charAt(0)), idx2 = getIndex(extensionNode.key.charAt(0));
                    branchNode.children[idx1] = insert(branchNode.children[idx1], restKey.substring(1), value);
                    // 这里需要进行特殊判断当前extension是否只有一个字符
                    // 如果只有一个字符则将branchNode指向extensionNode的孩子节点
                    // 如果有多个字符则新生成一个extensionNode指向原本extensionNode的孩子节点
                    if (extensionNode.key.substring(1).isEmpty()) {
                        branchNode.children[idx2] = extensionNode.child;
                        extensionCount = extensionCount.subtract(one);
                    }
                    else {
                        ExtensionNode extensionNode1 = new ExtensionNode(extensionNode.key.substring(1));
                        extensionNode1.child = extensionNode.child;
                        branchNode.children[idx2] = extensionNode1;
                        extensionMap[extensionNode1.key.length()] ++ ;
                    }
                    branchCount = branchCount.add(one);
                    extensionMap[extensionNode.key.length()] -- ;
                    return branchNode;
                }
                else {
                    if (restKey.startsWith(extensionNode.key)) {
                        extensionNode.child = insert(extensionNode.child, restKey.substring(extensionNode.key.length()), value);
                        return extensionNode;
                    }
                    else {
                        ExtensionNode extensionNode1 = new ExtensionNode(prefix);
                        BranchNode branchNode = new BranchNode();
                        int idx = prefix.length(), idx1 = getIndex(restKey.charAt(idx)), idx2 = getIndex(extensionNode.key.charAt(idx));
                        branchNode.children[idx1] = insert(branchNode.children[idx1], restKey.substring(idx + 1), value);
                        // 这里需要进行特殊判断即prefix + branchNode的一个字符是否等于原始的extensionNode
                        // 如果是的话那就不需要额外生成一个extensionNode
                        // 如果不是那么就需要额外生成一个extensionNode并将其指向原始extensionNode的孩子节点
                        if (extensionNode.key.substring(idx + 1).isEmpty()) branchNode.children[idx2] = extensionNode.child;
                        else {
                            ExtensionNode extensionNode2 = new ExtensionNode(extensionNode.key.substring(idx + 1));
                            extensionNode2.child = extensionNode.child;
                            branchNode.children[idx2] = extensionNode2;
                            extensionCount = extensionCount.add(one);
                            extensionMap[extensionNode2.key.length()] ++ ;
                        }
                        extensionNode1.child = branchNode;
                        branchCount = branchCount.add(one);
                        extensionMap[extensionNode.key.length()] -- ;
                        extensionMap[extensionNode1.key.length()] ++ ;
                        return extensionNode1;
                    }
                }
            }
            else if (node instanceof LeafNode leafNode) {
                // 如果遍历到叶子节点，首先检查restKey是否和node的key相同
                // 如果相同，则更新node的值
                // 如果不同，则需要计算公共前缀
                if (restKey.equals(leafNode.key)) {
                    leafNode.value = value;
                    return leafNode;
                }
                else {
                    // 比较公共前缀
                    // 如果存在公共前缀，则分配一个ExtensionNode,一个BranchNode，两个LeafNode
                    // 如果不存在公共前缀,一个BranchNode，两个LeafNode
                    String prefix = findCommonPrefix(restKey, leafNode.key);
                    if (prefix.isEmpty()) {
                        BranchNode branchNode = new BranchNode();
                        int idx1 = getIndex(restKey.charAt(0)), idx2 = getIndex(leafNode.key.charAt(0));
                        branchNode.children[idx1] = new LeafNode(restKey.substring(1), value);
                        branchNode.children[idx2] = new LeafNode(leafNode.key.substring(1), leafNode.value);
                        branchCount = branchCount.add(one);
                        leafCount = leafCount.add(one);
                        return branchNode;
                    }
                    else {
                        ExtensionNode extensionNode = new ExtensionNode(prefix);
                        BranchNode branchNode = new BranchNode();
                        int idx = prefix.length(), idx1 = getIndex(restKey.charAt(idx)), idx2 = getIndex(leafNode.key.charAt(idx));
                        branchNode.children[idx1] = new LeafNode(restKey.substring(idx + 1), value);
                        branchNode.children[idx2] = new LeafNode(leafNode.key.substring(idx + 1), leafNode.value);
                        extensionNode.child = branchNode;
                        branchCount = branchCount.add(one);
                        extensionCount = extensionCount.add(one);
                        leafCount = leafCount.add(one);
                        extensionMap[extensionNode.key.length()] ++ ;
                        return extensionNode;
                    }
                }
            }
            else {
                System.err.println("遍历过程中出现节点种类错误！");
                throw new RuntimeException("节点种类异常");
            }
        }
    }

    public void insert(String key, String value) {
        root = insert(root, key, value);
    }

    private String get(Node node, String restKey) {
        if (node == null) return "";
        else {
            if (node instanceof BranchNode branchNode) {
                int idx = getIndex(restKey.charAt(0));
                return get(branchNode.children[idx], restKey.substring(1));
            }
            else if (node instanceof ExtensionNode extensionNode) {
                if (restKey.startsWith(extensionNode.key)) return get(extensionNode.child, restKey.substring(extensionNode.key.length()));
                else return null;
            }
            else if (node instanceof LeafNode leafNode) {
                if (restKey.equals(leafNode.key)) return leafNode.value;
                else return null;
            }
            else {
                System.err.println("遍历过程中出现节点种类错误！");
                throw new RuntimeException("节点种类异常");
            }
        }
    }

    public String get(String key) {
        return get(root, key);
    }

    private int getPathLength(Node node, String restKey) {
        if (node == null) return 0;
        else {
            if (node instanceof BranchNode branchNode) {
                int idx = getIndex(restKey.charAt(0));
                return getPathLength(branchNode.children[idx], restKey.substring(1)) + 1;
            }
            else if (node instanceof ExtensionNode extensionNode) {
                if (restKey.startsWith(extensionNode.key)) return getPathLength(extensionNode.child, restKey.substring(extensionNode.key.length())) + 1;
                else return 0;
            }
            else if (node instanceof LeafNode leafNode) {
                if (restKey.equals(leafNode.key)) return 1;
                else return 0;
            }
            else {
                System.err.println("遍历过程中出现节点种类错误！");
                throw new RuntimeException("节点种类异常");
            }
        }
    }

    public int getPathLength(String key) {
        return getPathLength(root, key);
    }

    public void showExtensionNodes() {
        for (int i = 1; i <= 40; i ++ ) System.out.print(extensionMap[i] + " ");
        System.out.println();
    }

    @Override
    public String toString() {
        return "MemoryTrie{" +
                "branchCount=" + branchCount +
                ", extensionCount=" + extensionCount +
                ", leafCount=" + leafCount +
                '}';
    }
}
