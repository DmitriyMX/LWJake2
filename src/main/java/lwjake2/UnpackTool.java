package lwjake2;

import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.*;

@Slf4j
@UtilityClass
public class UnpackTool {

    private static final String READ_ONLY = "r";
    private static Map<String, Entry> listPakFiles = new HashMap<>();
    private static Map<String, RandomAccessFile> pakFiles = new HashMap<>();

    private static RandomAccessFile getPak(String pakName) {
        RandomAccessFile raf;

        if (!pakFiles.containsKey(pakName)) {
            try {
                raf = new RandomAccessFile(pakName, READ_ONLY);
            } catch (FileNotFoundException e) {
                log.error("Read file error: {}", e.getMessage());
                raf = null;
            }

            pakFiles.put(pakName, raf);
        } else {
            raf = pakFiles.get(pakName);
        }

        return raf;
    }

    public static void add(Entry entry) {
        listPakFiles.put(entry.getName(), entry);
    }

    public static void printList() {
        listPakFiles.values().stream()
                .sorted((o1, o2) -> {
                    final int result = o1.getPakName().compareTo(o2.getPakName());
                    if (result == 0) {
                        return o1.getName().compareTo(o2.getName());
                    } else {
                        return result;
                    }
                })
                .forEach(entry -> log.info("{}: {}", entry.getPakName(), entry.getName()));
    }

    public static Collection<Entry> list() {
        return listPakFiles.values();
    }

    public static Entry get(String name) {
        return listPakFiles.get(name);
    }

    @Value
    public static class Entry {
        private String name;
        private int pos;
        private long size;
        private String pakName;

        public Entry(String name, int pos, long size, String pakName) {
            int idx;
            if ((idx = name.indexOf('\0')) != -1) {
                this.name = name.substring(0, idx);
            } else {
                this.name = name;
            }

            this.pos = pos;
            this.size = size;
            this.pakName = pakName;
        }

        public byte[] getBytes() throws IOException {
            RandomAccessFile pak = UnpackTool.getPak(pakName);
            if (pak == null) {
                return null;
            }

            pak.seek(pos);

            byte[] result = new byte[(int) size];
            if (result.length < size) {
                log.error("Can't cast long to int: size too big for intValue");
                return null;
            }

            pak.readFully(result);
            return result;
        }
    }
}
