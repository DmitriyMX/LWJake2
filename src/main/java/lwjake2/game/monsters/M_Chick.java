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
import lwjake2.game.EntDodgeAdapter;
import lwjake2.game.EntInteractAdapter;
import lwjake2.game.EntPainAdapter;
import lwjake2.game.EntThinkAdapter;
import lwjake2.game.GameAI;
import lwjake2.game.GameBase;
import lwjake2.game.GameMisc;
import lwjake2.game.GameUtil;
import lwjake2.game.GameWeapon;
import lwjake2.game.Monster;
import lwjake2.game.edict_t;
import lwjake2.game.mframe_t;
import lwjake2.game.mmove_t;
import lwjake2.util.Lib;
import lwjake2.util.Math3D;

public class M_Chick {

    public final static int FRAME_attak101 = 0;

    public final static int FRAME_attak102 = 1;

    public final static int FRAME_attak103 = 2;

    public final static int FRAME_attak104 = 3;

    public final static int FRAME_attak105 = 4;

    public final static int FRAME_attak106 = 5;

    public final static int FRAME_attak107 = 6;

    public final static int FRAME_attak108 = 7;

    public final static int FRAME_attak113 = 12;

    public final static int FRAME_attak114 = 13;

    public final static int FRAME_attak127 = 26;

    public final static int FRAME_attak128 = 27;

    public final static int FRAME_attak132 = 31;

    public final static int FRAME_attak201 = 32;

    public final static int FRAME_attak203 = 34;

    public final static int FRAME_attak204 = 35;

    public final static int FRAME_attak212 = 43;

    public final static int FRAME_attak213 = 44;

    public final static int FRAME_attak216 = 47;

    public final static int FRAME_death101 = 48;

    public final static int FRAME_death106 = 53;

    public final static int FRAME_death112 = 59;

    public final static int FRAME_death201 = 60;

    public final static int FRAME_death223 = 82;

    public final static int FRAME_duck01 = 83;

    public final static int FRAME_duck07 = 89;

    public final static int FRAME_pain101 = 90;

    public final static int FRAME_pain102 = 91;

    public final static int FRAME_pain103 = 92;

    public final static int FRAME_pain104 = 93;

    public final static int FRAME_pain105 = 94;

    public final static int FRAME_pain201 = 95;

    public final static int FRAME_pain202 = 96;

    public final static int FRAME_pain203 = 97;

    public final static int FRAME_pain204 = 98;

    public final static int FRAME_pain205 = 99;

    public final static int FRAME_pain301 = 100;

    public final static int FRAME_pain302 = 101;

    public final static int FRAME_pain303 = 102;

    public final static int FRAME_pain304 = 103;

    public final static int FRAME_pain321 = 120;

    public final static int FRAME_stand101 = 121;

    public final static int FRAME_stand130 = 150;

    public final static int FRAME_stand201 = 151;

    public final static int FRAME_stand230 = 180;

    public final static int FRAME_walk01 = 181;

    public final static int FRAME_walk10 = 190;

    public final static int FRAME_walk11 = 191;

    public final static int FRAME_walk12 = 192;

    public final static int FRAME_walk20 = 200;

    public final static float MODEL_SCALE = 1.000000f;

    static int sound_missile_prelaunch;

    static int sound_missile_launch;

    static int sound_melee_swing;

    static int sound_melee_hit;

    static int sound_missile_reload;

    static int sound_death1;

    static int sound_death2;

    static int sound_fall_down;

    static int sound_idle1;

    static int sound_idle2;

    static int sound_pain1;

    static int sound_pain2;

    static int sound_pain3;

    static int sound_sight;

    static int sound_search;

    static EntThinkAdapter ChickMoan = new EntThinkAdapter() {
        public String getID() { return "ChickMoan"; }
        public boolean think(edict_t self) {
            if (Lib.random() < 0.5)
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_idle1, 1,
                        Defines.ATTN_IDLE, 0);
            else
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_idle2, 1,
                        Defines.ATTN_IDLE, 0);
            return true;
        }
    };

    static mframe_t chick_frames_fidget[] = new mframe_t[] {
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, ChickMoan),
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
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null) };

    static EntThinkAdapter chick_stand = new EntThinkAdapter() {
        public String getID() { return "chick_stand"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = chick_move_stand;
            return true;
        }
    };

    static mmove_t chick_move_fidget = new mmove_t(FRAME_stand201,
            FRAME_stand230, chick_frames_fidget, chick_stand);

    static EntThinkAdapter chick_fidget = new EntThinkAdapter() {
        public String getID() { return "chick_fidget"; }
        public boolean think(edict_t self) {
            if ((self.monsterinfo.aiflags & Defines.AI_STAND_GROUND) != 0)
                return true;
            if (Lib.random() <= 0.3)
                self.monsterinfo.currentmove = chick_move_fidget;
            return true;
        }
    };

    static mframe_t chick_frames_stand[] = new mframe_t[] {
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
            new mframe_t(GameAI.ai_stand, 0, chick_fidget), };

    static mmove_t chick_move_stand = new mmove_t(FRAME_stand101,
            FRAME_stand130, chick_frames_stand, null);

    static EntThinkAdapter chick_run = new EntThinkAdapter() {
        public String getID() { return "chick_run"; }
        public boolean think(edict_t self) {
            if ((self.monsterinfo.aiflags & Defines.AI_STAND_GROUND) != 0) {
                self.monsterinfo.currentmove = chick_move_stand;
                return true;
            }

            if (self.monsterinfo.currentmove == chick_move_walk
                    || self.monsterinfo.currentmove == chick_move_start_run) {
                self.monsterinfo.currentmove = chick_move_run;
            } else {
                self.monsterinfo.currentmove = chick_move_start_run;
            }
            return true;
        }
    };

    static mframe_t chick_frames_start_run[] = new mframe_t[] {
            new mframe_t(GameAI.ai_run, 1, null),
            new mframe_t(GameAI.ai_run, 0, null),
            new mframe_t(GameAI.ai_run, 0, null),
            new mframe_t(GameAI.ai_run, -1, null),
            new mframe_t(GameAI.ai_run, -1, null),
            new mframe_t(GameAI.ai_run, 0, null),
            new mframe_t(GameAI.ai_run, 1, null),
            new mframe_t(GameAI.ai_run, 3, null),
            new mframe_t(GameAI.ai_run, 6, null),
            new mframe_t(GameAI.ai_run, 3, null) };

    static mmove_t chick_move_start_run = new mmove_t(FRAME_walk01,
            FRAME_walk10, chick_frames_start_run, chick_run);

    static mframe_t chick_frames_run[] = new mframe_t[] {
            new mframe_t(GameAI.ai_run, 6, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 13, null),
            new mframe_t(GameAI.ai_run, 5, null),
            new mframe_t(GameAI.ai_run, 7, null),
            new mframe_t(GameAI.ai_run, 4, null),
            new mframe_t(GameAI.ai_run, 11, null),
            new mframe_t(GameAI.ai_run, 5, null),
            new mframe_t(GameAI.ai_run, 9, null),
            new mframe_t(GameAI.ai_run, 7, null) };

    static mmove_t chick_move_run = new mmove_t(FRAME_walk11, FRAME_walk20,
            chick_frames_run, null);

    static mframe_t chick_frames_walk[] = new mframe_t[] {
            new mframe_t(GameAI.ai_walk, 6, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 13, null),
            new mframe_t(GameAI.ai_walk, 5, null),
            new mframe_t(GameAI.ai_walk, 7, null),
            new mframe_t(GameAI.ai_walk, 4, null),
            new mframe_t(GameAI.ai_walk, 11, null),
            new mframe_t(GameAI.ai_walk, 5, null),
            new mframe_t(GameAI.ai_walk, 9, null),
            new mframe_t(GameAI.ai_walk, 7, null) };

    static mmove_t chick_move_walk = new mmove_t(FRAME_walk11, FRAME_walk20,
            chick_frames_walk, null);

    static EntThinkAdapter chick_walk = new EntThinkAdapter() {
        public String getID() { return "chick_walk"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = chick_move_walk;
            return true;
        }
    };

    static mframe_t chick_frames_pain1[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t chick_move_pain1 = new mmove_t(FRAME_pain101, FRAME_pain105,
            chick_frames_pain1, chick_run);

    static mframe_t chick_frames_pain2[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t chick_move_pain2 = new mmove_t(FRAME_pain201, FRAME_pain205,
            chick_frames_pain2, chick_run);

    static mframe_t chick_frames_pain3[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, -6, null),
            new mframe_t(GameAI.ai_move, 3, null),
            new mframe_t(GameAI.ai_move, 11, null),
            new mframe_t(GameAI.ai_move, 3, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 4, null),
            new mframe_t(GameAI.ai_move, 1, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, -3, null),
            new mframe_t(GameAI.ai_move, -4, null),
            new mframe_t(GameAI.ai_move, 5, null),
            new mframe_t(GameAI.ai_move, 7, null),
            new mframe_t(GameAI.ai_move, -2, null),
            new mframe_t(GameAI.ai_move, 3, null),
            new mframe_t(GameAI.ai_move, -5, null),
            new mframe_t(GameAI.ai_move, -2, null),
            new mframe_t(GameAI.ai_move, -8, null),
            new mframe_t(GameAI.ai_move, 2, null) };

    static mmove_t chick_move_pain3 = new mmove_t(FRAME_pain301, FRAME_pain321,
            chick_frames_pain3, chick_run);

    static EntPainAdapter chick_pain = new EntPainAdapter() {
        public String getID() { return "chick_pain"; }
        public void pain(edict_t self, edict_t other, float kick, int damage) {
            float r;

            if (self.health < (self.max_health / 2))
                self.s.skinnum = 1;

            if (GameBase.level.time < self.pain_debounce_time)
                return;

            self.pain_debounce_time = GameBase.level.time + 3;

            r = Lib.random();
            if (r < 0.33)
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain1, 1,
                        Defines.ATTN_NORM, 0);
            else if (r < 0.66)
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain2, 1,
                        Defines.ATTN_NORM, 0);
            else
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain3, 1,
                        Defines.ATTN_NORM, 0);

            if (GameBase.skill.value == 3)
                return; // no pain anims in nightmare

            if (damage <= 10)
                self.monsterinfo.currentmove = chick_move_pain1;
            else if (damage <= 25)
                self.monsterinfo.currentmove = chick_move_pain2;
            else
                self.monsterinfo.currentmove = chick_move_pain3;
            return;
        }
    };

    static EntThinkAdapter chick_dead = new EntThinkAdapter() {
        public String getID() { return "chick_dead"; }
        public boolean think(edict_t self) {
            Math3D.VectorSet(self.mins, -16, -16, 0);
            Math3D.VectorSet(self.maxs, 16, 16, 16);
            self.movetype = Defines.MOVETYPE_TOSS;
            self.svflags |= Defines.SVF_DEADMONSTER;
            self.nextthink = 0;
            GameBase.gi.linkentity(self);
            return true;
        }
    };

    static mframe_t chick_frames_death2[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, -6, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, -1, null),
            new mframe_t(GameAI.ai_move, -5, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, -1, null),
            new mframe_t(GameAI.ai_move, -2, null),
            new mframe_t(GameAI.ai_move, 1, null),
            new mframe_t(GameAI.ai_move, 10, null),
            new mframe_t(GameAI.ai_move, 2, null),
            new mframe_t(GameAI.ai_move, 3, null),
            new mframe_t(GameAI.ai_move, 1, null),
            new mframe_t(GameAI.ai_move, 2, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 3, null),
            new mframe_t(GameAI.ai_move, 3, null),
            new mframe_t(GameAI.ai_move, 1, null),
            new mframe_t(GameAI.ai_move, -3, null),
            new mframe_t(GameAI.ai_move, -5, null),
            new mframe_t(GameAI.ai_move, 4, null),
            new mframe_t(GameAI.ai_move, 15, null),
            new mframe_t(GameAI.ai_move, 14, null),
            new mframe_t(GameAI.ai_move, 1, null) };

    static mmove_t chick_move_death2 = new mmove_t(FRAME_death201,
            FRAME_death223, chick_frames_death2, chick_dead);

    static mframe_t chick_frames_death1[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, -7, null),
            new mframe_t(GameAI.ai_move, 4, null),
            new mframe_t(GameAI.ai_move, 11, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t chick_move_death1 = new mmove_t(FRAME_death101,
            FRAME_death112, chick_frames_death1, chick_dead);

    static EntDieAdapter chick_die = new EntDieAdapter() {
        public String getID() { return "chick_die"; }

        public void die(edict_t self, edict_t inflictor, edict_t attacker,
                int damage, float[] point) {
            int n;

            //           check for gib
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

            //           regular death
            self.deadflag = Defines.DEAD_DEAD;
            self.takedamage = Defines.DAMAGE_YES;

            n = Lib.rand() % 2;
            if (n == 0) {
                self.monsterinfo.currentmove = chick_move_death1;
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_death1, 1,
                        Defines.ATTN_NORM, 0);
            } else {
                self.monsterinfo.currentmove = chick_move_death2;
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_death2, 1,
                        Defines.ATTN_NORM, 0);
            }
        }

    };

    static EntThinkAdapter chick_duck_down = new EntThinkAdapter() {
        public String getID() { return "chick_duck_down"; }
        public boolean think(edict_t self) {
            if ((self.monsterinfo.aiflags & Defines.AI_DUCKED) != 0)
                return true;
            self.monsterinfo.aiflags |= Defines.AI_DUCKED;
            self.maxs[2] -= 32;
            self.takedamage = Defines.DAMAGE_YES;
            self.monsterinfo.pausetime = GameBase.level.time + 1;
            GameBase.gi.linkentity(self);
            return true;
        }
    };

    static EntThinkAdapter chick_duck_hold = new EntThinkAdapter() {
        public String getID() { return "chick_duck_hold"; }
        public boolean think(edict_t self) {
            if (GameBase.level.time >= self.monsterinfo.pausetime)
                self.monsterinfo.aiflags &= ~Defines.AI_HOLD_FRAME;
            else
                self.monsterinfo.aiflags |= Defines.AI_HOLD_FRAME;
            return true;
        }
    };

    static EntThinkAdapter chick_duck_up = new EntThinkAdapter() {
        public String getID() { return "chick_duck_up"; }
        public boolean think(edict_t self) {
            self.monsterinfo.aiflags &= ~Defines.AI_DUCKED;
            self.maxs[2] += 32;
            self.takedamage = Defines.DAMAGE_AIM;
            GameBase.gi.linkentity(self);
            return true;
        }
    };

    static mframe_t chick_frames_duck[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, chick_duck_down),
            new mframe_t(GameAI.ai_move, 1, null),
            new mframe_t(GameAI.ai_move, 4, chick_duck_hold),
            new mframe_t(GameAI.ai_move, -4, null),
            new mframe_t(GameAI.ai_move, -5, chick_duck_up),
            new mframe_t(GameAI.ai_move, 3, null),
            new mframe_t(GameAI.ai_move, 1, null) };

    static mmove_t chick_move_duck = new mmove_t(FRAME_duck01, FRAME_duck07,
            chick_frames_duck, chick_run);

    static EntDodgeAdapter chick_dodge = new EntDodgeAdapter() {
        public String getID() { return "chick_dodge"; }
        public void dodge(edict_t self, edict_t attacker, float eta) {
            if (Lib.random() > 0.25)
                return;

            if (self.enemy != null)
                self.enemy = attacker;

            self.monsterinfo.currentmove = chick_move_duck;
            return;
        }
    };

    static EntThinkAdapter ChickSlash = new EntThinkAdapter() {
        public String getID() { return "ChickSlash"; }
        public boolean think(edict_t self) {
            float[] aim = { 0, 0, 0 };

            Math3D.VectorSet(aim, Defines.MELEE_DISTANCE, self.mins[0], 10);
            GameBase.gi.sound(self, Defines.CHAN_WEAPON, sound_melee_swing, 1,
                    Defines.ATTN_NORM, 0);
            GameWeapon.fire_hit(self, aim, (10 + (Lib.rand() % 6)), 100);
            return true;
        }
    };

    static EntThinkAdapter ChickRocket = new EntThinkAdapter() {
        public String getID() { return "ChickRocket"; }
        public boolean think(edict_t self) {
            float[] forward = { 0, 0, 0 }, right = { 0, 0, 0 };
            float[] start = { 0, 0, 0 };
            float[] dir = { 0, 0, 0 };
            float[] vec = { 0, 0, 0 };

            Math3D.AngleVectors(self.s.angles, forward, right, null);
            Math3D.G_ProjectSource(self.s.origin,
                    M_Flash.monster_flash_offset[Defines.MZ2_CHICK_ROCKET_1],
                    forward, right, start);

            Math3D.VectorCopy(self.enemy.s.origin, vec);
            vec[2] += self.enemy.viewheight;
            Math3D.VectorSubtract(vec, start, dir);
            Math3D.VectorNormalize(dir);

            Monster.monster_fire_rocket(self, start, dir, 50, 500,
                    Defines.MZ2_CHICK_ROCKET_1);
            return true;
        }
    };

    static EntThinkAdapter Chick_PreAttack1 = new EntThinkAdapter() {
        public String getID() { return "Chick_PreAttack1"; }
        public boolean think(edict_t self) {
            GameBase.gi.sound(self, Defines.CHAN_VOICE,
                    sound_missile_prelaunch, 1, Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static EntThinkAdapter ChickReload = new EntThinkAdapter() {
        public String getID() { return "ChickReload"; }
        public boolean think(edict_t self) {
            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_missile_reload,
                    1, Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static EntThinkAdapter chick_attack1 = new EntThinkAdapter() {
        public String getID() { return "chick_attack1"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = chick_move_attack1;
            return true;
        }
    };

    static EntThinkAdapter chick_rerocket = new EntThinkAdapter() {
        public String getID() { return "chick_rerocket"; }
        public boolean think(edict_t self) {
            if (self.enemy.health > 0) {
                if (GameUtil.range(self, self.enemy) > Defines.RANGE_MELEE)
                    if (GameUtil.visible(self, self.enemy))
                        if (Lib.random() <= 0.6) {
                            self.monsterinfo.currentmove = chick_move_attack1;
                            return true;
                        }
            }
            self.monsterinfo.currentmove = chick_move_end_attack1;
            return true;
        }
    };

    static mframe_t chick_frames_start_attack1[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 0, Chick_PreAttack1),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 4, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, -3, null),
            new mframe_t(GameAI.ai_charge, 3, null),
            new mframe_t(GameAI.ai_charge, 5, null),
            new mframe_t(GameAI.ai_charge, 7, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, chick_attack1) };

    static mmove_t chick_move_start_attack1 = new mmove_t(FRAME_attak101,
            FRAME_attak113, chick_frames_start_attack1, null);

    static mframe_t chick_frames_attack1[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 19, ChickRocket),
            new mframe_t(GameAI.ai_charge, -6, null),
            new mframe_t(GameAI.ai_charge, -5, null),
            new mframe_t(GameAI.ai_charge, -2, null),
            new mframe_t(GameAI.ai_charge, -7, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 10, ChickReload),
            new mframe_t(GameAI.ai_charge, 4, null),
            new mframe_t(GameAI.ai_charge, 5, null),
            new mframe_t(GameAI.ai_charge, 6, null),
            new mframe_t(GameAI.ai_charge, 6, null),
            new mframe_t(GameAI.ai_charge, 4, null),
            new mframe_t(GameAI.ai_charge, 3, chick_rerocket) };

    static mmove_t chick_move_attack1 = new mmove_t(FRAME_attak114,
            FRAME_attak127, chick_frames_attack1, null);

    static mframe_t chick_frames_end_attack1[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, -3, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, -6, null),
            new mframe_t(GameAI.ai_charge, -4, null),
            new mframe_t(GameAI.ai_charge, -2, null) };

    static mmove_t chick_move_end_attack1 = new mmove_t(FRAME_attak128,
            FRAME_attak132, chick_frames_end_attack1, chick_run);

    static EntThinkAdapter chick_reslash = new EntThinkAdapter() {
        public String getID() { return "chick_reslash"; }
        public boolean think(edict_t self) {
            if (self.enemy.health > 0) {
                if (GameUtil.range(self, self.enemy) == Defines.RANGE_MELEE)
                    if (Lib.random() <= 0.9) {
                        self.monsterinfo.currentmove = chick_move_slash;
                        return true;
                    } else {
                        self.monsterinfo.currentmove = chick_move_end_slash;
                        return true;
                    }
            }
            self.monsterinfo.currentmove = chick_move_end_slash;
            return true;
        }
    };

    static mframe_t chick_frames_slash[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 7, ChickSlash),
            new mframe_t(GameAI.ai_charge, -7, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, -1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, -2, chick_reslash) };

    static mmove_t chick_move_slash = new mmove_t(FRAME_attak204,
            FRAME_attak212, chick_frames_slash, null);

    static mframe_t chick_frames_end_slash[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, -6, null),
            new mframe_t(GameAI.ai_charge, -1, null),
            new mframe_t(GameAI.ai_charge, -6, null),
            new mframe_t(GameAI.ai_charge, 0, null) };

    static mmove_t chick_move_end_slash = new mmove_t(FRAME_attak213,
            FRAME_attak216, chick_frames_end_slash, chick_run);

    static EntThinkAdapter chick_slash = new EntThinkAdapter() {
        public String getID() { return "chick_slash"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = chick_move_slash;
            return true;
        }
    };

    static mframe_t chick_frames_start_slash[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 8, null),
            new mframe_t(GameAI.ai_charge, 3, null) };

    static mmove_t chick_move_start_slash = new mmove_t(FRAME_attak201,
            FRAME_attak203, chick_frames_start_slash, chick_slash);

    static EntThinkAdapter chick_melee = new EntThinkAdapter() {
        public String getID() { return "chick_melee"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = chick_move_start_slash;
            return true;
        }
    };

    static EntThinkAdapter chick_attack = new EntThinkAdapter() {
        public String getID() { return "chick_attack"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = chick_move_start_attack1;
            return true;
        }
    };

    static EntInteractAdapter chick_sight = new EntInteractAdapter() {
        public String getID() { return "chick_sight"; }
        public boolean interact(edict_t self, edict_t other) {
            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_sight, 1,
                    Defines.ATTN_NORM, 0);
            return true;
        }
    };

    /*
     * QUAKED monster_chick (1 .5 0) (-16 -16 -24) (16 16 32) Ambush
     * Trigger_Spawn Sight
     */
    public static void SP_monster_chick(edict_t self) {
        if (GameBase.deathmatch.value != 0) {
            GameUtil.G_FreeEdict(self);
            return;
        }

        sound_missile_prelaunch = GameBase.gi.soundindex("chick/chkatck1.wav");
        sound_missile_launch = GameBase.gi.soundindex("chick/chkatck2.wav");
        sound_melee_swing = GameBase.gi.soundindex("chick/chkatck3.wav");
        sound_melee_hit = GameBase.gi.soundindex("chick/chkatck4.wav");
        sound_missile_reload = GameBase.gi.soundindex("chick/chkatck5.wav");
        sound_death1 = GameBase.gi.soundindex("chick/chkdeth1.wav");
        sound_death2 = GameBase.gi.soundindex("chick/chkdeth2.wav");
        sound_fall_down = GameBase.gi.soundindex("chick/chkfall1.wav");
        sound_idle1 = GameBase.gi.soundindex("chick/chkidle1.wav");
        sound_idle2 = GameBase.gi.soundindex("chick/chkidle2.wav");
        sound_pain1 = GameBase.gi.soundindex("chick/chkpain1.wav");
        sound_pain2 = GameBase.gi.soundindex("chick/chkpain2.wav");
        sound_pain3 = GameBase.gi.soundindex("chick/chkpain3.wav");
        sound_sight = GameBase.gi.soundindex("chick/chksght1.wav");
        sound_search = GameBase.gi.soundindex("chick/chksrch1.wav");

        self.movetype = Defines.MOVETYPE_STEP;
        self.solid = Defines.SOLID_BBOX;
        self.s.modelindex = GameBase.gi
                .modelindex("models/monsters/bitch/tris.md2");
        Math3D.VectorSet(self.mins, -16, -16, 0);
        Math3D.VectorSet(self.maxs, 16, 16, 56);

        self.health = 175;
        self.gib_health = -70;
        self.mass = 200;

        self.pain = chick_pain;
        self.die = chick_die;

        self.monsterinfo.stand = chick_stand;
        self.monsterinfo.walk = chick_walk;
        self.monsterinfo.run = chick_run;
        self.monsterinfo.dodge = chick_dodge;
        self.monsterinfo.attack = chick_attack;
        self.monsterinfo.melee = chick_melee;
        self.monsterinfo.sight = chick_sight;

        GameBase.gi.linkentity(self);

        self.monsterinfo.currentmove = chick_move_stand;
        self.monsterinfo.scale = MODEL_SCALE;

        GameAI.walkmonster_start.think(self);
    }
}