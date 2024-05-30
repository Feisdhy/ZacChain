package blockchain.experiment;

import blockchain.dcmpt.OptTrie;
import blockchain.mpt.Trie;
import blockchain.util.AddressUtils;
import blockchain.util.Common;
import blockchain.util.HashUtils;
import blockchain.util.RocksDBUtils;
import org.rocksdb.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExperimentOne {
    public static void test() throws RocksDBException {
        openDatabase("rocksDB");
        for (int i = 1; i <= thresholds[4]; i ++ ) {
            put(HashUtils.generateHash(), AddressUtils.generateAddress());
            if (i % 1_000_000 == 0) System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已生成" + i + "个账户信息");
        }
        commit();
        close();
    }

    public static int[] thresholds = new int[] {
            10_000,
            100_000,
            1_000_000,
            10_000_000,
            100_000_000,
            300_000_000
    };

    public static String[] paths1 = new String[] {
            "MPT/1",
            "MPT/2",
            "MPT/3",
            "MPT/4",
            "MPT/5",
            "MPT/6",
    };

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void test1() throws RocksDBException {
        openDatabase("rocksDB");
        for (int i = 0, j = 0; i <= 3; i ++ ) {
            RocksDBUtils.openDatabase(paths1[i]);

            Trie trie = new Trie();
            try (final RocksIterator iterator = db.newIterator()) {
                j = 0;
                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    trie.insert(Common.bytesToHex(iterator.value()), "");
                    j ++ ;
                    if (j % 1_000_000 == 0) {
                        trie.insertCommit(true);
                        System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + j + "个账户信息");
                    }
                    if (j == thresholds[i]) break;
                }
            }
            trie.insertCommit(true);
            System.out.println(trie);

            /*long total1 = 0, total2 = 0;
            int min1 = Integer.MAX_VALUE, max1 = Integer.MIN_VALUE, min2 = Integer.MAX_VALUE, max2 = Integer.MIN_VALUE;
            try (final RocksIterator iterator = db.newIterator()) {
                j = 0;
                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    int current = trie.getPathLength(Common.bytesToHex(iterator.value()));
                    min1 = Math.min(min1, current);
                    max1 = Math.max(max1, current);
                    total1 += current;

                    current = trie.getProofSize(Common.bytesToHex(iterator.value()));
                    min2 = Math.min(min2, current);
                    max2 = Math.max(max2, current);
                    total2 += current;
                    j ++ ;
                    if (j % 1_000_000 == 0) System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + j + "个账户信息");
                    if (j == thresholds[i]) break;
                }
            }
            System.out.println("Length analysis " + min1 + " " + max1 + " " + total1);
            System.out.println("Proof analysis " + min2 + " " + max2 + " " + total2);*/

            RocksDBUtils.close();

            RocksDBUtils.openDatabase(paths1[i]);

            Trie newTrie = new Trie(trie.root.hash(), trie.branchCount.toString(), trie.extensionCount.toString(), trie.leafCount.toString());
            try (final RocksIterator iterator = db.newIterator()) {
                j = 0;
                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    newTrie.get(Common.bytesToHex(iterator.value()));
                    newTrie.update(Common.bytesToHex(iterator.value()), "");
                    j ++ ;
                    if (j % 1_000_000 == 0) {
                        newTrie.updateCommit(true);
                        System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已读写" + j + "个账户信息");
                    }
                    if (j == thresholds[i]) break;
                }
            }
            newTrie.updateCommit(true);
            System.out.println(newTrie);
            System.out.println();

            RocksDBUtils.close();
        }
        close();
    }

    public static String paths2 = "DCMPT";

    public static int[] slotThresholds = new int[] {
            1,
            4,
            16,
            64,
            256
    };

    private static String generatePath(String threshold, String round) {
        return paths2 + "/" + threshold + "/" + round;
    }

    public static void test2() throws RocksDBException {
        openDatabase("rocksDB");
        for (int i = 0; i < 5; i ++ ) {
            String threshold = "threshold" + String.valueOf(slotThresholds[i]);
            for (int j = 0, k = 0; j <= 3; j++) {
                String path = generatePath(threshold, String.valueOf(j + 1));
                RocksDBUtils.openDatabase(path);

                OptTrie trie = new OptTrie(slotThresholds[i]);
                try (final RocksIterator iterator = db.newIterator()) {
                    k = 0;
                    for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                        trie.insert(Common.bytesToHex(iterator.value()), "");
                        k ++ ;
                        if (k% 1_000_000 == 0) System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + k + "个账户信息");
                        if (k == thresholds[j]) break;
                    }
                }
                trie.insertCommit(true);
                System.out.println(trie);

                /*long total1 = 0, total2 = 0;
                int min1 = Integer.MAX_VALUE, max1 = Integer.MIN_VALUE, min2 = Integer.MAX_VALUE, max2 = Integer.MIN_VALUE;
                try (final RocksIterator iterator = db.newIterator()) {
                    k = 0;
                    for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                        int current = trie.getPathLength(Common.bytesToHex(iterator.value()));
                        min1 = Math.min(min1, current);
                        max1 = Math.max(max1, current);
                        total1 += current;

                        current = trie.getProofSize(Common.bytesToHex(iterator.value()));
                        min2 = Math.min(min2, current);
                        max2 = Math.max(max2, current);
                        total2 += current;
                        k ++ ;
                        if (k% 1_000_000 == 0) System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + k + "个账户信息");
                        if (k == thresholds[j]) break;
                    }
                }
                System.out.println("Length analysis " + min1 + " " + max1 + " " + total1);
                System.out.println("Proof analysis " + min2 + " " + max2 + " " + total2);*/

                RocksDBUtils.close();

                RocksDBUtils.openDatabase(path);

                OptTrie newTrie = new OptTrie(trie.root.hash(), slotThresholds[i], trie.compactCount.toString(), trie.leafCount.toString(), trie.slotCount.toString());
                try (final RocksIterator iterator = db.newIterator()) {
                    k = 0;
                    for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                        newTrie.get(Common.bytesToHex(iterator.value()));
                        newTrie.update(Common.bytesToHex(iterator.value()), "");
                        k ++ ;
                        if (k % 1_000_000 == 0) {
                            newTrie.updateCommit(true);
                            System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已读写" + k + "个账户信息");
                        }
                        if (k == thresholds[j]) break;
                    }
                }
                newTrie.updateCommit(true);
                System.out.println(newTrie);
                System.out.println();

                RocksDBUtils.close();
            }
        }
        close();
    }

    static {
        RocksDB.loadLibrary();
    }

    private static RocksDB db;
    private static Options options;
    private static WriteBatch writeBatch;
    private static WriteOptions writeOptions;

    public static void openDatabase(String dbPath) throws RocksDBException {
        options = new Options().setCreateIfMissing(true);
        db = RocksDB.open(options, dbPath);
        writeBatch = new WriteBatch();
        writeOptions = new WriteOptions();
    }

    public static void put(byte[] key, byte[] value) throws RocksDBException {
        writeBatch.put(key, value);
    }

    public static void delete(byte[] key) throws RocksDBException {
        writeBatch.delete(key);
    }

    public static void commit() throws RocksDBException {
        db.write(writeOptions, writeBatch);
        // 清空现有的 WriteBatch 以便重新使用
        writeBatch.clear();
    }

    public static byte[] get(byte[] key) throws RocksDBException {
        return db.get(key);
    }

    public static void close() {
        if (writeBatch != null) {
            writeBatch.close();
        }
        if (writeOptions != null) {
            writeOptions.close();
        }
        if (db != null) {
            db.close();
        }
        if (options != null) {
            options.close();
        }
    }
}