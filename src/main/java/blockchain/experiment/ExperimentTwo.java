package blockchain.experiment;

import blockchain.dcmpt.OptTrie;
import blockchain.entity.Block;
import blockchain.entity.Transaction;
import blockchain.mpt.Trie;
import blockchain.util.*;
import org.rocksdb.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

public class ExperimentTwo {
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final BigInteger one = new BigInteger("1"), two = new BigInteger("2");

    public static void test() throws RocksDBException {
        openDatabase1("Accounts");
        HashSet<String> set = new HashSet<>();
        try (final RocksIterator iterator = db1.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next())
                set.add(Common.bytesToHex(iterator.value()));
        }
        close1();

        openDatabase1("RandomAccounts");
        int threshold = 100_000_000 - set.size();
        for (int i = 1; i <= threshold; i ++ ) {
            byte[] bytes = AddressUtils.generateAddress();
            String address = Common.bytesToHex(bytes);
            if (set.contains(address)) i -- ;
            else {
                put1(HashUtils.generateHash(), bytes);
                if ((set.size() + i) % 1_000_000 == 0)
                    System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已生成" + (set.size() + i) + "个账户信息");
            }
        }
        commit1();
        close1();
    }

    public static void test1() throws RocksDBException {
        RocksDBUtils.openDatabase("Experiment/MPT");
        int cnt = 0;
        Trie trie = new Trie();
        openDatabase1("Accounts");
        try (final RocksIterator iterator = db1.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                cnt ++ ;
                String address = Common.bytesToHex(iterator.value());
                trie.insert(address, "");
                if (cnt % 1_000_000 == 0) {
                    trie.insertCommit(true);
                    System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + cnt + "个账户信息");
                }
            }
        }
        close1();
        openDatabase1("RandomAccounts");
        try (final RocksIterator iterator = db1.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                cnt ++ ;
                String address = Common.bytesToHex(iterator.value());
                trie.insert(address, "");
                if (cnt % 1_000_000 == 0) {
                    trie.insertCommit(true);
                    System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + cnt + "个账户信息");
                }
            }
        }
        close1();
        trie.insertCommit(true);
        System.out.println(trie);
        System.out.println(trie.root.hash() + " " + trie.branchCount.toString() + " " + trie.extensionCount.toString() + " " + trie.leafCount.toString());
        RocksDBUtils.close();

        /*RocksDBUtils.openDatabase("Experiment/MPT");
        Trie newTrie = new Trie(trie.root.hash(), trie.branchCount.toString(), trie.extensionCount.toString(), trie.leafCount.toString());
        BigInteger getTime = new BigInteger("0"), updateTime = new BigInteger("0"), commitTime = new BigInteger("0");
        BigInteger transactionCount = new BigInteger("0"), getCount = new BigInteger("0"), updateCount = new BigInteger("0"), commitCount = new BigInteger("0");
        openDatabase1("Blocks");
        for (int i = 1; i <= 20000; i ++ ) {
            Block block = SerializeUtils.deserializeBlock(get1(String.valueOf(14000000 + i).getBytes()));
            assert block != null;
            transactionCount = transactionCount.add(new BigInteger(String.valueOf(block.n)));

            for (int j = 0; j < block.n; j ++ ) {
                Transaction transaction = block.transactions[j];

                long time = System.nanoTime();
                newTrie.get(transaction.from);
                newTrie.get(transaction.to);
                getTime = getTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
                getCount = getCount.add(two);

                time = System.nanoTime();
                newTrie.update(transaction.from, "");
                newTrie.update(transaction.to, "");
                updateTime = updateTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
                updateCount = updateCount.add(two);
            }

            long time = System.nanoTime();
            newTrie.updateCommit(true);
            commitTime = commitTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
            commitCount = commitCount.add(one);

            System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 区块" + (14000000 + i) + "已完成");
        }
        close1();
        System.out.println(newTrie);
        System.out.println();
        RocksDBUtils.close();*/
    }

    public static void test2() throws RocksDBException {
        RocksDBUtils.openDatabase("Experiment/DCMPT1");
        int cnt = 0;
        OptTrie trie = new OptTrie(16);
        openDatabase1("Accounts");
        openDatabase2("RandomAccounts");
        try (final RocksIterator iterator1 = db1.newIterator()) {
            for (iterator1.seekToFirst(); iterator1.isValid(); iterator1.next()) {
                cnt ++ ;
                String address1 = Common.bytesToHex(iterator1.value());
                trie.insert(address1, "");
                if (cnt % 1_000_000 == 0) System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + cnt + "个账户信息");
                if (cnt == 1_000_000) {
                    try (final RocksIterator iterator2 = db2.newIterator()) {
                        for (iterator2.seekToFirst(); iterator2.isValid(); iterator2.next()) {
                            cnt ++ ;
                            String address2 = Common.bytesToHex(iterator2.value());
                            trie.insert(address2, "");
                            if (cnt % 1_000_000 == 0) System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + cnt + "个账户信息");
                        }
                    }
                }
            }
        }
        close1();
        close2();
        trie.insertCommit(true);
        System.out.println(trie);
        System.out.println(trie.root.hash() + " " + 16 + " " + trie.compactCount.toString() + " " + trie.leafCount.toString() + " " + trie.slotCount.toString());
        RocksDBUtils.close();

        /*RocksDBUtils.openDatabase("Experiment/DCMPT1");
        OptTrie newTrie = new OptTrie(trie.root.hash(), 16, trie.compactCount.toString(), trie.leafCount.toString(), trie.slotCount.toString());
        BigInteger getTime = new BigInteger("0"), updateTime = new BigInteger("0"), commitTime = new BigInteger("0");
        BigInteger transactionCount = new BigInteger("0"), getCount = new BigInteger("0"), updateCount = new BigInteger("0"), commitCount = new BigInteger("0");
        openDatabase1("Blocks");
        for (int i = 1; i <= 20000; i ++ ) {
            Block block = SerializeUtils.deserializeBlock(get1(String.valueOf(14000000 + i).getBytes()));
            assert block != null;
            transactionCount = transactionCount.add(new BigInteger(String.valueOf(block.n)));

            for (int j = 0; j < block.n; j ++ ) {
                Transaction transaction = block.transactions[j];

                long time = System.nanoTime();
                newTrie.get(transaction.from);
                newTrie.get(transaction.to);
                getTime = getTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
                getCount = getCount.add(two);

                time = System.nanoTime();
                newTrie.update(transaction.from, "");
                newTrie.update(transaction.to, "");
                updateTime = updateTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
                updateCount = updateCount.add(two);
            }

            long time = System.nanoTime();
            newTrie.updateCommit(true);
            commitTime = commitTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
            commitCount = commitCount.add(one);

            System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 区块" + (14000000 + i) + "已完成");
        }
        close1();
        System.out.println(newTrie);
        System.out.println();
        RocksDBUtils.close();*/
    }

    public static void test3() throws RocksDBException {
        RocksDBUtils.openDatabase("Experiment/DCMPT2");
        int cnt = 0;
        OptTrie trie = new OptTrie(16);
        openDatabase1("Accounts");
        try (final RocksIterator iterator = db1.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                cnt ++ ;
                String address = Common.bytesToHex(iterator.value());
                trie.insert(address, "");
                if (cnt % 1_000_000 == 0) System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + cnt + "个账户信息");
            }
        }
        close1();
        openDatabase1("RandomAccounts");
        try (final RocksIterator iterator = db1.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                cnt ++ ;
                String address = Common.bytesToHex(iterator.value());
                trie.insert(address, "");
                if (cnt % 1_000_000 == 0) System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 已存储" + cnt + "个账户信息");
            }
        }
        close1();
        trie.insertCommit(true);
        System.out.println(trie);
        System.out.println(trie.root.hash() + " " + 16 + " " + trie.compactCount.toString() + " " + trie.leafCount.toString() + " " + trie.slotCount.toString());
        RocksDBUtils.close();

        /*RocksDBUtils.openDatabase("Experiment/DCMPT2");
        OptTrie newTrie = new OptTrie(trie.root.hash(), 16, trie.compactCount.toString(), trie.leafCount.toString(), trie.slotCount.toString());
        BigInteger getTime = new BigInteger("0"), updateTime = new BigInteger("0"), commitTime = new BigInteger("0");
        BigInteger transactionCount = new BigInteger("0"), getCount = new BigInteger("0"), updateCount = new BigInteger("0"), commitCount = new BigInteger("0");
        openDatabase1("Blocks");
        for (int i = 1; i <= 20000; i ++ ) {
            Block block = SerializeUtils.deserializeBlock(get1(String.valueOf(14000000 + i).getBytes()));
            assert block != null;
            transactionCount = transactionCount.add(new BigInteger(String.valueOf(block.n)));

            for (int j = 0; j < block.n; j ++ ) {
                Transaction transaction = block.transactions[j];

                long time = System.nanoTime();
                newTrie.get(transaction.from);
                newTrie.get(transaction.to);
                getTime = getTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
                getCount = getCount.add(two);

                time = System.nanoTime();
                newTrie.update(transaction.from, "");
                newTrie.update(transaction.to, "");
                updateTime = updateTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
                updateCount = updateCount.add(two);
            }

            long time = System.nanoTime();
            newTrie.updateCommit(true);
            commitTime = commitTime.add(new BigInteger(String.valueOf(System.nanoTime() - time)));
            commitCount = commitCount.add(one);

            System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 区块" + (14000000 + i) + "已完成");
        }
        close1();
        System.out.println(newTrie);
        System.out.println();
        RocksDBUtils.close();*/
    }

    static {
        RocksDB.loadLibrary();
    }

    private static RocksDB db1, db2;
    private static Options options1, options2;
    private static WriteBatch writeBatch1, writeBatch2;
    private static WriteOptions writeOptions1, writeOptions2;

    public static void openDatabase1(String dbPath) throws RocksDBException {
        options1 = new Options().setCreateIfMissing(true);
        db1 = RocksDB.open(options1, dbPath);
        writeBatch1 = new WriteBatch();
        writeOptions1 = new WriteOptions();
    }

    public static void openDatabase2(String dbPath) throws RocksDBException {
        options2 = new Options().setCreateIfMissing(true);
        db2 = RocksDB.open(options2, dbPath);
        writeBatch2 = new WriteBatch();
        writeOptions2 = new WriteOptions();
    }

    public static void put1(byte[] key, byte[] value) throws RocksDBException {
        writeBatch1.put(key, value);
    }

    public static void put2(byte[] key, byte[] value) throws RocksDBException {
        writeBatch2.put(key, value);
    }

    public static void delete1(byte[] key) throws RocksDBException {
        writeBatch1.delete(key);
    }

    public static void delete2(byte[] key) throws RocksDBException {
        writeBatch2.delete(key);
    }

    public static void commit1() throws RocksDBException {
        db1.write(writeOptions1, writeBatch1);
        // 清空现有的 WriteBatch 以便重新使用
        writeBatch1.clear();
    }

    public static void commit2() throws RocksDBException {
        db2.write(writeOptions2, writeBatch2);
        // 清空现有的 WriteBatch 以便重新使用
        writeBatch2.clear();
    }

    public static byte[] get1(byte[] key) throws RocksDBException {
        return db1.get(key);
    }

    public static byte[] get2(byte[] key) throws RocksDBException {
        return db2.get(key);
    }

    public static void close1() {
        if (writeBatch1 != null) {
            writeBatch1.close();
        }
        if (writeOptions1 != null) {
            writeOptions1.close();
        }
        if (db1 != null) {
            db1.close();
        }
        if (options1 != null) {
            options1.close();
        }
    }

    public static void close2() {
        if (writeBatch2 != null) {
            writeBatch2.close();
        }
        if (writeOptions2 != null) {
            writeOptions2.close();
        }
        if (db2 != null) {
            db2.close();
        }
        if (options2 != null) {
            options2.close();
        }
    }
}
