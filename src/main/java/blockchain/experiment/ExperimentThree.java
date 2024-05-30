package blockchain.experiment;

import blockchain.entity.Block;
import blockchain.util.AddressUtils;
import blockchain.util.Common;
import blockchain.util.HashUtils;
import blockchain.util.SerializeUtils;
import org.rocksdb.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class ExperimentThree {
    public static void test() throws RocksDBException {
        openDatabase1("experiment/accounts");
        HashSet<String> set = new HashSet<>();
        try (final RocksIterator iterator = db1.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next())
                set.add(Common.bytesToHex(iterator.value()));
        }
        close1();

        openDatabase1("experiment/randomAccounts");
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
        HashMap<String, Integer> res = new HashMap<>();
        openDatabase1("experiment/blocks");
        for (int i = 0; i < 200; i ++ ) {
            HashMap<String, Integer> current = new HashMap<>();
            for (int j = 1; j <= 100; j ++ ) {
                String height = String.valueOf(14000000 + i * 100 + j);
                Block block = SerializeUtils.deserializeBlock(get1(height.getBytes()));
                for (int k = 0; k < Objects.requireNonNull(block).n; k ++ ) {
                    String from = block.transactions[k].from, to = block.transactions[k].to;
                    current.put(from, current.getOrDefault(from, 0) + 1);
                    current.put(to, current.getOrDefault(to, 0) + 1);
                }
                // System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " 区块高度 " + height);
            }
            for (Map.Entry<String, Integer> entry : current.entrySet()) {
                String address = entry.getKey();
                if (entry.getValue() >= 33) res.put(address, res.getOrDefault(address, 0) + 1);
            }
        }
        close1();
        int cnt = 0, total = 0;
        for (Map.Entry<String, Integer> entry : res.entrySet()) {
            total ++ ;
            if (entry.getValue() >= 2) {
                cnt ++ ;
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        }
        System.out.println(cnt + " / " + total);
    }

    static {
        RocksDB.loadLibrary();
    }

    private static RocksDB db1, db2;
    private static Options options1, options2;
    private static WriteBatch writeBatch1, writeBatch2;
    private static WriteOptions writeOptions1, writeOptions2;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
