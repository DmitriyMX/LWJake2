/*
 * Copyright (C) 1997-2001 Id Software, Inc.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package lwjake2.qcommon;

import lombok.extern.slf4j.Slf4j;
import lwjake2.Defines;
import lwjake2.Globals;
import lwjake2.game.Cmd;
import lwjake2.game.cvar_t;
import lwjake2.sys.Sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static lwjake2.Defines.*;

/*
 * ==================================================
 *
 * QUAKE FILESYSTEM
 *
 * @author cwei
 * @author DmitriyMX <dimon550@gmail.com>
 * @modify 2018-03-06
 *
 * ==================================================
 */

@Slf4j
public class BaseQ2FileSystem implements FileSystem {
    private static final BaseQ2FileSystem instance = new BaseQ2FileSystem();
    private static final int IDPAKHEADER = (('K' << 24) + ('C' << 16) + ('A' << 8) + 'P');
    private static final int MAX_FILES_IN_PACK = 4096;
    // read in blocks of 64k
    private static final int MAX_READ = 0x10000;

    public static BaseQ2FileSystem getInstance() {
        return instance;
    }

    private BaseQ2FileSystem() {}

    private static class packfile_t {
        static final int SIZE = 64;
        static final int NAME_SIZE = 56;
        String name; // char name[56]
        int filepos, filelen;
        public String toString() {
            return name + " [ length: " + filelen + " pos: " + filepos + " ]";
        }
    }

    private static class pack_t {
        String filename;
        RandomAccessFile handle;
        ByteBuffer backbuffer;
        int numfiles;
        Hashtable<String, packfile_t> files; // with packfile_t entries
    }

    private String fs_gamedir;

    private String fs_userdir;

    private cvar_t fs_basedir;

    private cvar_t fs_cddir;

    private cvar_t fs_gamedirvar;

    private static class filelink_t {
        String from;
        int fromlength;
        String to;
    }

    // with filelink_t entries
    private List<filelink_t> fs_links = new LinkedList<>();

    private static class searchpath_t {
        String filename;
        pack_t pack; // only one of filename or pack will be used
        searchpath_t next;
    }

    private searchpath_t fs_searchpaths;

    // without gamedirs
    private searchpath_t fs_base_searchpaths;

    /*
     * All of Quake's data access is through a hierchal file system, but the
     * contents of the file system can be transparently merged from several
     * sources.
     * 
     * The "base directory" is the path to the directory holding the quake.exe
     * and all game directories. The sys_* files pass this to host_init in
     * quakeparms_t->basedir. This can be overridden with the "-basedir" command
     * line parm to allow code debugging in a different directory. The base
     * directory is only used during filesystem initialization.
     * 
     * The "game directory" is the first tree on the search path and directory
     * that all generated files (savegames, screenshots, demos, config files)
     * will be saved to. This can be overridden with the "-game" command line
     * parameter. The game directory can never be changed while quake is
     * executing. This is a precacution against having a malicious server
     * instruct clients to write files over areas they shouldn't.
     *  
     */

    /*
     * CreatePath
     *
     * Creates any directories needed to store the given filename.
     */
    @Override
    public void createPath(String path) {
        int index = path.lastIndexOf('/');
        // -1 if not found and 0 means write to root
        if (index > 0) {
            File f = new File(path.substring(0, index));
            if (!f.mkdirs() && !f.isDirectory()) {
                log.warn("can't create path \"{}\"", path);
            }
        }
    }

    @Override
    public int fileLength(String filename) {
        searchpath_t search;
        String netpath;
        pack_t pak;

        file_from_pak = 0;

        // check for links first
        for (filelink_t link : fs_links) {
            if (filename.regionMatches(0, link.from, 0, link.fromlength)) {
                netpath = link.to + filename.substring(link.fromlength);
                File file = new File(netpath);
                if (file.canRead()) {
                    Com.DPrintf("link file: " + netpath + '\n');
                    return (int) file.length();
                }
                return -1;
            }
        }

        // search through the path, one element at a time

        for (search = fs_searchpaths; search != null; search = search.next) {
            // is the element a pak file?
            if (search.pack != null) {
                // look through all the pak file elements
                pak = search.pack;
                filename = filename.toLowerCase();
                packfile_t entry = pak.files.get(filename);

                if (entry != null) {
                    // found it!
                    file_from_pak = 1;
                    Com.DPrintf("PackFile: " + pak.filename + " : " + filename
                            + '\n');
                    // open a new file on the pakfile
                    File file = new File(pak.filename);
                    if (!file.canRead()) {
                        Com.Error(Defines.ERR_FATAL, "Couldn't reopen "
                                + pak.filename);
                    }
                    return entry.filelen;
                }
            } else {
                // check a file in the directory tree
                netpath = search.filename + '/' + filename;

                File file = new File(netpath);
                if (!file.canRead())
                    continue;

                Com.DPrintf("FindFile: " + netpath + '\n');

                return (int) file.length();
            }
        }
        Com.DPrintf("FindFile: can't find " + filename + '\n');
        return -1;
    }

    private int file_from_pak = 0;

    /*
     * FOpenFile
     *
     * Finds the file in the search path. returns a RadomAccesFile. Used for
     * streaming data out of either a pak file or a seperate file.
     */
    @Override
    public RandomAccessFile FOpenFile(String filename) throws IOException {
        searchpath_t search;
        String netpath;
        pack_t pak;
        File file;

        file_from_pak = 0;

        // check for links first
        for (filelink_t link : fs_links) {
            //			if (!strncmp (filename, link->from, link->fromlength))
            if (filename.regionMatches(0, link.from, 0, link.fromlength)) {
                netpath = link.to + filename.substring(link.fromlength);
                file = new File(netpath);
                if (file.canRead()) {
                    //Com.DPrintf ("link file: " + netpath +'\n');
                    return new RandomAccessFile(file, "r");
                }
                return null;
            }
        }

        //
        // search through the path, one element at a time
        //
        for (search = fs_searchpaths; search != null; search = search.next) {
            // is the element a pak file?
            if (search.pack != null) {
                // look through all the pak file elements
                pak = search.pack;
                filename = filename.toLowerCase();
                packfile_t entry = pak.files.get(filename);

                if (entry != null) {
                    // found it!
                    file_from_pak = 1;
                    //Com.DPrintf ("PackFile: " + pak.filename + " : " +
                    // filename + '\n');
                    file = new File(pak.filename);
                    if (!file.canRead())
                        Com.Error(Defines.ERR_FATAL, "Couldn't reopen "
                                + pak.filename);
                    if (pak.handle == null || !pak.handle.getFD().valid()) {
                        // hold the pakfile handle open
                        pak.handle = new RandomAccessFile(pak.filename, "r");
                    }
                    // open a new file on the pakfile

                    RandomAccessFile raf = new RandomAccessFile(file, "r");
                    raf.seek(entry.filepos);

                    return raf;
                }
            } else {
                // check a file in the directory tree
                netpath = search.filename + '/' + filename;

                file = new File(netpath);
                if (!file.canRead())
                    continue;

                //Com.DPrintf("FindFile: " + netpath +'\n');

                return new RandomAccessFile(file, "r");
            }
        }
        //Com.DPrintf ("FindFile: can't find " + filename + '\n');
        return null;
    }

    /**
     * Read
     *
     * Properly handles partial reads
     */
    @Override
    public void read(byte[] buffer, int len, RandomAccessFile file) {
        int block, remaining;
        int offset = 0;
        int read;

        // read in chunks for progress bar
        remaining = len;

        while (remaining != 0) {
            block = Math.min(remaining, MAX_READ);
            try {
                read = file.read(buffer, offset, block);
            } catch (IOException e) {
                log.error(e.toString());
                throw new RuntimeException("Look log file");
            }

            if (read == 0) {
                log.error("FS_Read: 0 bytes read");
                throw new RuntimeException("Look log file");
            } else if (read == -1) {
                log.error("FS_Read: -1 bytes read");
                throw new RuntimeException("Look log file");
            }
            //
            // do some progress bar thing here...
            //
            remaining -= read;
            offset += read;
        }
    }

    /*
     * LoadFile
     *
     * Filename are reletive to the quake search path a null buffer will just
     * return the file content as byte[]
     */
    @Override
    public byte[] loadFile(String path) {
        RandomAccessFile file;

        byte[] buf = null;
        int len;

        // TODO hack for bad strings (fuck \0)
        int index = path.indexOf('\0');
        if (index != -1)
            path = path.substring(0, index);

        // look for it in the filesystem or pack files
        len = fileLength(path);

        if (len < 1)
            return null;

        try {
            file = FOpenFile(path);
            //Read(buf = new byte[len], len, h);
            buf = new byte[len];
            assert file != null;
            file.readFully(buf);
            file.close();
        } catch (IOException e) {
            Com.Error(Defines.ERR_FATAL, e.toString());
        }
        return buf;
    }

    /*
     * LoadMappedFile
     *
     * Filename are reletive to the quake search path a null buffer will just
     * return the file content as ByteBuffer (memory mapped)
     */
    @Override
    public ByteBuffer loadMappedFile(String filename) {
        searchpath_t search;
        String netpath;
        pack_t pak;
        File file;

        int fileLength;
        FileChannel channel = null;
        FileInputStream input = null;
        ByteBuffer buffer;

        file_from_pak = 0;

        try {
            // check for links first
            for (filelink_t link : fs_links) {
                if (filename.regionMatches(0, link.from, 0, link.fromlength)) {
                    netpath = link.to + filename.substring(link.fromlength);
                    file = new File(netpath);
                    if (file.canRead()) {
                        input = new FileInputStream(file);
                        channel = input.getChannel();
                        fileLength = (int) channel.size();
                        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
                                fileLength);
                        input.close();
                        return buffer;
                    }
                    return null;
                }
            }

            //
            // search through the path, one element at a time
            //
            for (search = fs_searchpaths; search != null; search = search.next) {
                // is the element a pak file?
                if (search.pack != null) {
                    // look through all the pak file elements
                    pak = search.pack;
                    filename = filename.toLowerCase();
                    packfile_t entry = pak.files.get(filename);

                    if (entry != null) {
                        // found it!
                        file_from_pak = 1;
                        //Com.DPrintf ("PackFile: " + pak.filename + " : " +
                        // filename + '\n');
                        file = new File(pak.filename);
                        if (!file.canRead())
                            Com.Error(Defines.ERR_FATAL, "Couldn't reopen "
                                    + pak.filename);
                        if (pak.handle == null || !pak.handle.getFD().valid()) {
                            // hold the pakfile handle open
                            pak.handle = new RandomAccessFile(pak.filename, "r");
                        }
                        // open a new file on the pakfile
                        if (pak.backbuffer == null) {
                            channel = pak.handle.getChannel();
                            pak.backbuffer = channel.map(
                                    FileChannel.MapMode.READ_ONLY, 0,
                                    pak.handle.length());
                            channel.close();
                        }
                        pak.backbuffer.position(entry.filepos);
                        buffer = pak.backbuffer.slice();
                        buffer.limit(entry.filelen);
                        return buffer;
                    }
                } else {
                    // check a file in the directory tree
                    netpath = search.filename + '/' + filename;

                    file = new File(netpath);
                    if (!file.canRead())
                        continue;

                    //Com.DPrintf("FindFile: " + netpath +'\n');
                    input = new FileInputStream(file);
                    channel = input.getChannel();
                    fileLength = (int) channel.size();
                    buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
                            fileLength);
                    input.close();
                    return buffer;
                }
            }
        } catch (Exception ignored) {
        }
        try {
            if (input != null)
                input.close();
            else if (channel != null && channel.isOpen())
                channel.close();
        } catch (IOException ignored) {
        }
        return null;
    }

    private static class dpackheader_t {
        int ident; // IDPAKHEADER
        int dirofs;
        int dirlen;
    }

    // buffer for C-Strings char[56]
    private byte[] tmpText = new byte[packfile_t.NAME_SIZE];

    /*
     * LoadPackFile
     *
     * Takes an explicit (not game tree related) path to a pak file.
     *
     * Loads the header and directory, adding the files at the beginning of the
     * list so they override previous pack files.
     */
    private pack_t loadPackFile(String packfile) {
        dpackheader_t header;
        Hashtable<String, packfile_t> newfiles;
        RandomAccessFile file;
        int numpackfiles;
        pack_t pack;
        //		unsigned checksum;
        //
        try {
            file = new RandomAccessFile(packfile, "r");
            FileChannel fc = file.getChannel();
            ByteBuffer packhandle = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            packhandle.order(ByteOrder.LITTLE_ENDIAN);

            fc.close();

            if (packhandle.limit() < 1)
                return null;
            //
            header = new dpackheader_t();
            header.ident = packhandle.getInt();
            header.dirofs = packhandle.getInt();
            header.dirlen = packhandle.getInt();

            if (header.ident != IDPAKHEADER)
                Com.Error(Defines.ERR_FATAL, packfile + " is not a packfile");

            numpackfiles = header.dirlen / packfile_t.SIZE;

            if (numpackfiles > MAX_FILES_IN_PACK)
                Com.Error(Defines.ERR_FATAL, packfile + " has " + numpackfiles
                        + " files");

            newfiles = new Hashtable<>(numpackfiles);

            packhandle.position(header.dirofs);

            // parse the directory
            packfile_t entry;

            for (int i = 0; i < numpackfiles; i++) {
                packhandle.get(tmpText);

                entry = new packfile_t();
                entry.name = new String(tmpText).trim();
                entry.filepos = packhandle.getInt();
                entry.filelen = packhandle.getInt();

                newfiles.put(entry.name.toLowerCase(), entry);
            }

        } catch (IOException e) {
            Com.DPrintf(e.getMessage() + '\n');
            return null;
        }

        pack = new pack_t();
        pack.filename = packfile;
        pack.handle = file;
        pack.numfiles = numpackfiles;
        pack.files = newfiles;

        log.info("Added packfile {} ({} files)", packfile, numpackfiles);

        return pack;
    }

    /*
     * AddGameDirectory
     *
     * Sets fs_gamedir, adds the directory to the head of the path, then loads
     * and adds pak1.pak pak2.pak ...
     */
    private void addGameDirectory(String dir) {
        int i;
        searchpath_t search;
        pack_t pak;
        String pakfile;

        fs_gamedir = dir;

        //
        // add the directory to the search path
        // ensure fs_userdir is first in searchpath
        search = new searchpath_t();
        search.filename = dir;
        if (fs_searchpaths != null) {
            search.next = fs_searchpaths.next;
            fs_searchpaths.next = search;
        } else {
            fs_searchpaths = search;
        }

        //
        // add any pak files in the format pak0.pak pak1.pak, ...
        //
        for (i = 0; i < 10; i++) {
            pakfile = dir + "/pak" + i + ".pak";
            if (!(new File(pakfile).canRead()))
                continue;

            pak = loadPackFile(pakfile);
            if (pak == null)
                continue;

            search = new searchpath_t();
            search.pack = pak;
            search.filename = "";
            search.next = fs_searchpaths;
            fs_searchpaths = search;
        }
    }

    /*
     * Gamedir
     * 
     * Called to find where to write a file (demos, savegames, etc)
     * this is modified to <user.home>/.lwjake2 
     */
    @Override
    public String getGamedir() {
        return (fs_userdir != null) ? fs_userdir : Globals.BASEDIRNAME;
    }

    /*
     * BaseGamedir
     * 
     * Called to find where to write a downloaded file
     */
    @Override
    public String getBaseGamedir() {
        return (fs_gamedir != null) ? fs_gamedir : Globals.BASEDIRNAME;
    }

    /*
     * ExecAutoexec
     */
    @Override
    public void execAutoexec() {
        String dir = fs_userdir;

        String name;
        if (dir != null && dir.length() > 0) {
            name = dir + "/autoexec.cfg";
        } else {
            name = fs_basedir.string + '/' + Globals.BASEDIRNAME
                    + "/autoexec.cfg";
        }

        int canthave = Defines.SFF_SUBDIR | Defines.SFF_HIDDEN
                | Defines.SFF_SYSTEM;

        if (Sys.FindAll(name, 0, canthave) != null) {
            Cbuf.AddText("exec autoexec.cfg\n");
        }
    }

    /*
     * SetGamedir
     *
     * Sets the gamedir and path to a different directory.
     */
    @Override
    public void setGamedir(String dir) {
        searchpath_t next;

        if (dir.contains("..") || dir.contains("/") || dir.contains("\\") || dir.contains(":")) {
            log.warn("Gamedir should be a single filename, not a path");
            return;
        }

        //
        // free up any current game dir info
        //
        while (fs_searchpaths != fs_base_searchpaths) {
            if (fs_searchpaths.pack != null) {
                try {
                    fs_searchpaths.pack.handle.close();
                } catch (IOException e) {
                    Com.DPrintf(e.getMessage() + '\n');
                }
                // clear the hashtable
                fs_searchpaths.pack.files.clear();
                fs_searchpaths.pack.files = null;
                fs_searchpaths.pack = null;
            }
            next = fs_searchpaths.next;
            fs_searchpaths = next;
        }

        //
        // flush all data, so it will be forced to reload
        //
        if ((Globals.dedicated != null) && (Globals.dedicated.value == 0.0f))
            Cbuf.AddText("vid_restart\nsnd_restart\n");

        fs_gamedir = fs_basedir.string + '/' + dir;

        if (dir.equals(Globals.BASEDIRNAME) || (dir.length() == 0)) {
            Cvar.FullSet("gamedir", "", CVAR_SERVERINFO | CVAR_NOSET);
            Cvar.FullSet("game", "", CVAR_LATCH | CVAR_SERVERINFO);
        } else {
            Cvar.FullSet("gamedir", dir, CVAR_SERVERINFO | CVAR_NOSET);
            if (fs_cddir.string != null && fs_cddir.string.length() > 0)
                addGameDirectory(fs_cddir.string + '/' + dir);

            addGameDirectory(fs_basedir.string + '/' + dir);
        }
    }

    /*
     * Link_f
     * 
     * Creates a filelink_t
     */
    private void link_f() {
        filelink_t entry;

        if (Cmd.Argc() != 3) {
            log.info("USAGE: link <from> <to>");
            return;
        }

        // see if the link already exists
        for (Iterator<filelink_t> it = fs_links.iterator(); it.hasNext();) {
            entry = it.next();

            if (entry.from.equals(Cmd.Argv(1))) {
                if (Cmd.Argv(2).length() < 1) {
                    // delete it
                    it.remove();
                    return;
                }
                entry.to = Cmd.Argv(2);
                return;
            }
        }

        // create a new link if the <to> is not empty
        if (Cmd.Argv(2).length() > 0) {
            entry = new filelink_t();
            entry.from = Cmd.Argv(1);
            entry.fromlength = entry.from.length();
            entry.to = Cmd.Argv(2);
            fs_links.add(entry);
        }
    }

    /*
     * ListFiles
     */
    @Override
    public String[] listFiles(String findname, int musthave, int canthave) {
        String[] list = new String[0];

        File[] files = Sys.FindAll(findname, musthave, canthave);

        if (files != null) {
            list = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                list[i] = files[i].getPath();
            }
        }

        return list;
    }

    /*
     * Dir_f
     */
    private void dir_f() {
        String path = null;
        String findname;
        String wildcard = "*.*";
        String[] dirnames;

        if (Cmd.Argc() != 1) {
            wildcard = Cmd.Argv(1);
        }

        while ((path = nextPath(path)) != null) {
            findname = path + '/' + wildcard;

            log.info("Directory of {}", findname);
            log.info("----");

            dirnames = listFiles(findname, 0, 0);

            if (dirnames.length != 0) {
                int index;
                for (String dirname : dirnames) {
                    if ((index = dirname.lastIndexOf('/')) > 0) {
                        log.info(dirname.substring(index + 1, dirname.length()));
                    } else {
                        log.info(dirname);
                    }
                }
            }

        }
    }

    /*
     * Path_f
     */
    private void path_f() {
        searchpath_t s;

        log.info("Current search path:");
        for (s = fs_searchpaths; s != null; s = s.next) {
            if (s == fs_base_searchpaths)
                log.info("----------");
            if (s.pack != null)
                log.info("{} ({} files)", s.pack.filename, s.pack.numfiles);
            else
                log.info(s.filename);
        }

        log.info("Links:");
        for (filelink_t link : fs_links) {
            log.info("{} : {}", link.from, link.to);
        }
    }

    /*
     * NextPath
     *
     * Allows enumerating all of the directories in the search path
     */
    @Override
    public String nextPath(String prevpath) {
        searchpath_t s;
        String prev;

        if (prevpath == null || prevpath.length() == 0)
            return fs_gamedir;

        prev = fs_gamedir;
        for (s = fs_searchpaths; s != null; s = s.next) {
            if (s.pack != null)
                continue;

            if (prevpath.equals(prev))
                return s.filename;

            prev = s.filename;
        }

        return null;
    }

    /*
     * InitFilesystem
     */
    @Override
    public void init() {
        Cmd.AddCommand("path", this::path_f);
        Cmd.AddCommand("link", this::link_f);
        Cmd.AddCommand("dir", this::dir_f);

        fs_userdir = System.getProperty("user.home") + "/.lwjake2";
        createPath(fs_userdir + "/");
        addGameDirectory(fs_userdir);

        //
        // basedir <path>
        // allows the game to run from outside the data tree
        //
        fs_basedir = Cvar.Get("basedir", ".", CVAR_NOSET);

        //
        // cddir <path>
        // Logically concatenates the cddir after the basedir for
        // allows the game to run from outside the data tree
        //

        setCDDir();

        //
        // start up with baseq2 by default
        //
        addGameDirectory(fs_basedir.string + '/' + Globals.BASEDIRNAME);

        // any set gamedirs will be freed up to here
        markBaseSearchPaths();

        // check for game override
        checkOverride();
    }

    /**
     * set baseq2 directory
     */
    @Override
    public void setCDDir() {
        fs_cddir = Cvar.Get("cddir", "", CVAR_ARCHIVE);
        if (fs_cddir != null && fs_cddir.string.length() > 0)
            addGameDirectory(fs_cddir.string + '/' + Globals.BASEDIRNAME);
    }

    /**
     * Check for "+set game" override - Used to properly set gamedir
     */
    @Override
    public void checkOverride() {
        fs_gamedirvar = Cvar.Get("game", "", CVAR_LATCH | CVAR_SERVERINFO);

        if (fs_gamedirvar != null && fs_gamedirvar.string.length() > 0) {
            setGamedir(fs_gamedirvar.string);
        }
    }

    @Override
    public void markBaseSearchPaths() {
        // any set gamedirs will be freed up to here
        fs_base_searchpaths = fs_searchpaths;
    }

    //	RAFAEL
    /*
     * Developer_searchpath
     */
    @Override
    public int developer_searchpath(int who) {
        // PMM - warning removal
        //	 char *start;
        searchpath_t s;

        for (s = fs_searchpaths; s != null; s = s.next) {
            if (s.filename.contains("xatrix"))
                return 1;

            if (s.filename.contains("rogue"))
                return 2;
        }

        return 0;
    }

    @Override
    public int getFileFromPak() {
        return file_from_pak;
    }

    @Override
    public cvar_t getGamedirVar() {
        return fs_gamedirvar;
    }
}