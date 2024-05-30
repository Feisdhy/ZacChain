package blockchain.test;

import blockchain.entity.Block;
import blockchain.entity.Transaction;
import blockchain.util.*;
import org.rocksdb.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BlockTest {
    public static final String path = "D:/Project/leveldb/workloads/";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void test1() throws RocksDBException {
        RocksDBUtils.openDatabase("Blocks");
        for (int i = 1; i <= 20000; i ++ ) {
            String fileName = (14000000 + i) + ".txt", filePath = path + fileName;
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                List<Transaction> list = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    String from = parts[0].substring(2).toUpperCase(),
                            to = parts[1].substring(2).toUpperCase();
                    list.add(new Transaction(from, to));
                }
                System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " " + (14000000 + i) + " " + list.size());
                Block block = new Block(list);
                RocksDBUtils.put(String.valueOf(14000000 + i).getBytes(), SerializeUtils.serializeBlock(block));
                RocksDBUtils.commit();
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
            }
        }
        RocksDBUtils.close();
    }

    public static void test2() throws RocksDBException, InterruptedException {
        RocksDBUtils.openDatabase("Blocks");
        openDatabase("Accounts");
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 1; i <= 20000; i ++ ) {
            Block block = SerializeUtils.deserializeBlock(RocksDBUtils.get(String.valueOf(14000000 + i).getBytes()));
            for (int j = 0; j < Objects.requireNonNull(block).n; j ++ ) {
                if (block.transactions[j].from.length() != 40 || block.transactions[j].to.length() != 40) {
                    System.err.println("交易地址处理错误！");
                    break;
                }
                else {
                    map.put(block.transactions[j].from, map.getOrDefault(block.transactions[j].from, 0) + 1);
                    map.put(block.transactions[j].to, map.getOrDefault(block.transactions[j].to, 0) + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) put(HashUtils.generateHash(), Common.hexToBytes(entry.getKey()));
        commit();
        close();
        RocksDBUtils.close();
    }

    public static void test3() throws RocksDBException {
        openDatabase("Accounts");
        try (final RocksIterator iterator = db.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next())
                System.out.println(Common.bytesToHex(iterator.key()) + " " + Common.bytesToHex(iterator.value()));
        }
        close();
    }

    public static void test4() {
        int cnt = 0;
        while (true) {
            String address = Common.bytesToHex(AddressUtils.generateAddress());
            if (address.matches(".*[a-z].*")) {
                System.err.println("生成的地址存在小写字母");
                break;
            }
            if (++ cnt == 1_000_000) {
                System.out.println("所生成的地址均不存在小写字母");
                break;
            }
        }
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
