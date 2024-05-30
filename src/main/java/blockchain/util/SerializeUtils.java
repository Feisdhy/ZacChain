package blockchain.util;

import blockchain.entity.Block;
import blockchain.global.Node;

import java.io.*;

public class SerializeUtils {
    public static byte[] serializeNode(Node node) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(node);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("序列化时出现错误！");
            return null;
        }
    }

    public static byte[] serializeBlock(Block block) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(block);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("序列化时出现错误！");
            return null;
        }
    }

    public static Node deserializeNode(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (Node) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("反序列化时出现错误！");
            return null;
        }
    }

    public static Block deserializeBlock(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (Block) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("反序列化时出现错误！");
            return null;
        }
    }
}
