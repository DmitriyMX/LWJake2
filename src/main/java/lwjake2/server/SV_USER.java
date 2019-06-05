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

package lwjake2.server;

import lombok.extern.slf4j.Slf4j;
import lwjake2.Defines;
import lwjake2.ErrorCode;
import lwjake2.Globals;
import dmx.lwjake2.UnpackLoader;
import lwjake2.game.Cmd;
import lwjake2.game.GameBase;
import lwjake2.game.Info;
import lwjake2.game.PlayerClient;
import lwjake2.game.edict_t;
import lwjake2.game.entity_state_t;
import lwjake2.game.usercmd_t;
import lwjake2.qcommon.*;
import lwjake2.util.Lib;

@Slf4j
public class SV_USER {
//    private static final FileSystem fileSystem = null/*BaseQ2FileSystem.getInstance()*/;

    static edict_t sv_player;

    public static class ucmd_t {
        public ucmd_t(String n, Runnable r) {
            name = n;
            this.r = r;
        }

        String name;

        Runnable r;
    }

    static ucmd_t u1 = new ucmd_t("new", SV_USER::SV_New_f);

    static ucmd_t ucmds[] = {
            // auto issued
            new ucmd_t("new", SV_USER::SV_New_f),
            new ucmd_t("configstrings", SV_USER::SV_Configstrings_f),
            new ucmd_t("baselines", SV_USER::SV_Baselines_f),
            new ucmd_t("begin", SV_USER::SV_Begin_f),
            new ucmd_t("nextserver", SV_USER::SV_Nextserver_f),
            new ucmd_t("disconnect", SV_USER::SV_Disconnect_f),

            // issued by hand at client consoles
            new ucmd_t("info", SV_USER::SV_ShowServerinfo_f),
            new ucmd_t("download", SV_USER::SV_BeginDownload_f),
            new ucmd_t("nextdl", SV_USER::SV_NextDownload_f)
    };

    public static final int MAX_STRINGCMDS = 8;

    /*
     * ============================================================
     * 
     * USER STRINGCMD EXECUTION
     * 
     * sv_client and sv_player will be valid.
     * ============================================================
     */

    /*
     * ================== SV_BeginDemoServer ==================
     */
    public static void SV_BeginDemoserver() {
        String name;

        name = "demos/" + SV_INIT.sv.name;
        SV_INIT.sv.demofile = UnpackLoader.loadFileAsRAF(name);
        if (SV_INIT.sv.demofile == null)
            Com.Error(ErrorCode.ERR_DROP, "Couldn't open " + name + "\n");
    }

    /*
     * ================ SV_New_f
     * 
     * Sends the first message from the server to a connected client. This will
     * be sent on the initial connection and upon each server load.
     * ================
     */
    public static void SV_New_f() {
        String gamedir;
        int playernum;
        edict_t ent;

        log.debug("New() from {}", SV_MAIN.sv_client.name);

        if (SV_MAIN.sv_client.state != Defines.cs_connected) {
            log.warn("New not valid -- already spawned");
            return;
        }

        // demo servers just dump the file message
        if (SV_INIT.sv.state == Defines.ss_demo) {
            SV_BeginDemoserver();
            return;
        }

        //
        // serverdata needs to go over for all types of servers
        // to make sure the protocol is right, and to set the gamedir
        //
        gamedir = Cvar.VariableString("gamedir");

        // send the serverdata
        MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                        Defines.svc_serverdata);
        MSG.WriteInt(SV_MAIN.sv_client.netchan.message,
                Defines.PROTOCOL_VERSION);
        
        MSG.WriteLong(SV_MAIN.sv_client.netchan.message,
                        SV_INIT.svs.spawncount);
        MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                SV_INIT.sv.attractloop ? 1 : 0);
        MSG.WriteString(SV_MAIN.sv_client.netchan.message, gamedir);

        if (SV_INIT.sv.state == Defines.ss_cinematic
                || SV_INIT.sv.state == Defines.ss_pic)
            playernum = -1;
        else
            //playernum = sv_client - svs.clients;
            playernum = SV_MAIN.sv_client.serverindex;

        MSG.WriteShort(SV_MAIN.sv_client.netchan.message, playernum);

        // send full levelname
        MSG.WriteString(SV_MAIN.sv_client.netchan.message,
                SV_INIT.sv.configstrings[Defines.CS_NAME]);

        //
        // game server
        // 
        if (SV_INIT.sv.state == Defines.ss_game) {
            // set up the entity for the client
            ent = GameBase.g_edicts[playernum + 1];
            ent.s.number = playernum + 1;
            SV_MAIN.sv_client.edict = ent;
            SV_MAIN.sv_client.lastcmd = new usercmd_t();

            // begin fetching configstrings
            MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                    Defines.svc_stufftext);
            MSG.WriteString(SV_MAIN.sv_client.netchan.message,
                    "cmd configstrings " + SV_INIT.svs.spawncount + " 0\n");
        }
        
    }

    /*
     * ================== SV_Configstrings_f ==================
     */
    public static void SV_Configstrings_f() {
        int start;

        log.debug("Configstrings() from {}", SV_MAIN.sv_client.name);

        if (SV_MAIN.sv_client.state != Defines.cs_connected) {
            log.warn("configstrings not valid -- already spawned");
            return;
        }

        // handle the case of a level changing while a client was connecting
        if (Lib.atoi(Cmd.Argv(1)) != SV_INIT.svs.spawncount) {
            log.warn("SV_Configstrings_f from different level");
            SV_New_f();
            return;
        }

        start = Lib.atoi(Cmd.Argv(2));

        // write a packet full of data

        while (SV_MAIN.sv_client.netchan.message.cursize < Defines.MAX_MSGLEN / 2
                && start < Defines.MAX_CONFIGSTRINGS) {
            if (SV_INIT.sv.configstrings[start] != null
                    && SV_INIT.sv.configstrings[start].length() != 0) {
                MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                        Defines.svc_configstring);
                MSG.WriteShort(SV_MAIN.sv_client.netchan.message, start);
                MSG.WriteString(SV_MAIN.sv_client.netchan.message,
                        SV_INIT.sv.configstrings[start]);
            }
            start++;
        }

        // send next command

        if (start == Defines.MAX_CONFIGSTRINGS) {
            MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                    Defines.svc_stufftext);
            MSG.WriteString(SV_MAIN.sv_client.netchan.message, "cmd baselines "
                    + SV_INIT.svs.spawncount + " 0\n");
        } else {
            MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                    Defines.svc_stufftext);
            MSG.WriteString(SV_MAIN.sv_client.netchan.message,
                    "cmd configstrings " + SV_INIT.svs.spawncount + " " + start
                            + "\n");
        }
    }

    /*
     * ================== SV_Baselines_f ==================
     */
    public static void SV_Baselines_f() {
        int start;
        entity_state_t nullstate;
        entity_state_t base;

        log.debug("Baselines() from {}", SV_MAIN.sv_client.name);

        if (SV_MAIN.sv_client.state != Defines.cs_connected) {
            log.warn("baselines not valid -- already spawned");
            return;
        }

        // handle the case of a level changing while a client was connecting
        if (Lib.atoi(Cmd.Argv(1)) != SV_INIT.svs.spawncount) {
            log.warn("SV_Baselines_f from different level");
            SV_New_f();
            return;
        }

        start = Lib.atoi(Cmd.Argv(2));

        //memset (&nullstate, 0, sizeof(nullstate));
        nullstate = new entity_state_t(null);

        // write a packet full of data

        while (SV_MAIN.sv_client.netchan.message.cursize < Defines.MAX_MSGLEN / 2
                && start < Defines.MAX_EDICTS) {
            base = SV_INIT.sv.baselines[start];
            if (base.modelindex != 0 || base.sound != 0 || base.effects != 0) {
                MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                        Defines.svc_spawnbaseline);
                MSG.WriteDeltaEntity(nullstate, base,
                        SV_MAIN.sv_client.netchan.message, true, true);
            }
            start++;
        }

        // send next command

        if (start == Defines.MAX_EDICTS) {
            MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                    Defines.svc_stufftext);
            MSG.WriteString(SV_MAIN.sv_client.netchan.message, "precache "
                    + SV_INIT.svs.spawncount + "\n");
        } else {
            MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                    Defines.svc_stufftext);
            MSG.WriteString(SV_MAIN.sv_client.netchan.message, "cmd baselines "
                    + SV_INIT.svs.spawncount + " " + start + "\n");
        }
    }

    /*
     * ================== SV_Begin_f ==================
     */
    public static void SV_Begin_f() {
        log.debug("Begin() from {}", SV_MAIN.sv_client.name);

        // handle the case of a level changing while a client was connecting
        if (Lib.atoi(Cmd.Argv(1)) != SV_INIT.svs.spawncount) {
            log.warn("SV_Begin_f from different level");
            SV_New_f();
            return;
        }

        SV_MAIN.sv_client.state = Defines.cs_spawned;

        // call the game begin function
        PlayerClient.ClientBegin(SV_USER.sv_player);

        Cbuf.InsertFromDefer();
    }

    //=============================================================================

    /*
     * ================== SV_NextDownload_f ==================
     */
    public static void SV_NextDownload_f() {
        int r;
        int percent;
        int size;

        if (SV_MAIN.sv_client.download == null)
            return;

        r = SV_MAIN.sv_client.downloadsize - SV_MAIN.sv_client.downloadcount;
        if (r > 1024)
            r = 1024;

        MSG.WriteByte(SV_MAIN.sv_client.netchan.message, Defines.svc_download);
        MSG.WriteShort(SV_MAIN.sv_client.netchan.message, r);

        SV_MAIN.sv_client.downloadcount += r;
        size = SV_MAIN.sv_client.downloadsize;
        if (size == 0)
            size = 1;
        percent = SV_MAIN.sv_client.downloadcount * 100 / size;
        MSG.WriteByte(SV_MAIN.sv_client.netchan.message, percent);
        SZ.Write(SV_MAIN.sv_client.netchan.message, SV_MAIN.sv_client.download,
                SV_MAIN.sv_client.downloadcount - r, r);

        if (SV_MAIN.sv_client.downloadcount != SV_MAIN.sv_client.downloadsize)
            return;

        SV_MAIN.sv_client.download = null;
    }

    /*
     * ================== SV_BeginDownload_f ==================
     */
    public static void SV_BeginDownload_f() {
        String name;
        int offset = 0;

        name = Cmd.Argv(1);

        if (Cmd.Argc() > 2)
            offset = Lib.atoi(Cmd.Argv(2)); // downloaded offset

        // hacked by zoid to allow more conrol over download
        // first off, no .. or global allow check

        if (name.indexOf("..") != -1
                || SV_MAIN.allow_download.value == 0 // leading dot is no good
                || name.charAt(0) == '.' // leading slash bad as well, must be
                                         // in subdir
                || name.charAt(0) == '/' // next up, skin check
                || (name.startsWith("players/") && 0 == SV_MAIN.allow_download_players.value) // now
                                                                                              // models
                || (name.startsWith("models/") && 0 == SV_MAIN.allow_download_models.value) // now
                                                                                            // sounds
                || (name.startsWith("sound/") && 0 == SV_MAIN.allow_download_sounds.value)
                // now maps (note special case for maps, must not be in pak)
                || (name.startsWith("maps/") && 0 == SV_MAIN.allow_download_maps.value) // MUST
                                                                                        // be
                                                                                        // in a
                                                                                        // subdirectory
                || name.indexOf('/') == -1) { // don't allow anything with ..
                                              // path
            MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                    Defines.svc_download);
            MSG.WriteShort(SV_MAIN.sv_client.netchan.message, -1);
            MSG.WriteByte(SV_MAIN.sv_client.netchan.message, 0);
            return;
        }

        if (SV_MAIN.sv_client.download != null)
            SV_MAIN.sv_client.download = null;

        SV_MAIN.sv_client.download = UnpackLoader.loadFile(name);
        
        // rst: this handles loading errors, no message yet visible 
        if (SV_MAIN.sv_client.download == null)
        {            
            return;
        }
        
        SV_MAIN.sv_client.downloadsize = SV_MAIN.sv_client.download.length;
        SV_MAIN.sv_client.downloadcount = offset;

        if (offset > SV_MAIN.sv_client.downloadsize)
            SV_MAIN.sv_client.downloadcount = SV_MAIN.sv_client.downloadsize;

        if (SV_MAIN.sv_client.download == null // special check for maps, if it
                                               // came from a pak file, don't
                                               // allow
                                               // download ZOID
                || (name.startsWith("maps/") /*&& fileSystem.getFileFromPak() != 0*/)) {
            log.debug("Couldn't download {} to {}", name, SV_MAIN.sv_client.name);
            if (SV_MAIN.sv_client.download != null) {
                SV_MAIN.sv_client.download = null;
            }

            MSG.WriteByte(SV_MAIN.sv_client.netchan.message,
                    Defines.svc_download);
            MSG.WriteShort(SV_MAIN.sv_client.netchan.message, -1);
            MSG.WriteByte(SV_MAIN.sv_client.netchan.message, 0);
            return;
        }

        SV_NextDownload_f();
        log.debug("Downloading {} to {}", name, SV_MAIN.sv_client.name);
    }

    //============================================================================

    /*
     * ================= SV_Disconnect_f
     * 
     * The client is going to disconnect, so remove the connection immediately
     * =================
     */
    public static void SV_Disconnect_f() {
        //    SV_EndRedirect ();
        SV_MAIN.SV_DropClient(SV_MAIN.sv_client);
    }

    /*
     * ================== SV_ShowServerinfo_f
     * 
     * Dumps the serverinfo info string ==================
     */
    public static void SV_ShowServerinfo_f() {
        Info.Print(Cvar.Serverinfo());
    }

    public static void SV_Nextserver() {
        String v;

        //ZOID, ss_pic can be nextserver'd in coop mode
        if (SV_INIT.sv.state == Defines.ss_game
                || (SV_INIT.sv.state == Defines.ss_pic && 
                        0 == Cvar.VariableValue("coop")))
            return; // can't nextserver while playing a normal game

        SV_INIT.svs.spawncount++; // make sure another doesn't sneak in
        v = Cvar.VariableString("nextserver");
        //if (!v[0])
        if (v.length() == 0)
            Cbuf.AddText("killserver\n");
        else {
            Cbuf.AddText(v);
            Cbuf.AddText("\n");
        }
        Cvar.Set("nextserver", "");
    }

    /*
     * ================== SV_Nextserver_f
     * 
     * A cinematic has completed or been aborted by a client, so move to the
     * next server, ==================
     */
    public static void SV_Nextserver_f() {
        if (Lib.atoi(Cmd.Argv(1)) != SV_INIT.svs.spawncount) {
            log.debug("Nextserver() from wrong level, from {}", SV_MAIN.sv_client.name);
            return; // leftover from last server
        }

        log.debug("Nextserver() from {}", SV_MAIN.sv_client.name);

        SV_Nextserver();
    }

    /*
     * ================== SV_ExecuteUserCommand ==================
     */
    public static void SV_ExecuteUserCommand(String s) {

        log.debug("SV_ExecuteUserCommand:{}", s);
        SV_USER.ucmd_t u = null;

        Cmd.TokenizeString(s.toCharArray(), true);
        SV_USER.sv_player = SV_MAIN.sv_client.edict;

        //    SV_BeginRedirect (RD_CLIENT);

        int i = 0;
        for (; i < SV_USER.ucmds.length; i++) {
            u = SV_USER.ucmds[i];
            if (Cmd.Argv(0).equals(u.name)) {
                u.r.run();
                break;
            }
        }

        if (i == SV_USER.ucmds.length && SV_INIT.sv.state == Defines.ss_game)
            Cmd.ClientCommand(SV_USER.sv_player);

        //    SV_EndRedirect ();
    }

    /*
     * ===========================================================================
     * 
     * USER CMD EXECUTION
     * 
     * ===========================================================================
     */

    public static void SV_ClientThink(client_t cl, usercmd_t cmd) {
        cl.commandMsec -= cmd.msec & 0xFF;

        if (cl.commandMsec < 0 && SV_MAIN.sv_enforcetime.value != 0) {
            log.debug("commandMsec underflow from {}", cl.name);
            return;
        }

        PlayerClient.ClientThink(cl.edict, cmd);
    }

    /*
     * =================== SV_ExecuteClientMessage
     * 
     * The current net_message is parsed for the given client
     * ===================
     */
    public static void SV_ExecuteClientMessage(client_t cl) {
        int c;
        String s;

        usercmd_t nullcmd = new usercmd_t();
        usercmd_t oldest = new usercmd_t(), oldcmd = new usercmd_t(), newcmd = new usercmd_t();
        int net_drop;
        int stringCmdCount;
        int checksum, calculatedChecksum;
        int checksumIndex;
        boolean move_issued;
        int lastframe;

        SV_MAIN.sv_client = cl;
        SV_USER.sv_player = SV_MAIN.sv_client.edict;

        // only allow one move command
        move_issued = false;
        stringCmdCount = 0;

        while (true) {
            if (Globals.net_message.readcount > Globals.net_message.cursize) {
                log.warn("SV_ReadClientMessage: bad read:");
                log.warn("{}", Lib.hexDump(Globals.net_message.data, 32, false));
                SV_MAIN.SV_DropClient(cl);
                return;
            }

            c = MSG.ReadByte(Globals.net_message);
            if (c == -1)
                break;

            switch (c) {
            default:
                log.warn("SV_ReadClientMessage: unknown command char");
                SV_MAIN.SV_DropClient(cl);
                return;

            case Defines.clc_nop:
                break;

            case Defines.clc_userinfo:
                cl.userinfo = MSG.ReadString(Globals.net_message);
                SV_MAIN.SV_UserinfoChanged(cl);
                break;

            case Defines.clc_move:
                if (move_issued)
                    return; // someone is trying to cheat...

                move_issued = true;
                checksumIndex = Globals.net_message.readcount;
                checksum = MSG.ReadByte(Globals.net_message);
                lastframe = MSG.ReadLong(Globals.net_message);

                if (lastframe != cl.lastframe) {
                    cl.lastframe = lastframe;
                    if (cl.lastframe > 0) {
                        cl.frame_latency[cl.lastframe
                                & (Defines.LATENCY_COUNTS - 1)] = SV_INIT.svs.realtime
                                - cl.frames[cl.lastframe & Defines.UPDATE_MASK].senttime;
                    }
                }

                //memset (nullcmd, 0, sizeof(nullcmd));
                nullcmd = new usercmd_t();
                MSG.ReadDeltaUsercmd(Globals.net_message, nullcmd, oldest);
                MSG.ReadDeltaUsercmd(Globals.net_message, oldest, oldcmd);
                MSG.ReadDeltaUsercmd(Globals.net_message, oldcmd, newcmd);

                if (cl.state != Defines.cs_spawned) {
                    cl.lastframe = -1;
                    break;
                }

                // if the checksum fails, ignore the rest of the packet

                calculatedChecksum = Com.BlockSequenceCRCByte(
                        Globals.net_message.data, checksumIndex + 1,
                        Globals.net_message.readcount - checksumIndex - 1,
                        cl.netchan.incoming_sequence);

                if ((calculatedChecksum & 0xff) != checksum) {
                    log.debug("Failed command checksum for {} ({} != {})/{}",
                            cl.name,
                            calculatedChecksum,
                            checksum,
                            cl.netchan.incoming_sequence
                    );
                    return;
                }

                if (0 == SV_MAIN.sv_paused.value) {
                    net_drop = cl.netchan.dropped;
                    if (net_drop < 20) {

                        //if (net_drop > 2)

                        //    Com.Printf ("drop %i\n", net_drop);
                        while (net_drop > 2) {
                            SV_ClientThink(cl, cl.lastcmd);

                            net_drop--;
                        }
                        if (net_drop > 1)
                            SV_ClientThink(cl, oldest);

                        if (net_drop > 0)
                            SV_ClientThink(cl, oldcmd);

                    }
                    SV_ClientThink(cl, newcmd);
                }

                // copy.
                cl.lastcmd.set(newcmd);
                break;

            case Defines.clc_stringcmd:
                s = MSG.ReadString(Globals.net_message);

                // malicious users may try using too many string commands
                if (++stringCmdCount < SV_USER.MAX_STRINGCMDS)
                    SV_ExecuteUserCommand(s);

                if (cl.state == Defines.cs_zombie)
                    return; // disconnect command
                break;
            }
        }
    }
}