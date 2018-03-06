/*
 * DmitriyMX <dimon550@gmail.com>
 * 2018-03-06
 */
package lwjake2.qcommon;

import lwjake2.game.cvar_t;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public interface FileSystem {
    void init();
    byte[] loadFile(String path);
    void read(byte[] buffer, int len, RandomAccessFile file);
    void execAutoexec();
    void createPath(String path);
    int fileLength(String filename);
    String nextPath(String prevpath);
    String[] listFiles(String findname, int musthave, int canthave);

    int developer_searchpath(int who);
    cvar_t getGamedirVar();

    void setGamedir(String dir);
    String getGamedir();

    String getBaseGamedir();

    ByteBuffer loadMappedFile(String filename);
    void setCDDir();
    void markBaseSearchPaths();
    void checkOverride();
    RandomAccessFile FOpenFile(String filename) throws IOException;
    int getFileFromPak();
}
