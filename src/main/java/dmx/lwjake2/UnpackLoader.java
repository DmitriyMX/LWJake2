package dmx.lwjake2;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@UtilityClass
public class UnpackLoader {

    @Getter
    @Setter
    private static String rootPath;

    private static File getFile(String path) {
        File file = new File(rootPath, path);
        if (!file.exists()) {
            log.warn("File '{}' not exists", file.getPath());
            return null;
        }

        return file;
    }

    @Nullable
    public static byte[] loadFile(String path) {
        File file = getFile(path);
        if (file == null) {
            return null;
        }

        byte[] result = new byte[(int) file.length()];
        if (result.length < file.length()) {
            log.error("Can't cast long to int: {} < {}", result.length, file.length());
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(result);
        } catch (IOException e) {
            log.error("Can't read file '{}': {}", file.getPath(), e.getMessage());
            return null;
        }

        return result;
    }

    @Nullable
    public static ByteBuffer loadFileAsByteBuffer(String path) {
        File file = getFile(path);
        if (file == null) {
            return null;
        }

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            return raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        } catch (IOException e) {
            log.error("Can't load file '{}' as ByteBuffer: {}", file.getPath(), e.getMessage());
            return null;
        }
    }

    public static RandomAccessFile loadFileAsRAF(String path) {
        File file = getFile(path);
        if (file == null) {
            return null;
        }

        try {
            return new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            log.error("Can't read file '{}': {}", file.getPath(), e.getMessage());
            return null;
        }
    }

    public static boolean exists(String path) {
        return Files.exists(Paths.get(rootPath, path));
    }
}
