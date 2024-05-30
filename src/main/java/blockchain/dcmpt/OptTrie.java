package blockchain.dcmpt;

import blockchain.dcmpt.slot.StateSlot;
import blockchain.global.Node;
import blockchain.util.Common;
import blockchain.util.HashUtils;
import blockchain.util.RocksDBUtils;
import blockchain.util.SerializeUtils;
import org.rocksdb.RocksDBException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class OptTrie {
    private static final BigInteger one = new BigInteger("1");

    public Node root;
    public int threshold;
    public BigInteger compactCount, leafCount, slotCount;
    public BigInteger readCount, updateCount, writeCount, readTime, updateTime, writeTime;
    public BigInteger ioCount, ioTime;
    public BigInteger hashCount, insertHashSuffix, updateHashSuffix;
    public BigInteger serializeTime, deserializeTime;
    private final String insertHash, updateHash;
    public HashMap<String, Node> persistentNodes;

    public OptTrie(int threshold) {
        root = null;
        this.threshold = threshold;
        compactCount = new BigInteger("0"); leafCount = new BigInteger("0"); slotCount = new BigInteger("0");
        readCount = new BigInteger("0"); updateCount = new BigInteger("0"); writeCount = new BigInteger("0");
        readTime = new BigInteger("0"); updateTime = new BigInteger("0"); writeTime = new BigInteger("0");
        ioCount = new BigInteger("0"); ioTime = new BigInteger("0");
        hashCount = new BigInteger("0"); insertHashSuffix = new BigInteger("0"); updateHashSuffix = new BigInteger("0");
        serializeTime = new BigInteger("0"); deserializeTime = new BigInteger("0");
        insertHash = Common.bytesToHex(HashUtils.generateHash());
        updateHash = Common.bytesToHex(HashUtils.generateHash());
        persistentNodes = new HashMap<>();
    }

    public OptTrie(String rootHash, int threshold, String compactCount, String leafCount, String slotCount) throws RocksDBException {
        this.threshold = threshold;
        this.compactCount = new BigInteger(compactCount); this.leafCount = new BigInteger(leafCount); this.slotCount = new BigInteger(slotCount);
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
        return "OptTrie{" + "\n" +
                "   root=" + root + "\n" +
                "   threshold=" + threshold + "\n" +
                "   compactCount=" + compactCount + "\n" +
                "   leafCount=" + leafCount + "\n" +
                "   slotCount=" + slotCount + "\n" +
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

        if (node instanceof CompactNode compactNode) {
            compactNode.hash = hash;
            return compactNode;
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
            if (node instanceof CompactNode compactNode) {
                int idx = getIndex(restKey.charAt(0));
                if (compactNode.segments[idx] == null) compactNode.segments[idx] = new Segment(threshold);
                Segment segment = compactNode.segments[idx];
                if (segment.hasSpace()) {
                    segment.insertStateSlot(restKey.substring(1), value);
                    slotCount = slotCount.add(one);
                }
                else if (segment.trieSlot == null) {
                    segment.trieSlot = insert(null, restKey.substring(1), value);
                    segment.trieSlotHash = segment.trieSlot.hash();
                }
                else {
                    segment.trieSlot = insert(segment.trieSlot, restKey.substring(1), value);
                    segment.trieSlotHash = segment.trieSlot.hash();
                }
                return compactNode;
            }
            else if (node instanceof LeafNode leafNode) {
                if (restKey.equals(leafNode.key)) {
                    leafNode.value = value;
                    return leafNode;
                }
                else {
                    CompactNode compactNode = new CompactNode(generateInsertHash());
                    int idx1 = getIndex(restKey.charAt(0)), idx2 = getIndex(leafNode.key.charAt(0));
                    Segment segment = new Segment(threshold);
                    compactNode.segments[idx2] = segment;
                    segment.insertStateSlot(leafNode.key.substring(1), leafNode.value);
                    if (idx1 != idx2) {
                        Segment segment1 = new Segment(threshold);
                        compactNode.segments[idx1] = segment1;
                        segment1.insertStateSlot(restKey.substring(1), value);
                        slotCount = slotCount.add(one);
                    }
                    else if (segment.hasSpace()) {
                        segment.insertStateSlot(restKey.substring(1), value);
                        slotCount = slotCount.add(one);
                    }
                    else {
                        segment.trieSlot = insert(null, restKey.substring(1), value);
                        segment.trieSlotHash = segment.trieSlot.hash();
                    }
                    slotCount = slotCount.add(one);
                    leafCount = leafCount.subtract(one);
                    compactCount = compactCount.add(one);
                    persistentNodes.remove(leafNode.hash);
                    persistentNodes.put(compactNode.hash, compactNode);
                    return compactNode;
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
            if (node instanceof CompactNode compactNode) {
                int idx = getIndex(restKey.charAt(0));
                Segment segment = compactNode.segments[idx];
                if (segment == null) return 0;
                else {
                    String res = segment.find(restKey.substring(1));
                    if (res != null) return 1;
                    else if (segment.trieSlot == null) return 0;
                    else return getPathLength(segment.trieSlot, restKey.substring(1)) + 1;
                }
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
            if (node instanceof CompactNode compactNode) {
                int cnt = 0, idx = getIndex(restKey.charAt(0));
                Segment segment = compactNode.segments[idx];
                String res = segment.find(restKey.substring(1));
                if (res != null) return 0;
                else {
                    for (int i = 0; i < 16; i ++ )
                        if (i != idx && compactNode.segments[i] != null && compactNode.segments[i].trieSlotHash != null) cnt ++ ;
                    return getProofSize(segment.trieSlot, restKey.substring(1)) + cnt;
                }
            }
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
            if (node instanceof CompactNode compactNode) {
                int idx = getIndex(restKey.charAt(0));
                Segment segment = compactNode.segments[idx];
                if (segment == null) return null;
                else {
                    String res = segment.find(restKey.substring(1));
                    if (res != null) return res;
                    else if (segment.trieSlot == null && segment.trieSlotHash == null) return null;
                    else {
                        if (segment.trieSlot == null) segment.trieSlot = initNode(segment.trieSlotHash);
                        return get(segment.trieSlot, restKey.substring(1));
                    }
                }
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
        if (node instanceof CompactNode compactNode) {
            int idx1 = getIndex(restKey.charAt(0));
            Segment segment = compactNode.segments[idx1];
            int idx2 = segment.getIndex(restKey.substring(1));
            if (idx2 != -1) {
                StateSlot stateSlot = segment.stateSlots[idx2];
                stateSlot.value = value;
            }
            else update(segment.trieSlot, restKey.substring(1), value);
            persistentNodes.put(compactNode.hash, compactNode);
            // persistentNodes.put(generateUpdateHash(), compactNode);
        }
        else if (node instanceof LeafNode leafNode) {
            leafNode.value = value;
            persistentNodes.put(leafNode.hash, leafNode);
            // persistentNodes.put(generateUpdateHash(), leafNode);
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

