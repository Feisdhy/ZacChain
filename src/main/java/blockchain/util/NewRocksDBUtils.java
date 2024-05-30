package blockchain.util;

import org.rocksdb.*;

public class NewRocksDBUtils {
    static {
        RocksDB.loadLibrary();
    }

    public final RocksDB db;
    public final Options options;
    public final WriteBatch writeBatch;
    public final WriteOptions writeOptions;

    // Constructor to initialize and open a database
    public NewRocksDBUtils(String dbPath) throws RocksDBException {
        this.options = new Options().setCreateIfMissing(true);
        this.db = RocksDB.open(options, dbPath);
        this.writeBatch = new WriteBatch();
        this.writeOptions = new WriteOptions();
    }

    // Method to put data into the database
    public void put(byte[] key, byte[] value) throws RocksDBException {
        writeBatch.put(key, value);
    }

    // Method to delete data from the database
    public void delete(byte[] key) throws RocksDBException {
        writeBatch.delete(key);
    }

    // Method to commit changes to the database
    public void commit() throws RocksDBException {
        db.write(writeOptions, writeBatch);
        // Clear the existing WriteBatch for reuse
        writeBatch.clear();
    }

    // Method to commit changes immediately and flush them
    public void commitImmediately() throws RocksDBException {
        db.write(writeOptions, writeBatch);
        writeBatch.clear();

        // Ensure FlushOptions are properly closed using try-with-resources
        try (FlushOptions flushOptions = new FlushOptions().setWaitForFlush(true)) {
            db.flush(flushOptions);
        }
    }

    // Method to get data from the database
    public byte[] get(byte[] key) throws RocksDBException {
        return db.get(key);
    }

    // Method to close all resources
    public void close() {
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
