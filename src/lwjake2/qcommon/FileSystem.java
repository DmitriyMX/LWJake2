/*
 * DmitriyMX <dimon550@gmail.com>
 * 2018-03-06
 */
package lwjake2.qcommon;

import lwjake2.game.cvar_t;

import java.io.RandomAccessFile;

public interface FileSystem {
    void init(); // FS.InitFilesystem();
    byte[] loadFile(String path);
    void read(byte[] buffer, int len, RandomAccessFile file);
    void execAutoexec();
    void createPath(String path);
    int fileLength(String filename);

    int developer_searchpath(int who);
    cvar_t getGamedirVar();

    void setGamedir(String dir);
    String getGamedir();
}
