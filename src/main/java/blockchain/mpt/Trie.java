package blockchain.mpt;

import blockchain.global.Node;
import blockchain.util.Common;
import blockchain.util.HashUtils;
import blockchain.util.RocksDBUtils;
import blockchain.util.SerializeUtils;
import org.rocksdb.RocksDBException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Trie {
    private static final BigInteger one = new BigInteger("1");

    public Node root;
    public BigInteger branchCount, extensionCount, leafCount;
    public BigInteger readCount, updateCount, writeCount, readTime, updateTime, writeTime;
    public BigInteger ioCount, ioTime;
    public BigInteger hashCount, insertHashSuffix, updateHashSuffix;
    public BigInteger serializeTime, deserializeTime;
    private final String insertHash, updateHash;
    public HashMap<String, Node> persistentNodes;

    public Trie() {
        root = null;
        branchCount = new BigInteger("0"); extensionCount = new BigInteger("0"); leafCount = new BigInteger("0");
        readCount = new BigInteger("0"); updateCount = new BigInteger("0"); writeCount = new BigInteger("0");
        readTime = new BigInteger("0"); updateTime = new BigInteger("0"); writeTime = new BigInteger("0");
        ioCount = new BigInteger("0"); ioTime = new BigInteger("0");
        hashCount = new BigInteger("0"); insertHashSuffix = new BigInteger("0"); updateHashSuffix = new BigInteger("0");
        serializeTime = new BigInteger("0"); deserializeTime = new BigInteger("0");
        insertHash = Common.bytesToHex(HashUtils.generateHash());
        updateHash = Common.bytesToHex(HashUtils.generateHash());
        persistentNodes = new HashMap<>();
    }

    public Trie(String rootHash, String branchCount, String extensionCount, String leafCount) throws RocksDBException {
        this.branchCount = new BigInteger(branchCount); this.extensionCount = new BigInteger(extensionCount); this.leafCount = new BigInteger(leafCount);
        readCount = new BigInteger("0"); updateCount = new BigInteger("0"); writeCount = new BigInteger("0");
        readTime = new BigInteger("0"); updateTime = new BigInteger("0"); writeTime = new BigInteger("0");
        ioCount = new BigInteger("0"); ioTime = new BigInteger("0");
        hashCount = new BigInteger("0"); insertHashSuffix = new BigInteger("0"); updateHashSuffix = new BigInteger("0");
        serializeTime = new BigInteger("0"); deserializeTime = new BigInteger("0");
        insertHash = Common.bytesToHex(HashUtils.generateHash());
        updateHash = Common.bytesToHex(HashUtils.generateHash());
        persistentNodes = new HashMap<>();
        root = initNode(rootHash);
    }

    @Override
    public String toString() {
        return "Trie{" + "\n" +
                "   root=" + root + "\n" +
                "   branchCount=" + branchCount + "\n" +
                "   extensionCount=" + extensionCount + "\n" +
                "   leafCount=" + leafCount + "\n" +
                "   readCount=" + readCount + "\n" +
                "   updateCount=" + updateCount + "\n" +
                "   writeCount=" + writeCount + "\n" +
                "   readTime=" + readTime + "\n" +
                "   updateTime=" + updateTime + "\n" +
                "   writeTime=" + writeTime + "\n" +
                "   ioCount=" + ioCount + "\n" +
                "   ioTime=" + ioTime + "\n" +
                "   hashCount=" + hashCount + "\n" +
                "   insertHashSuffix=" + insertHashSuffix + "\n" +
                "   updateHashSuffix=" + updateHashSuffix + "\n" +
                "   serializeTime=" + serializeTime + "\n" +
                "   deserializeTime=" + deserializeTime + "\n" +
                "   insertHash='" + insertHash + '\'' + "\n" +
                "   updateHash='" + updateHash + '\'' + "\n" +
                "   persistentNodes=" + persistentNodes + "\n" +
                '}';
    }

    private Node initNode(String hash) throws RocksDBException {
        ioCount = ioCount.add(one);
        long time = System.nanoTime();
        byte[] bytes = RocksDBUtils.get(Common.hexToBytes(hash));
        ioTime = ioTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));

        time = System.nanoTime();
        Node node = SerializeUtils.deserializeNode(bytes);
        deserializeTime = deserializeTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));

        if (node instanceof BranchNode branchNode) {
            branchNode.children = new Node[16];
            branchNode.hash = hash;
            return branchNode;
        }
        else if (node instanceof ExtensionNode extensionNode) {
            extensionNode.hash = hash;
            return extensionNode;
        }
        else if (node instanceof LeafNode leafNode) {
            leafNode.hash = hash;
            return leafNode;
        }
        else {
            System.err.println("遍历过程中出现节点种类错误！");
            throw new RuntimeException("节点种类异常");
        }
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

    public String generateInsertHash() {
        String res;
        if (insertHashSuffix.toString().length() % 2 == 0) res = insertHash + insertHashSuffix.toString();
        else res = insertHash + "0" + insertHashSuffix.toString();
        insertHashSuffix = insertHashSuffix.add(one);
        return res;
    }

    public String generateUpdateHash() {
        String res;
        if (updateHashSuffix.toString().length() % 2 == 0) res = updateHash + updateHashSuffix.toString();
        else res = updateHash + "0" + insertHashSuffix.toString();
        updateHashSuffix = updateHashSuffix.add(one);
        hashCount = hashCount.add(one);
        return res;
    }

    public void insertCommit(Boolean writeBack) throws RocksDBException {
        for (Map.Entry<String, Node> entry : persistentNodes.entrySet()) {
            long time = System.nanoTime();
            byte[] bytes = SerializeUtils.serializeNode(entry.getValue());
            serializeTime = serializeTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));

            time = System.nanoTime();
            RocksDBUtils.put(Common.hexToBytes(entry.getKey()), bytes);
            writeTime = writeTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
        }
        persistentNodes = new HashMap<>();
        if (writeBack) {
            long time = System.nanoTime();
            RocksDBUtils.commit();
            writeTime = writeTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
        }
        if (writeBack) writeCount = writeCount.add(one);
        root.hash();
    }

    public void updateCommit(Boolean writeBack) throws RocksDBException {
        for (Map.Entry<String, Node> entry : persistentNodes.entrySet()) {
            long time = System.nanoTime();
            byte[] bytes = SerializeUtils.serializeNode(entry.getValue());
            serializeTime = serializeTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));

            time = System.nanoTime();
            RocksDBUtils.put(Common.hexToBytes(entry.getKey()), bytes);
            writeTime = writeTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));

            time = System.nanoTime();
            RocksDBUtils.put(Common.hexToBytes(generateUpdateHash()), bytes);
            writeTime = writeTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
        }
        persistentNodes = new HashMap<>();
        if (writeBack) {
            long time = System.nanoTime();
            RocksDBUtils.commit();
            writeTime = writeTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
        }
        if (writeBack) writeCount = writeCount.add(one);
        root.hash();
    }

    private Node insert(Node node, String restKey, String value) {
        if (node == null) {
            leafCount = leafCount.add(one);
            LeafNode leafNode = new LeafNode(restKey, value, generateInsertHash());
            persistentNodes.put(leafNode.hash, leafNode);
            return leafNode;
        }
        else {
            if (node instanceof BranchNode branchNode) {
                int idx = getIndex(restKey.charAt(0));
                branchNode.children[idx] = insert(branchNode.children[idx], restKey.substring(1), value);
                branchNode.childrenHash[idx] = branchNode.children[idx].hash();
                persistentNodes.put(branchNode.hash, branchNode);
                return branchNode;
            }
            else if (node instanceof ExtensionNode extensionNode) {
                String prefix = findCommonPrefix(restKey, extensionNode.key);
                if (prefix.isEmpty()) {
                    BranchNode branchNode = new BranchNode(generateInsertHash());
                    int idx1 = getIndex(restKey.charAt(0)), idx2 = getIndex(extensionNode.key.charAt(0));
                    branchNode.children[idx1] = insert(branchNode.children[idx1], restKey.substring(1), value);
                    branchNode.childrenHash[idx1] = branchNode.children[idx1].hash();
                    if (extensionNode.key.substring(1).isEmpty()) {
                        branchNode.children[idx2] = extensionNode.child;
                        branchNode.childrenHash[idx2] = branchNode.children[idx2].hash();
                        extensionCount = extensionCount.subtract(one);
                    }
                    else {
                        ExtensionNode extensionNode1 = new ExtensionNode(extensionNode.key.substring(1), generateInsertHash());
                        extensionNode1.child = extensionNode.child;
                        extensionNode1.childHash = extensionNode.childHash;
                        branchNode.children[idx2] = extensionNode1;
                        branchNode.childrenHash[idx2] = extensionNode1.hash;
                        persistentNodes.put(extensionNode1.hash, extensionNode1);
                    }
                    branchCount = branchCount.add(one);
                    persistentNodes.remove(extensionNode.hash);
                    persistentNodes.put(branchNode.hash, branchNode);
                    return branchNode;
                }
                else {
                    if (restKey.startsWith(extensionNode.key)) {
                        extensionNode.child = insert(extensionNode.child, restKey.substring(extensionNode.key.length()), value);
                        extensionNode.childHash = extensionNode.child.hash();
                        persistentNodes.put(extensionNode.hash, extensionNode);
                        return extensionNode;
                    }
                    else {
                        ExtensionNode extensionNode1 = new ExtensionNode(prefix, generateInsertHash());
                        BranchNode branchNode = new BranchNode(generateInsertHash());
                        int idx = prefix.length(), idx1 = getIndex(restKey.charAt(idx)), idx2 = getIndex(extensionNode.key.charAt(idx));
                        branchNode.children[idx1] = insert(branchNode.children[idx1], restKey.substring(idx + 1), value);
                        branchNode.childrenHash[idx1] = branchNode.children[idx1].hash();
                        if (extensionNode.key.substring(idx + 1).isEmpty()) {
                            branchNode.children[idx2] = extensionNode.child;
                            branchNode.childrenHash[idx2] = branchNode.children[idx2].hash();
                        }
                        else {
                            ExtensionNode extensionNode2 = new ExtensionNode(extensionNode.key.substring(idx + 1), generateInsertHash());
                            extensionNode2.child = extensionNode.child;
                            extensionNode2.childHash = extensionNode.childHash;
                            branchNode.children[idx2] = extensionNode2;
                            branchNode.childrenHash[idx2] = branchNode.children[idx2].hash();
                            extensionCount = extensionCount.add(one);
                            persistentNodes.put(extensionNode2.hash, extensionNode2);
                        }
                        extensionNode1.child = branchNode;
                        extensionNode1.childHash = branchNode.hash;
                        branchCount = branchCount.add(one);
                        persistentNodes.remove(extensionNode.hash);
                        persistentNodes.put(extensionNode1.hash, extensionNode1);
                        persistentNodes.put(branchNode.hash, branchNode);
                        return extensionNode1;
                    }
                }
            }
            else if (node instanceof LeafNode leafNode) {
                if (restKey.equals(leafNode.key)) {
                    leafNode.value = value;
                    persistentNodes.put(leafNode.hash, leafNode);
                    return leafNode;
                }
                else {
                    String prefix = findCommonPrefix(restKey, leafNode.key);
                    if (prefix.isEmpty()) {
                        BranchNode branchNode = new BranchNode(generateInsertHash());
                        int idx1 = getIndex(restKey.charAt(0)), idx2 = getIndex(leafNode.key.charAt(0));
                        LeafNode leafNode1 = new LeafNode(restKey.substring(1), value, generateInsertHash()),
                                leafNode2 = new LeafNode(leafNode.key.substring(1), leafNode.value, generateInsertHash());
                        branchNode.children[idx1] = leafNode1;
                        branchNode.childrenHash[idx1] = branchNode.children[idx1].hash();
                        branchNode.children[idx2] = leafNode2;
                        branchNode.childrenHash[idx2] = branchNode.children[idx2].hash();
                        branchCount = branchCount.add(one);
                        leafCount = leafCount.add(one);
                        persistentNodes.remove(leafNode.hash);
                        persistentNodes.put(branchNode.hash, branchNode);
                        persistentNodes.put(leafNode1.hash, leafNode1);
                        persistentNodes.put(leafNode2.hash, leafNode2);
                        return branchNode;
                    }
                    else {
                        ExtensionNode extensionNode = new ExtensionNode(prefix, generateInsertHash());
                        BranchNode branchNode = new BranchNode(generateInsertHash());
                        int idx = prefix.length(), idx1 = getIndex(restKey.charAt(idx)), idx2 = getIndex(leafNode.key.charAt(idx));
                        LeafNode leafNode1 = new LeafNode(restKey.substring(idx + 1), value, generateInsertHash()),
                                leafNode2 = new LeafNode(leafNode.key.substring(idx + 1), leafNode.value, generateInsertHash());
                        branchNode.children[idx1] = leafNode1;
                        branchNode.childrenHash[idx1] = leafNode1.hash;
                        branchNode.children[idx2] = leafNode2;
                        branchNode.childrenHash[idx2] = leafNode2.hash;
                        extensionNode.child = branchNode;
                        extensionNode.childHash = branchNode.hash;
                        branchCount = branchCount.add(one);
                        extensionCount = extensionCount.add(one);
                        leafCount = leafCount.add(one);
                        persistentNodes.remove(leafNode.hash);
                        persistentNodes.put(extensionNode.hash, extensionNode);
                        persistentNodes.put(branchNode.hash, branchNode);
                        persistentNodes.put(leafNode1.hash, leafNode1);
                        persistentNodes.put(leafNode2.hash, leafNode2);
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

    public String insert(String key, String value) {
        root = insert(root, key, value);
        return root.hash();
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

    private int getProofSize(Node node, String restKey) {
        if (node == null) return 0;
        else {
            if (node instanceof BranchNode branchNode) {
                int cnt = 0, idx = getIndex(restKey.charAt(0));
                for (int i = 0; i < 16; i ++ )
                    if (i != idx && branchNode.childrenHash[i] != null) cnt ++ ;
                return getProofSize(branchNode.children[idx], restKey.substring(1)) + cnt;
            }
            else if (node instanceof ExtensionNode extensionNode) return getProofSize(extensionNode.child, restKey.substring(extensionNode.key.length()));
            else if (node instanceof LeafNode) return 0;
            else {
                System.err.println("遍历过程中出现节点种类错误！");
                throw new RuntimeException("节点种类异常");
            }
        }
    }

    public int getProofSize(String key) {
        return getProofSize(root, key);
    }

    private String get(Node node, String restKey) throws RocksDBException {
        if (node == null) return null;
        else {
            if (node instanceof BranchNode branchNode) {
                int idx = getIndex(restKey.charAt(0));
                if (branchNode.children[idx] == null && branchNode.childrenHash[idx] != null)
                    branchNode.children[idx] = initNode(branchNode.childrenHash[idx]);
                return get(branchNode.children[idx], restKey.substring(1));
            }
            else if (node instanceof ExtensionNode extensionNode) {
                if (restKey.startsWith(extensionNode.key)) {
                    if (extensionNode.child == null && extensionNode.childHash != null)
                        extensionNode.child = initNode(extensionNode.childHash);
                    return get(extensionNode.child, restKey.substring(extensionNode.key.length()));
                }
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

    public String get(String key) throws RocksDBException {
        readCount = readCount.add(one);
        long time = System.nanoTime();
        String res = get(root, key);
        readTime = readTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
        return res;
    }

    private void update(Node node, String restKey, String value) {
        if (node instanceof BranchNode branchNode) {
            int idx = getIndex(restKey.charAt(0));
            update(branchNode.children[idx], restKey.substring(1), value);
            persistentNodes.put(branchNode.hash, branchNode);
            //persistentNodes.put(generateUpdateHash(), branchNode);
        }
        else if (node instanceof ExtensionNode extensionNode) {
            if (restKey.startsWith(extensionNode.key)) {
                update(extensionNode.child, restKey.substring(extensionNode.key.length()), value);
                persistentNodes.put(extensionNode.hash, extensionNode);
                //persistentNodes.put(generateUpdateHash(), extensionNode);
            }
        }
        else if (node instanceof LeafNode leafNode) {
            leafNode.value = value;
            persistentNodes.put(leafNode.hash, leafNode);
            //persistentNodes.put(generateUpdateHash(), leafNode);
        }
        else {
            System.err.println("遍历过程中出现节点种类错误！");
            throw new RuntimeException("节点种类异常");
        }
    }

    public String update(String key, String value) {
        updateCount = updateCount.add(one);
        long time = System.nanoTime();
        update(root, key, value);
        updateTime = updateTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
        return root.hash();
    }
}