package blockchain.additional;

import blockchain.entity.Block;
import blockchain.memory.MemoryTrie;
import blockchain.util.*;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;

public class Experiment {
    // experiment/accounts存储的是全部区块中涉及的账户信息
    public static void showAccounts() throws RocksDBException {
        NewRocksDBUtils dbUtils = new NewRocksDBUtils("experiment/accounts");
        try (final RocksIterator iterator = dbUtils.db.newIterator()) {
            int cnt = 0;
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                // System.out.println(Common.bytesToHex(iterator.value()));
                cnt ++ ;
            }
            System.out.println(cnt);
        }
    }

    // experiment/block 存储的是全部区块中涉及的区块信息
    // 区块高度14000001至14049999
    public static void showBlocks() throws RocksDBException, InterruptedException {
        NewRocksDBUtils dbUtils = new NewRocksDBUtils("experiment/blocks");
        for (int i = 1; i < 50000; i ++ ) {
            String height = String.valueOf(14000000 + i);
            Block block = SerializeUtils.deserializeBlock(dbUtils.get(height.getBytes()));
            for (int k = 0; k < Objects.requireNonNull(block).n; k ++ ) {
                String from = block.transactions[k].from, to = block.transactions[k].to;
                System.out.println(from + " " + to);
            }
            System.out.println();
            Thread.sleep(1000);
        }
    }

    // a711355
    // a77d337
    // a7f9365
    // a77d397
    // 用来测试实例样图MPT的Extension Node压缩路径的效果
    public static void showExtensionNodesInExample() {
        MemoryTrie trie = new MemoryTrie();
        trie.insert("a711355", "");
        trie.insert("a77d337", "");
        trie.insert("a7f9365", "");
        trie.insert("a77d397", "");
        System.out.println(trie);
        trie.showExtensionNodes();
    }

    // 时间格式，用于标准化输出
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 用于生成剩余九千万个随机状态
    // 随机状态存储在experiment/randomAccounts中的value字段
    public static void generateRandomAccounts() throws RocksDBException {
        NewRocksDBUtils dbUtils = new NewRocksDBUtils("experiment/accounts");
        HashSet<String> set = new HashSet<>();
        try (final RocksIterator iterator = dbUtils.db.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next())
                set.add(Common.bytesToHex(iterator.value()));
        }
        dbUtils.close();

        dbUtils = new NewRocksDBUtils("experiment/randomAccounts");
        int threshold = 100_000_000 - set.size();
        for (int i = 1; i <= threshold; i ++ ) {
            byte[] bytes = AddressUtils.generateAddress();
            String address = Common.bytesToHex(bytes);
            if (set.contains(address)) i -- ;
            else {
                dbUtils.put(HashUtils.generateHash(), bytes);
                if ((set.size() + i) % 1_000_000 == 0)
                    System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已生成" + (set.size() + i) + "个账户信息");
            }
        }
        dbUtils.commit();
        dbUtils.close();
    }

    // 用来测试MPT的Extension Node压缩路径的效果
    public static void showExtensionNodes() throws RocksDBException {
        MemoryTrie trie = new MemoryTrie();
        NewRocksDBUtils dbUtils = new NewRocksDBUtils("experiment/accounts");
        HashSet<String> set = new HashSet<>();
        try (final RocksIterator iterator = dbUtils.db.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                String key = Common.bytesToHex(iterator.value());
                trie.insert(key, "");
                set.add(key);
                if (set.size() % 1_000_000 == 0)
                    System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已插入" + set.size() + "个账户信息");
            }
        }
        dbUtils.close();

        int threshold = 10_000_000;
        for (int i = 1; i <= threshold; i ++ ) {
            byte[] bytes = AddressUtils.generateAddress();
            String address = Common.bytesToHex(bytes);
            if (set.contains(address)) i -- ;
            else {
                trie.insert(address, "");
                if ((set.size() + i) % 1_000_000 == 0)
                    System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已插入" + (set.size() + i) + "个账户信息");
            }
        }

        /*System.out.println(trie);
        trie.showExtensionNodes();*/

        long max = 0, total = 0;
        for (String key : set) {
            int now = trie.getPathLength(key);
            max = Math.max(max, now);
            total += now;
        }
        System.out.println(max + " " + total);
    }
}
