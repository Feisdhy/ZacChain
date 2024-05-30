package blockchain.util;

import org.rocksdb.*;

public class RocksDBUtils {
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

    public static void commitImmediately() throws RocksDBException {
        db.write(writeOptions, writeBatch);
        // 清空现有的 WriteBatch 以便重新使用
        writeBatch.clear();

        // 使用 try-with-resources 语句确保 FlushOptions 被正确关闭
        try (FlushOptions flushOptions = new FlushOptions().setWaitForFlush(true)) {
            db.flush(flushOptions);
        }
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
