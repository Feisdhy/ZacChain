package blockchain.test;

import blockchain.mpt.Trie;
import blockchain.util.AddressUtils;
import blockchain.util.Common;
import blockchain.util.RocksDBUtils;
import org.rocksdb.RocksDBException;

import java.util.ArrayList;

public class MptTest {
    public static int threshold = 100_000;

    public static void test1() throws RocksDBException {
        RocksDBUtils.openDatabase("rocksDB");
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < threshold; i ++ ) list.add(Common.bytesToHex(AddressUtils.generateAddress()));

        Trie trie = new Trie();
        for (String key : list) trie.insert(key, key);
        trie.insertCommit(true);
        System.out.println(trie);

        Trie newTrie = new Trie(trie.root.hash(), trie.branchCount.toString(), trie.extensionCount.toString(), trie.leafCount.toString());
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

        Trie newTrie1 = new Trie(newTrie.root.hash(), newTrie.branchCount.toString(), newTrie.extensionCount.toString(), newTrie.leafCount.toString());
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

        Trie trie = new Trie();
        for (String key : list) trie.insert(key, "");
        for (String key : list) System.out.println(trie.get(key));

        System.out.println();

        for (String key : list) trie.insert(key, key);
        for (String key : list) System.out.println(trie.get(key));
    }
}
