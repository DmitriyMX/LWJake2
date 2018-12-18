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
import lwjake2.game.EntPainAdapter;
import lwjake2.game.EntThinkAdapter;
import lwjake2.game.GameAI;
import lwjake2.game.GameBase;
import lwjake2.game.GameUtil;
import lwjake2.game.Monster;
import lwjake2.game.edict_t;
import lwjake2.game.mframe_t;
import lwjake2.game.mmove_t;
import lwjake2.game.trace_t;
import lwjake2.util.Lib;
import lwjake2.util.Math3D;

public class M_Boss2 {

    public final static int FRAME_stand30 = 0;

    public final static int FRAME_stand50 = 20;

    public final static int FRAME_stand1 = 21;

    public final static int FRAME_stand10 = 30;

    public final static int FRAME_stand11 = 31;

    public final static int FRAME_stand12 = 32;

    public final static int FRAME_stand13 = 33;

    public final static int FRAME_stand14 = 34;

    public final static int FRAME_stand15 = 35;

    public final static int FRAME_stand16 = 36;

    public final static int FRAME_stand17 = 37;

    public final static int FRAME_stand18 = 38;

    public final static int FRAME_stand19 = 39;

    public final static int FRAME_stand20 = 40;

    public final static int FRAME_stand21 = 41;

    public final static int FRAME_stand22 = 42;

    public final static int FRAME_stand23 = 43;

    public final static int FRAME_stand24 = 44;

    public final static int FRAME_stand25 = 45;

    public final static int FRAME_stand26 = 46;

    public final static int FRAME_stand27 = 47;

    public final static int FRAME_stand28 = 48;

    public final static int FRAME_stand29 = 49;

    public final static int FRAME_walk1 = 50;

    public final static int FRAME_walk10 = 59;

    public final static int FRAME_walk11 = 60;

    public final static int FRAME_walk12 = 61;

    public final static int FRAME_walk20 = 69;

    public final static int FRAME_attack1 = 70;

    public final static int FRAME_attack9 = 78;

    public final static int FRAME_attack10 = 79;

    public final static int FRAME_attack15 = 84;

    public final static int FRAME_attack16 = 85;

    public final static int FRAME_attack19 = 88;

    public final static int FRAME_attack20 = 89;

    public final static int FRAME_attack40 = 109;

    public final static int FRAME_pain2 = 110;

    public final static int FRAME_pain19 = 127;

    public final static int FRAME_pain20 = 128;

    public final static int FRAME_pain23 = 131;

    public final static int FRAME_death2 = 132;

    public final static int FRAME_death50 = 180;

    public final static float MODEL_SCALE = 1.000000f;

    static int sound_pain1;

    static int sound_pain2;

    static int sound_pain3;

    static int sound_death;

    static int sound_search1;

    static EntThinkAdapter boss2_stand = new EntThinkAdapter() {
        public String getID() { return "boss2_stand"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = boss2_move_stand;
            return true;
        }
    };

    static EntThinkAdapter boss2_run = new EntThinkAdapter() {
        public String getID() { return "boss2_run"; }
        public boolean think(edict_t self) {
            if ((self.monsterinfo.aiflags & Defines.AI_STAND_GROUND) != 0)
                self.monsterinfo.currentmove = boss2_move_stand;
            else
                self.monsterinfo.currentmove = boss2_move_run;
            return true;
        }
    };

    static EntThinkAdapter boss2_walk = new EntThinkAdapter() {
        public String getID() { return "boss2_walk"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = boss2_move_stand;

            self.monsterinfo.currentmove = boss2_move_walk;
            return true;
        }
    };

    static EntThinkAdapter boss2_attack = new EntThinkAdapter() {
        public String getID() { return "boss2_attack"; }
        public boolean think(edict_t self) {
            float[] vec = { 0, 0, 0 };

            float range;

            Math3D.VectorSubtract(self.enemy.s.origin, self.s.origin, vec);
            range = Math3D.VectorLength(vec);

            if (range <= 125) {
                self.monsterinfo.currentmove = boss2_move_attack_pre_mg;
            } else {
                if (Lib.random() <= 0.6)
                    self.monsterinfo.currentmove = boss2_move_attack_pre_mg;
                else
                    self.monsterinfo.currentmove = boss2_move_attack_rocket;
            }
            return true;
        }
    };

    static EntThinkAdapter boss2_attack_mg = new EntThinkAdapter() {
        public String getID() { return "boss2_attack_mg"; }
        public boolean think(edict_t self) {
            self.monsterinfo.currentmove = boss2_move_attack_mg;
            return true;
        }
    };

    static EntThinkAdapter boss2_reattack_mg = new EntThinkAdapter() {
        public String getID() { return "boss2_reattack_mg"; }
        public boolean think(edict_t self) {
            if (GameUtil.infront(self, self.enemy))
                if (Lib.random() <= 0.7)
                    self.monsterinfo.currentmove = boss2_move_attack_mg;
                else
                    self.monsterinfo.currentmove = boss2_move_attack_post_mg;
            else
                self.monsterinfo.currentmove = boss2_move_attack_post_mg;
            return true;
        }
    };

    static EntPainAdapter boss2_pain = new EntPainAdapter() {
        public String getID() { return "boss2_pain"; }
        public void pain(edict_t self, edict_t other, float kick, int damage) {
            if (self.health < (self.max_health / 2))
                self.s.skinnum = 1;

            if (GameBase.level.time < self.pain_debounce_time)
                return;

            self.pain_debounce_time = GameBase.level.time + 3;
            //       American wanted these at no attenuation
            if (damage < 10) {
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain3, 1,
                        Defines.ATTN_NONE, 0);
                self.monsterinfo.currentmove = boss2_move_pain_light;
            } else if (damage < 30) {
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain1, 1,
                        Defines.ATTN_NONE, 0);
                self.monsterinfo.currentmove = boss2_move_pain_light;
            } else {
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain2, 1,
                        Defines.ATTN_NONE, 0);
                self.monsterinfo.currentmove = boss2_move_pain_heavy;
            }
        }
    };

    static EntThinkAdapter boss2_dead = new EntThinkAdapter() {
        public String getID() { return "boss2_dead"; }
        public boolean think(edict_t self) {
            Math3D.VectorSet(self.mins, -56, -56, 0);
            Math3D.VectorSet(self.maxs, 56, 56, 80);
            self.movetype = Defines.MOVETYPE_TOSS;
            self.svflags |= Defines.SVF_DEADMONSTER;
            self.nextthink = 0;
            GameBase.gi.linkentity(self);
            return true;
        }
    };

    static EntDieAdapter boss2_die = new EntDieAdapter() {
        public String getID() { return "boss2_die"; }
        public void die(edict_t self, edict_t inflictor, edict_t attacker,
                int damage, float[] point) {
            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_death, 1,
                    Defines.ATTN_NONE, 0);
            self.deadflag = Defines.DEAD_DEAD;
            self.takedamage = Defines.DAMAGE_NO;
            self.count = 0;
            self.monsterinfo.currentmove = boss2_move_death;

        }
    };

    static EntThinkAdapter Boss2_CheckAttack = new EntThinkAdapter() {
        public String getID() { return "Boss2_CheckAttack"; }
        public boolean think(edict_t self) {
            float[] spot1 = { 0, 0, 0 }, spot2 = { 0, 0, 0 };
            float[] temp = { 0, 0, 0 };
            float chance;
            trace_t tr;
            int enemy_range;
            float enemy_yaw;

            if (self.enemy.health > 0) {
                // see if any entities are in the way of the shot
                Math3D.VectorCopy(self.s.origin, spot1);
                spot1[2] += self.viewheight;
                Math3D.VectorCopy(self.enemy.s.origin, spot2);
                spot2[2] += self.enemy.viewheight;

                tr = GameBase.gi.trace(spot1, null, null, spot2, self,
                        Defines.CONTENTS_SOLID | Defines.CONTENTS_MONSTER
                                | Defines.CONTENTS_SLIME
                                | Defines.CONTENTS_LAVA);

                // do we have a clear shot?
                if (tr.ent != self.enemy)
                    return false;
            }

            enemy_range = GameUtil.range(self, self.enemy);
            Math3D.VectorSubtract(self.enemy.s.origin, self.s.origin, temp);
            enemy_yaw = Math3D.vectoyaw(temp);

            self.ideal_yaw = enemy_yaw;

            // melee attack
            if (enemy_range == Defines.RANGE_MELEE) {
                if (self.monsterinfo.melee != null)
                    self.monsterinfo.attack_state = Defines.AS_MELEE;
                else
                    self.monsterinfo.attack_state = Defines.AS_MISSILE;
                return true;
            }

            //       missile attack
            if (self.monsterinfo.attack == null)
                return false;

            if (GameBase.level.time < self.monsterinfo.attack_finished)
                return false;

            if (enemy_range == Defines.RANGE_FAR)
                return false;

            if ((self.monsterinfo.aiflags & Defines.AI_STAND_GROUND) != 0) {
                chance = 0.4f;
            } else if (enemy_range == Defines.RANGE_NEAR) {
                chance = 0.8f;
            } else if (enemy_range == Defines.RANGE_MID) {
                chance = 0.8f;
            } else {
                return false;
            }

            if (Lib.random() < chance) {
                self.monsterinfo.attack_state = Defines.AS_MISSILE;
                self.monsterinfo.attack_finished = GameBase.level.time + 2
                        * Lib.random();
                return true;
            }

            if ((self.flags & Defines.FL_FLY) != 0) {
                if (Lib.random() < 0.3)
                    self.monsterinfo.attack_state = Defines.AS_SLIDING;
                else
                    self.monsterinfo.attack_state = Defines.AS_STRAIGHT;
            }

            return false;
        }
    };

    static EntThinkAdapter boss2_search = new EntThinkAdapter() {
        public String getID() { return "boss2_search"; }
        public boolean think(edict_t self) {
            if (Lib.random() < 0.5)
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_search1, 1,
                        Defines.ATTN_NONE, 0);
            return true;
        }
    };

    static EntThinkAdapter Boss2Rocket = new EntThinkAdapter() {
        public String getID() { return "Boss2Rocket"; }
        public boolean think(edict_t self) {
            float[] forward = { 0, 0, 0 }, right = { 0, 0, 0 };
            float[] start = { 0, 0, 0 };
            float[] dir = { 0, 0, 0 };
            float[] vec = { 0, 0, 0 };

            Math3D.AngleVectors(self.s.angles, forward, right, null);

            //      1
            Math3D.G_ProjectSource(self.s.origin,
                    M_Flash.monster_flash_offset[Defines.MZ2_BOSS2_ROCKET_1],
                    forward, right, start);
            Math3D.VectorCopy(self.enemy.s.origin, vec);
            vec[2] += self.enemy.viewheight;
            Math3D.VectorSubtract(vec, start, dir);
            Math3D.VectorNormalize(dir);
            Monster.monster_fire_rocket(self, start, dir, 50, 500,
                    Defines.MZ2_BOSS2_ROCKET_1);

            //      2
            Math3D.G_ProjectSource(self.s.origin,
                    M_Flash.monster_flash_offset[Defines.MZ2_BOSS2_ROCKET_2],
                    forward, right, start);
            Math3D.VectorCopy(self.enemy.s.origin, vec);
            vec[2] += self.enemy.viewheight;
            Math3D.VectorSubtract(vec, start, dir);
            Math3D.VectorNormalize(dir);
            Monster.monster_fire_rocket(self, start, dir, 50, 500,
                    Defines.MZ2_BOSS2_ROCKET_2);

            //      3
            Math3D.G_ProjectSource(self.s.origin,
                    M_Flash.monster_flash_offset[Defines.MZ2_BOSS2_ROCKET_3],
                    forward, right, start);
            Math3D.VectorCopy(self.enemy.s.origin, vec);
            vec[2] += self.enemy.viewheight;
            Math3D.VectorSubtract(vec, start, dir);
            Math3D.VectorNormalize(dir);
            Monster.monster_fire_rocket(self, start, dir, 50, 500,
                    Defines.MZ2_BOSS2_ROCKET_3);

            //      4
            Math3D.G_ProjectSource(self.s.origin,
                    M_Flash.monster_flash_offset[Defines.MZ2_BOSS2_ROCKET_4],
                    forward, right, start);
            Math3D.VectorCopy(self.enemy.s.origin, vec);
            vec[2] += self.enemy.viewheight;
            Math3D.VectorSubtract(vec, start, dir);
            Math3D.VectorNormalize(dir);
            Monster.monster_fire_rocket(self, start, dir, 50, 500,
                    Defines.MZ2_BOSS2_ROCKET_4);
            return true;
        }
    };

    static EntThinkAdapter boss2_firebullet_right = new EntThinkAdapter() {
        public String getID() { return "boss2_firebullet_right"; }
        public boolean think(edict_t self) {
            float[] forward = { 0, 0, 0 }, right = { 0, 0, 0 }, target = { 0,
                    0, 0 };
            float[] start = { 0, 0, 0 };

            Math3D.AngleVectors(self.s.angles, forward, right, null);
            Math3D
                    .G_ProjectSource(
                            self.s.origin,
                            M_Flash.monster_flash_offset[Defines.MZ2_BOSS2_MACHINEGUN_R1],
                            forward, right, start);

            Math3D.VectorMA(self.enemy.s.origin, -0.2f, self.enemy.velocity,
                    target);
            target[2] += self.enemy.viewheight;
            Math3D.VectorSubtract(target, start, forward);
            Math3D.VectorNormalize(forward);

            Monster.monster_fire_bullet(self, start, forward, 6, 4,
                    Defines.DEFAULT_BULLET_HSPREAD,
                    Defines.DEFAULT_BULLET_VSPREAD,
                    Defines.MZ2_BOSS2_MACHINEGUN_R1);

            return true;
        }
    };

    static EntThinkAdapter boss2_firebullet_left = new EntThinkAdapter() {
        public String getID() { return "boss2_firebullet_left"; }
        public boolean think(edict_t self) {
            float[] forward = { 0, 0, 0 }, right = { 0, 0, 0 }, target = { 0,
                    0, 0 };
            float[] start = { 0, 0, 0 };

            Math3D.AngleVectors(self.s.angles, forward, right, null);
            Math3D
                    .G_ProjectSource(
                            self.s.origin,
                            M_Flash.monster_flash_offset[Defines.MZ2_BOSS2_MACHINEGUN_L1],
                            forward, right, start);

            Math3D.VectorMA(self.enemy.s.origin, -0.2f, self.enemy.velocity,
                    target);

            target[2] += self.enemy.viewheight;
            Math3D.VectorSubtract(target, start, forward);
            Math3D.VectorNormalize(forward);

            Monster.monster_fire_bullet(self, start, forward, 6, 4,
                    Defines.DEFAULT_BULLET_HSPREAD,
                    Defines.DEFAULT_BULLET_VSPREAD,
                    Defines.MZ2_BOSS2_MACHINEGUN_L1);

            return true;
        }
    };

    static EntThinkAdapter Boss2MachineGun = new EntThinkAdapter() {
        public String getID() { return "Boss2MachineGun"; }
        public boolean think(edict_t self) {
            /*
             * RST: this was disabled ! float[] forward={0,0,0}, right={0,0,0};
             * float[] start={0,0,0}; float[] dir={0,0,0}; float[] vec={0,0,0};
             * int flash_number;
             * 
             * AngleVectors (self.s.angles, forward, right, null);
             * 
             * flash_number = MZ2_BOSS2_MACHINEGUN_1 + (self.s.frame -
             * FRAME_attack10); G_ProjectSource (self.s.origin,
             * monster_flash_offset[flash_number], forward, right, start);
             * 
             * VectorCopy (self.enemy.s.origin, vec); vec[2] +=
             * self.enemy.viewheight; VectorSubtract (vec, start, dir);
             * VectorNormalize (dir); monster_fire_bullet (self, start, dir, 3,
             * 4, DEFAULT_BULLET_HSPREAD, DEFAULT_BULLET_VSPREAD, flash_number);
             */
            boss2_firebullet_left.think(self);
            boss2_firebullet_right.think(self);
            return true;
        }
    };

    static mframe_t boss2_frames_stand[] = new mframe_t[] {
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

    static mmove_t boss2_move_stand = new mmove_t(FRAME_stand30, FRAME_stand50,
            boss2_frames_stand, null);

    static mframe_t boss2_frames_fidget[] = new mframe_t[] {
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
            new mframe_t(GameAI.ai_stand, 0, null) };

    static mmove_t boss2_move_fidget = new mmove_t(FRAME_stand1, FRAME_stand30,
            boss2_frames_fidget, null);

    static mframe_t boss2_frames_walk[] = new mframe_t[] {
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 8, null) };

    static mmove_t boss2_move_walk = new mmove_t(FRAME_walk1, FRAME_walk20,
            boss2_frames_walk, null);

    static mframe_t boss2_frames_run[] = new mframe_t[] {
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null),
            new mframe_t(GameAI.ai_run, 8, null) };

    static mmove_t boss2_move_run = new mmove_t(FRAME_walk1, FRAME_walk20,
            boss2_frames_run, null);

    static mframe_t boss2_frames_attack_pre_mg[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, boss2_attack_mg) };

    static mmove_t boss2_move_attack_pre_mg = new mmove_t(FRAME_attack1,
            FRAME_attack9, boss2_frames_attack_pre_mg, null);

    //       Loop this
    static mframe_t boss2_frames_attack_mg[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 1, Boss2MachineGun),
            new mframe_t(GameAI.ai_charge, 1, Boss2MachineGun),
            new mframe_t(GameAI.ai_charge, 1, Boss2MachineGun),
            new mframe_t(GameAI.ai_charge, 1, Boss2MachineGun),
            new mframe_t(GameAI.ai_charge, 1, Boss2MachineGun),
            new mframe_t(GameAI.ai_charge, 1, boss2_reattack_mg) };

    static mmove_t boss2_move_attack_mg = new mmove_t(FRAME_attack10,
            FRAME_attack15, boss2_frames_attack_mg, null);

    static mframe_t boss2_frames_attack_post_mg[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null) };

    static mmove_t boss2_move_attack_post_mg = new mmove_t(FRAME_attack16,
            FRAME_attack19, boss2_frames_attack_post_mg, boss2_run);

    static mframe_t boss2_frames_attack_rocket[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_move, -20, Boss2Rocket),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null),
            new mframe_t(GameAI.ai_charge, 1, null) };

    static mmove_t boss2_move_attack_rocket = new mmove_t(FRAME_attack20,
            FRAME_attack40, boss2_frames_attack_rocket, boss2_run);

    static mframe_t boss2_frames_pain_heavy[] = new mframe_t[] {
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

    static mmove_t boss2_move_pain_heavy = new mmove_t(FRAME_pain2,
            FRAME_pain19, boss2_frames_pain_heavy, boss2_run);

    static mframe_t boss2_frames_pain_light[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t boss2_move_pain_light = new mmove_t(FRAME_pain20,
            FRAME_pain23, boss2_frames_pain_light, boss2_run);

    static mframe_t boss2_frames_death[] = new mframe_t[] {
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
            new mframe_t(GameAI.ai_move, 0, M_Supertank.BossExplode) };

    /*
     * static EntThinkAdapter xxx = new EntThinkAdapter() { public boolean
     * think(edict_t self) { return true; } };
     */

    static mmove_t boss2_move_death = new mmove_t(FRAME_death2, FRAME_death50,
            boss2_frames_death, boss2_dead);

    /*
     * QUAKED monster_boss2 (1 .5 0) (-56 -56 0) (56 56 80) Ambush Trigger_Spawn
     * Sight
     */
    public static void SP_monster_boss2(edict_t self) {
        if (GameBase.deathmatch.value != 0) {
            GameUtil.G_FreeEdict(self);
            return;
        }

        sound_pain1 = GameBase.gi.soundindex("bosshovr/bhvpain1.wav");
        sound_pain2 = GameBase.gi.soundindex("bosshovr/bhvpain2.wav");
        sound_pain3 = GameBase.gi.soundindex("bosshovr/bhvpain3.wav");
        sound_death = GameBase.gi.soundindex("bosshovr/bhvdeth1.wav");
        sound_search1 = GameBase.gi.soundindex("bosshovr/bhvunqv1.wav");

        self.s.sound = GameBase.gi.soundindex("bosshovr/bhvengn1.wav");

        self.movetype = Defines.MOVETYPE_STEP;
        self.solid = Defines.SOLID_BBOX;
        self.s.modelindex = GameBase.gi
                .modelindex("models/monsters/boss2/tris.md2");
        Math3D.VectorSet(self.mins, -56, -56, 0);
        Math3D.VectorSet(self.maxs, 56, 56, 80);

        self.health = 2000;
        self.gib_health = -200;
        self.mass = 1000;

        self.flags |= Defines.FL_IMMUNE_LASER;

        self.pain = boss2_pain;
        self.die = boss2_die;

        self.monsterinfo.stand = boss2_stand;
        self.monsterinfo.walk = boss2_walk;
        self.monsterinfo.run = boss2_run;
        self.monsterinfo.attack = boss2_attack;
        self.monsterinfo.search = boss2_search;
        self.monsterinfo.checkattack = Boss2_CheckAttack;
        GameBase.gi.linkentity(self);

        self.monsterinfo.currentmove = boss2_move_stand;
        self.monsterinfo.scale = MODEL_SCALE;

        GameAI.flymonster_start.think(self);
    }
}