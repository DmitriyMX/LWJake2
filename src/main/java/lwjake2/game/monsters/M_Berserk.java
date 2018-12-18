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

package lwjake2.game.monsters;

import lwjake2.Defines;
import lwjake2.game.EntDieAdapter;
import lwjake2.game.EntInteractAdapter;
import lwjake2.game.EntPainAdapter;
import lwjake2.game.EntThinkAdapter;
import lwjake2.game.GameAI;
import lwjake2.game.GameBase;
import lwjake2.game.GameMisc;
import lwjake2.game.GameUtil;
import lwjake2.game.GameWeapon;
import lwjake2.game.edict_t;
import lwjake2.game.mframe_t;
import lwjake2.game.mmove_t;
import lwjake2.util.Lib;
import lwjake2.util.Math3D;

public class M_Berserk {

    public final static int FRAME_stand1 = 0;

    public final static int FRAME_stand5 = 4;

    public final static int FRAME_standb1 = 5;

    public final static int FRAME_standb20 = 24;

    public final static int FRAME_walkc1 = 25;

    public final static int FRAME_walkc11 = 35;

    public final static int FRAME_run1 = 36;

    public final static int FRAME_run6 = 41;

    public final static int FRAME_att_c1 = 76;

    public final static int FRAME_att_c8 = 83;

    public final static int FRAME_att_c9 = 84;

    public final static int FRAME_att_c20 = 95;

    public final static int FRAME_att_c21 = 96;

    public final static int FRAME_att_c34 = 109;

    public final static int FRAME_painc1 = 199;

    public final static int FRAME_painc4 = 202;

    public final static int FRAME_painb1 = 203;

    public final static int FRAME_painb20 = 222;

    public final static int FRAME_death1 = 223;

    public final static int FRAME_death13 = 235;

    public final static int FRAME_deathc1 = 236;

    public final static int FRAME_deathc8 = 243;

    public final static float MODEL_SCALE = 1.000000f;

    static int sound_pain;

    static int sound_die;

    static int sound_idle;

    static int sound_punch;

    static int sound_sight;

    static int sound_search;

    static EntInteractAdapter berserk_sight = new EntInteractAdapter() {
        public String getID() { return "berserk_sight";}
        public boolean interact(edict_t self, edict_t other) {
            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_sight, 1,
                    Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static EntThinkAdapter berserk_search = new EntThinkAdapter() {
        public String getID() { return "berserk_search";}
        public boolean think(edict_t self) {
            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_search, 1,
                    Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static EntThinkAdapter berserk_fidget = new EntThinkAdapter() {
        public String getID() { return "berserk_fidget";}
        public boolean think(edict_t self) {
            if ((self.monsterinfo.aiflags & Defines.AI_STAND_GROUND) != 0)
                return true;

            if (Lib.random() > 0.15f)
                return true;

            self.monsterinfo.currentmove = berserk_move_stand_fidget;
            GameBase.gi.sound(self, Defines.CHAN_WEAPON, sound_idle, 1,
                    Defines.ATTN_IDLE, 0);
            return true;
        }
    };

    static mframe_t berserk_frames_stand[] = new mframe_t[] {
            new mframe_t(GameAI.ai_stand, 0, berserk_fidget),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null) };

    static mmove_t berserk_move_stand = new mmove_t(FRAME_stand1, FRAME_stand5,
            berserk_frames_stand, null);

    static EntThinkAdapter berserk_stand = new EntThinkAdapter() {
        public String getID() { return "berserk_stand";}
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = berserk_move_stand;
            return true;
        }
    };

    static mframe_t berserk_frames_stand_fidget[] = new mframe_t[] {
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null) };

    static mmove_t berserk_move_stand_fidget = new mmove_t(FRAME_standb1,
            FRAME_standb20, berserk_frames_stand_fidget, berserk_stand);

    static mframe_t berserk_frames_walk[] = new mframe_t[] {
            new mframe_t(GameAI.ai_walk, 9.1f, null),
            new mframe_t(GameAI.ai_walk, 6.3f, null),
            new mframe_t(GameAI.ai_walk, 4.9f, null),
            new mframe_t(GameAI.ai_walk, 6.7f, null),
            new mframe_t(GameAI.ai_walk, 6.0f, null),
            new mframe_t(GameAI.ai_walk, 8.2f, null),
            new mframe_t(GameAI.ai_walk, 7.2f, null),
            new mframe_t(GameAI.ai_walk, 6.1f, null),
            new mframe_t(GameAI.ai_walk, 4.9f, null),
            new mframe_t(GameAI.ai_walk, 4.7f, null),
            new mframe_t(GameAI.ai_walk, 4.7f, null),
            new mframe_t(GameAI.ai_walk, 4.8f, null) };

    static mmove_t berserk_move_walk = new mmove_t(FRAME_walkc1, FRAME_walkc11,
            berserk_frames_walk, null);

    static EntThinkAdapter berserk_walk = new EntThinkAdapter() {
        public String getID() { return "berserk_walk";}
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = berserk_move_walk;
            return true;
        }
    };

    /*
     * 
     * **************************** SKIPPED THIS FOR NOW!
     * ****************************
     * 
     * Running . Arm raised in air
     * 
     * void() berserk_runb1 =[ $r_att1 , berserk_runb2 ] {ai_run(21);}; void()
     * berserk_runb2 =[ $r_att2 , berserk_runb3 ] {ai_run(11);}; void()
     * berserk_runb3 =[ $r_att3 , berserk_runb4 ] {ai_run(21);}; void()
     * berserk_runb4 =[ $r_att4 , berserk_runb5 ] {ai_run(25);}; void()
     * berserk_runb5 =[ $r_att5 , berserk_runb6 ] {ai_run(18);}; void()
     * berserk_runb6 =[ $r_att6 , berserk_runb7 ] {ai_run(19);}; // running with
     * arm in air : start loop void() berserk_runb7 =[ $r_att7 , berserk_runb8 ]
     * {ai_run(21);}; void() berserk_runb8 =[ $r_att8 , berserk_runb9 ]
     * {ai_run(11);}; void() berserk_runb9 =[ $r_att9 , berserk_runb10 ]
     * {ai_run(21);}; void() berserk_runb10 =[ $r_att10 , berserk_runb11 ]
     * {ai_run(25);}; void() berserk_runb11 =[ $r_att11 , berserk_runb12 ]
     * {ai_run(18);}; void() berserk_runb12 =[ $r_att12 , berserk_runb7 ]
     * {ai_run(19);}; // running with arm in air : end loop
     */

    static mframe_t berserk_frames_run1[] = new mframe_t[] {
            new mframe_t(GameAI.ai_run, 21, null),
            new mframe_t(GameAI.ai_run, 11, null),
            new mframe_t(GameAI.ai_run, 21, null),
            new mframe_t(GameAI.ai_run, 25, null),
            new mframe_t(GameAI.ai_run, 18, null),
            new mframe_t(GameAI.ai_run, 19, null) };

    static mmove_t berserk_move_run1 = new mmove_t(FRAME_run1, FRAME_run6,
            berserk_frames_run1, null);

    static EntThinkAdapter berserk_run = new EntThinkAdapter() {
        public String getID() { return "berserk_run";}
        public boolean think(edict_t self) {
            if ((self.monsterinfo.aiflags & Defines.AI_STAND_GROUND) != 0)
                self.monsterinfo.currentmove = berserk_move_stand;
            else
                self.monsterinfo.currentmove = berserk_move_run1;
            return true;
        }
    };

    static EntThinkAdapter berserk_attack_spike = new EntThinkAdapter() {
        public String getID() { return "berserk_attack_spike";}
        public boolean think(edict_t self) {
            float[] aim = { Defines.MELEE_DISTANCE, 0f, -24f };

            GameWeapon.fire_hit(self, aim, (15 + (Lib.rand() % 6)), 400);
            //    Faster attack -- upwards and backwards

            return true;
        }
    };

    static EntThinkAdapter berserk_swing = new EntThinkAdapter() {
        public String getID() { return "berserk_swing";}
        public boolean think(edict_t self) {
            GameBase.gi.sound(self, Defines.CHAN_WEAPON, sound_punch, 1,
                    Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static mframe_t berserk_frames_attack_spike[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, berserk_swing),
            new mframe_t(GameAI.ai_charge, 0, berserk_attack_spike),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null) };

    static mmove_t berserk_move_attack_spike = new mmove_t(FRAME_att_c1,
            FRAME_att_c8, berserk_frames_attack_spike, berserk_run);

    static EntThinkAdapter berserk_attack_club = new EntThinkAdapter() {
        public String getID() { return "berserk_attack_club";}
        public boolean think(edict_t self) {
            float aim[] = { 0, 0, 0 };

            Math3D.VectorSet(aim, Defines.MELEE_DISTANCE, self.mins[0], -4);
            GameWeapon.fire_hit(self, aim, (5 + (Lib.rand() % 6)), 400); // Slower
                                                                   // attack

            return true;
        }
    };

    static mframe_t berserk_frames_attack_club[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, berserk_swing),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, berserk_attack_club),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null) };

    static mmove_t berserk_move_attack_club = new mmove_t(FRAME_att_c9,
            FRAME_att_c20, berserk_frames_attack_club, berserk_run);

    static EntThinkAdapter berserk_strike = new EntThinkAdapter() {
        public String getID() { return "berserk_strike";}
        public boolean think(edict_t self) {
            return true;
        }
    };

    static mframe_t berserk_frames_attack_strike[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, berserk_swing),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, berserk_strike),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 9.7f, null),
            new mframe_t(GameAI.ai_move, 13.6f, null) };

    static mmove_t berserk_move_attack_strike = new mmove_t(FRAME_att_c21,
            FRAME_att_c34, berserk_frames_attack_strike, berserk_run);

    static EntThinkAdapter berserk_melee = new EntThinkAdapter() {
        public String getID() { return "berserk_melee";}
        public boolean think(edict_t self) {
            if ((Lib.rand() % 2) == 0)
                self.monsterinfo.currentmove = berserk_move_attack_spike;
            else
                self.monsterinfo.currentmove = berserk_move_attack_club;
            return true;
        }
    };

    /*
     * void() berserk_atke1 =[ $r_attb1, berserk_atke2 ] {ai_run(9);}; void()
     * berserk_atke2 =[ $r_attb2, berserk_atke3 ] {ai_run(6);}; void()
     * berserk_atke3 =[ $r_attb3, berserk_atke4 ] {ai_run(18.4);}; void()
     * berserk_atke4 =[ $r_attb4, berserk_atke5 ] {ai_run(25);}; void()
     * berserk_atke5 =[ $r_attb5, berserk_atke6 ] {ai_run(14);}; void()
     * berserk_atke6 =[ $r_attb6, berserk_atke7 ] {ai_run(20);}; void()
     * berserk_atke7 =[ $r_attb7, berserk_atke8 ] {ai_run(8.5);}; void()
     * berserk_atke8 =[ $r_attb8, berserk_atke9 ] {ai_run(3);}; void()
     * berserk_atke9 =[ $r_attb9, berserk_atke10 ] {ai_run(17.5);}; void()
     * berserk_atke10 =[ $r_attb10, berserk_atke11 ] {ai_run(17);}; void()
     * berserk_atke11 =[ $r_attb11, berserk_atke12 ] {ai_run(9);}; void()
     * berserk_atke12 =[ $r_attb12, berserk_atke13 ] {ai_run(25);}; void()
     * berserk_atke13 =[ $r_attb13, berserk_atke14 ] {ai_run(3.7);}; void()
     * berserk_atke14 =[ $r_attb14, berserk_atke15 ] {ai_run(2.6);}; void()
     * berserk_atke15 =[ $r_attb15, berserk_atke16 ] {ai_run(19);}; void()
     * berserk_atke16 =[ $r_attb16, berserk_atke17 ] {ai_run(25);}; void()
     * berserk_atke17 =[ $r_attb17, berserk_atke18 ] {ai_run(19.6);}; void()
     * berserk_atke18 =[ $r_attb18, berserk_run1 ] {ai_run(7.8);};
     */

    static mframe_t berserk_frames_pain1[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t berserk_move_pain1 = new mmove_t(FRAME_painc1, FRAME_painc4,
            berserk_frames_pain1, berserk_run);

    static mframe_t berserk_frames_pain2[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t berserk_move_pain2 = new mmove_t(FRAME_painb1,
            FRAME_painb20, berserk_frames_pain2, berserk_run);

    static EntPainAdapter berserk_pain = new EntPainAdapter() {
        public String getID() { return "berserk_pain";}
        public void pain(edict_t self, edict_t other, float kick, int damage) {
            if (self.health < (self.max_health / 2))
                self.s.skinnum = 1;

            if (GameBase.level.time < self.pain_debounce_time)
                return;

            self.pain_debounce_time = GameBase.level.time + 3;
            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain, 1,
                    Defines.ATTN_NORM, 0);

            if (GameBase.skill.value == 3)
                return; // no pain anims in nightmare

            if ((damage < 20) || (Lib.random() < 0.5))
                self.monsterinfo.currentmove = berserk_move_pain1;
            else
                self.monsterinfo.currentmove = berserk_move_pain2;
        }
    };

    static EntThinkAdapter berserk_dead = new EntThinkAdapter() {
        public String getID() { return "berserk_dead";}
        public boolean think(edict_t self) {
            Math3D.VectorSet(self.mins, -16, -16, -24);
            Math3D.VectorSet(self.maxs, 16, 16, -8);
            self.movetype = Defines.MOVETYPE_TOSS;
            self.svflags |= Defines.SVF_DEADMONSTER;
            self.nextthink = 0;
            GameBase.gi.linkentity(self);
            return true;
        }
    };

    static mframe_t berserk_frames_death1[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t berserk_move_death1 = new mmove_t(FRAME_death1,
            FRAME_death13, berserk_frames_death1, berserk_dead);

    static mframe_t berserk_frames_death2[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t berserk_move_death2 = new mmove_t(FRAME_deathc1,
            FRAME_deathc8, berserk_frames_death2, berserk_dead);

    static EntDieAdapter berserk_die = new EntDieAdapter() {
        public String getID() { return "berserk_die";}
        public void die(edict_t self, edict_t inflictor, edict_t attacker,
                int damage, float point[]) {
            int n;

            if (self.health <= self.gib_health) {
                GameBase.gi
                        .sound(self, Defines.CHAN_VOICE, GameBase.gi
                                .soundindex("misc/udeath.wav"), 1,
                                Defines.ATTN_NORM, 0);
                for (n = 0; n < 2; n++)
                    GameMisc.ThrowGib(self, "models/objects/gibs/bone/tris.md2",
                            damage, Defines.GIB_ORGANIC);
                for (n = 0; n < 4; n++)
                    GameMisc.ThrowGib(self,
                            "models/objects/gibs/sm_meat/tris.md2", damage,
                            Defines.GIB_ORGANIC);
                GameMisc.ThrowHead(self, "models/objects/gibs/head2/tris.md2",
                        damage, Defines.GIB_ORGANIC);
                self.deadflag = Defines.DEAD_DEAD;
                return;
            }

            if (self.deadflag == Defines.DEAD_DEAD)
                return;

            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_die, 1,
                    Defines.ATTN_NORM, 0);
            self.deadflag = Defines.DEAD_DEAD;
            self.takedamage = Defines.DAMAGE_YES;

            if (damage >= 50)
                self.monsterinfo.currentmove = berserk_move_death1;
            else
                self.monsterinfo.currentmove = berserk_move_death2;
        }
    };

    /*
     * QUAKED monster_berserk (1 .5 0) (-16 -16 -24) (16 16 32) Ambush
     * Trigger_Spawn Sight
     */
    public static void SP_monster_berserk(edict_t self) {
        if (GameBase.deathmatch.value != 0) {
            GameUtil.G_FreeEdict(self);
            return;
        }

        // pre-caches
        sound_pain = GameBase.gi.soundindex("berserk/berpain2.wav");
        sound_die = GameBase.gi.soundindex("berserk/berdeth2.wav");
        sound_idle = GameBase.gi.soundindex("berserk/beridle1.wav");
        sound_punch = GameBase.gi.soundindex("berserk/attack.wav");
        sound_search = GameBase.gi.soundindex("berserk/bersrch1.wav");
        sound_sight = GameBase.gi.soundindex("berserk/sight.wav");

        self.s.modelindex = GameBase.gi
                .modelindex("models/monsters/berserk/tris.md2");
        Math3D.VectorSet(self.mins, -16, -16, -24);
        Math3D.VectorSet(self.maxs, 16, 16, 32);
        self.movetype = Defines.MOVETYPE_STEP;
        self.solid = Defines.SOLID_BBOX;

        self.health = 240;
        self.gib_health = -60;
        self.mass = 250;

        self.pain = berserk_pain;
        self.die = berserk_die;

        self.monsterinfo.stand = berserk_stand;
        self.monsterinfo.walk = berserk_walk;
        self.monsterinfo.run = berserk_run;
        self.monsterinfo.dodge = null;
        self.monsterinfo.attack = null;
        self.monsterinfo.melee = berserk_melee;
        self.monsterinfo.sight = berserk_sight;
        self.monsterinfo.search = berserk_search;

        self.monsterinfo.currentmove = berserk_move_stand;
        self.monsterinfo.scale = MODEL_SCALE;

        GameBase.gi.linkentity(self);

        GameAI.walkmonster_start.think(self);
    }
}