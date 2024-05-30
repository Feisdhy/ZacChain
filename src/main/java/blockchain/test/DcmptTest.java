package blockchain.test;

import blockchain.dcmpt.OptTrie;
import blockchain.util.AddressUtils;
import blockchain.util.Common;
import blockchain.util.RocksDBUtils;
import org.rocksdb.RocksDBException;

import java.util.ArrayList;

public class DcmptTest {
    public static int threshold = 100_000;

    public static void test1() throws RocksDBException {
        RocksDBUtils.openDatabase("rocksDB");
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < threshold; i ++ ) list.add(Common.bytesToHex(AddressUtils.generateAddress()));

        OptTrie trie = new OptTrie(16);
        for (String key : list) trie.insert(key, key);
        trie.insertCommit(true);
        System.out.println(trie);

        /*long total1 = 0, total2 = 0;
        int min1 = Integer.MAX_VALUE, max1 = Integer.MIN_VALUE, min2 = Integer.MAX_VALUE, max2 = Integer.MIN_VALUE;
        for (String key : list) {
            int current = trie.getPathLength(key);
            min1 = Math.min(min1, current);
            max1 = Math.max(max1, current);
            total1 += current;

            current = trie.getProofSize(key);
            min2 = Math.min(min2, current);
            max2 = Math.max(max2, current);
            total2 += current;
        }
        System.out.println("Length analysis " + min1 + " " + max1 + " " + total1);
        System.out.println("Proof analysis " + min2 + " " + max2 + " " + total2);*/

        OptTrie newTrie = new OptTrie(trie.root.hash(), 16, trie.compactCount.toString(), trie.leafCount.toString(), trie.slotCount.toString());
        for (String key : list) {
            String value = newTrie.get(key);
            if (key.equals(value)) newTrie.update(key, "");
            else {
                System.err.println("错误！");
                break;
            }
        }
        newTrie.updateCommit(true);
        System.out.println(newTrie);

        OptTrie newTrie1 = new OptTrie(newTrie.root.hash(), 16, newTrie.compactCount.toString(), newTrie.leafCount.toString(), newTrie.slotCount.toString());
        for (String key : list) {
            String value = newTrie1.get(key);
            if (!value.isEmpty()) {
                System.err.println("错误！");
                break;
            }
        }
        System.out.println(newTrie1);
        RocksDBUtils.close();
    }

    public static void test2() throws RocksDBException {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < threshold; i ++ ) list.add(Common.bytesToHex(AddressUtils.generateAddress()));

        OptTrie trie = new OptTrie(1);
        for (String key : list) trie.insert(key, "");
        for (String key : list) System.out.println(trie.get(key));

        System.out.println();

        for (String key : list) trie.insert(key, key);
        for (String key : list) System.out.println(trie.get(key));
    }
}
