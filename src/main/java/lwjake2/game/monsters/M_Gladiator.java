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
import lwjake2.game.Monster;
import lwjake2.game.edict_t;
import lwjake2.game.mframe_t;
import lwjake2.game.mmove_t;
import lwjake2.game.monsters.M_Flash;
import lwjake2.util.Lib;
import lwjake2.util.Math3D;

public class M_Gladiator {

    //    This file generated by ModelGen - Do NOT Modify

    public final static int FRAME_stand1 = 0;

    public final static int FRAME_stand7 = 6;

    public final static int FRAME_walk1 = 7;

    public final static int FRAME_walk10 = 16;

    public final static int FRAME_walk11 = 17;

    public final static int FRAME_walk12 = 18;

    public final static int FRAME_walk16 = 22;

    public final static int FRAME_run1 = 23;

    public final static int FRAME_run6 = 28;

    public final static int FRAME_melee1 = 29;

    public final static int FRAME_melee17 = 45;

    public final static int FRAME_attack1 = 46;

    public final static int FRAME_attack9 = 54;

    public final static int FRAME_pain1 = 55;

    public final static int FRAME_pain6 = 60;

    public final static int FRAME_death1 = 61;

    public final static int FRAME_death22 = 82;

    public final static int FRAME_painup1 = 83;

    public final static int FRAME_painup7 = 89;

    public final static float MODEL_SCALE = 1.000000f;

    static int sound_pain1;

    static int sound_pain2;

    static int sound_die;

    static int sound_gun;

    static int sound_cleaver_swing;

    static int sound_cleaver_hit;

    static int sound_cleaver_miss;

    static int sound_idle;

    static int sound_search;

    static int sound_sight;

    static EntThinkAdapter gladiator_idle = new EntThinkAdapter() {
        public String getID() { return "gladiator_idle"; }
        public boolean think(edict_t self) {

            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_idle, 1,
                    Defines.ATTN_IDLE, 0);
            return true;
        }
    };

    static EntInteractAdapter gladiator_sight = new EntInteractAdapter() {
        public String getID() { return "gladiator_sight"; }
        public boolean interact(edict_t self, edict_t other) {

            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_sight, 1,
                    Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static EntThinkAdapter gladiator_search = new EntThinkAdapter() {
        public String getID() { return "gladiator_search"; }
        public boolean think(edict_t self) {

            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_search, 1,
                    Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static EntThinkAdapter gladiator_cleaver_swing = new EntThinkAdapter() {
        public String getID() { return "gladiator_cleaver_swing"; }
        public boolean think(edict_t self) {

            GameBase.gi.sound(self, Defines.CHAN_WEAPON, sound_cleaver_swing,
                    1, Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static mframe_t gladiator_frames_stand[] = new mframe_t[] {
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null),
            new mframe_t(GameAI.ai_stand, 0, null) };

    static mmove_t gladiator_move_stand = new mmove_t(FRAME_stand1,
            FRAME_stand7, gladiator_frames_stand, null);

    static EntThinkAdapter gladiator_stand = new EntThinkAdapter() {
        public String getID() { return "gladiator_stand"; }
        public boolean think(edict_t self) {

            self.monsterinfo.currentmove = gladiator_move_stand;
            return true;
        }
    };

    static mframe_t gladiator_frames_walk[] = new mframe_t[] {
            new mframe_t(GameAI.ai_walk, 15, null),
            new mframe_t(GameAI.ai_walk, 7, null),
            new mframe_t(GameAI.ai_walk, 6, null),
            new mframe_t(GameAI.ai_walk, 5, null),
            new mframe_t(GameAI.ai_walk, 2, null),
            new mframe_t(GameAI.ai_walk, 0, null),
            new mframe_t(GameAI.ai_walk, 2, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 12, null),
            new mframe_t(GameAI.ai_walk, 8, null),
            new mframe_t(GameAI.ai_walk, 5, null),
            new mframe_t(GameAI.ai_walk, 5, null),
            new mframe_t(GameAI.ai_walk, 2, null),
            new mframe_t(GameAI.ai_walk, 2, null),
            new mframe_t(GameAI.ai_walk, 1, null),
            new mframe_t(GameAI.ai_walk, 8, null) };

    static mmove_t gladiator_move_walk = new mmove_t(FRAME_walk1, FRAME_walk16,
            gladiator_frames_walk, null);

    static EntThinkAdapter gladiator_walk = new EntThinkAdapter() {
        public String getID() { return "gladiator_walk"; }
        public boolean think(edict_t self) {

            self.monsterinfo.currentmove = gladiator_move_walk;

            return true;
        }
    };

    static mframe_t gladiator_frames_run[] = new mframe_t[] {
            new mframe_t(GameAI.ai_run, 23, null),
            new mframe_t(GameAI.ai_run, 14, null),
            new mframe_t(GameAI.ai_run, 14, null),
            new mframe_t(GameAI.ai_run, 21, null),
            new mframe_t(GameAI.ai_run, 12, null),
            new mframe_t(GameAI.ai_run, 13, null) };

    static mmove_t gladiator_move_run = new mmove_t(FRAME_run1, FRAME_run6,
            gladiator_frames_run, null);

    static EntThinkAdapter gladiator_run = new EntThinkAdapter() {
        public String getID() { return "gladiator_run"; }
        public boolean think(edict_t self) {

            if ((self.monsterinfo.aiflags & Defines.AI_STAND_GROUND) != 0)
                self.monsterinfo.currentmove = gladiator_move_stand;
            else
                self.monsterinfo.currentmove = gladiator_move_run;

            return true;
        }
    };

    static EntThinkAdapter GaldiatorMelee = new EntThinkAdapter() {
        public String getID() { return "GaldiatorMelee"; }
        public boolean think(edict_t self) {

            float[] aim = { 0, 0, 0 };

            Math3D.VectorSet(aim, Defines.MELEE_DISTANCE, self.mins[0], -4);
            if (GameWeapon.fire_hit(self, aim, (20 + (Lib.rand() % 5)), 300))
                GameBase.gi.sound(self, Defines.CHAN_AUTO, sound_cleaver_hit,
                        1, Defines.ATTN_NORM, 0);
            else
                GameBase.gi.sound(self, Defines.CHAN_AUTO, sound_cleaver_miss,
                        1, Defines.ATTN_NORM, 0);
            return true;
        }
    };

    static mframe_t gladiator_frames_attack_melee[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, gladiator_cleaver_swing),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, GaldiatorMelee),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, gladiator_cleaver_swing),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, GaldiatorMelee),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null) };

    static mmove_t gladiator_move_attack_melee = new mmove_t(FRAME_melee1,
            FRAME_melee17, gladiator_frames_attack_melee, gladiator_run);

    static EntThinkAdapter gladiator_melee = new EntThinkAdapter() {
        public String getID() { return "gladiator_melee"; }
        public boolean think(edict_t self) {

            self.monsterinfo.currentmove = gladiator_move_attack_melee;
            return true;
        }
    };

    static EntThinkAdapter GladiatorGun = new EntThinkAdapter() {
        public String getID() { return "GladiatorGun"; }
        public boolean think(edict_t self) {

            float[] start = { 0, 0, 0 };

            float[] dir = { 0, 0, 0 };
            float[] forward = { 0, 0, 0 }, right = { 0, 0, 0 };

            Math3D.AngleVectors(self.s.angles, forward, right, null);
            Math3D
                    .G_ProjectSource(
                            self.s.origin,
                            M_Flash.monster_flash_offset[Defines.MZ2_GLADIATOR_RAILGUN_1],
                            forward, right, start);

            // calc direction to where we targted
            Math3D.VectorSubtract(self.pos1, start, dir);
            Math3D.VectorNormalize(dir);

            Monster.monster_fire_railgun(self, start, dir, 50, 100,
                    Defines.MZ2_GLADIATOR_RAILGUN_1);

            return true;
        }
    };

    static mframe_t gladiator_frames_attack_gun[] = new mframe_t[] {
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, GladiatorGun),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null),
            new mframe_t(GameAI.ai_charge, 0, null) };

    static mmove_t gladiator_move_attack_gun = new mmove_t(FRAME_attack1,
            FRAME_attack9, gladiator_frames_attack_gun, gladiator_run);

    static EntThinkAdapter gladiator_attack = new EntThinkAdapter() {
        public String getID() { return "gladiator_attack"; }
        public boolean think(edict_t self) {

            float range;
            float[] v = { 0, 0, 0 };

            // a small safe zone
            Math3D.VectorSubtract(self.s.origin, self.enemy.s.origin, v);
            range = Math3D.VectorLength(v);
            if (range <= (Defines.MELEE_DISTANCE + 32))
                return true;

            // charge up the railgun
            GameBase.gi.sound(self, Defines.CHAN_WEAPON, sound_gun, 1,
                    Defines.ATTN_NORM, 0);
            Math3D.VectorCopy(self.enemy.s.origin, self.pos1);
            //save for aiming the shot
            self.pos1[2] += self.enemy.viewheight;
            self.monsterinfo.currentmove = gladiator_move_attack_gun;
            return true;
        }
    };

    static mframe_t gladiator_frames_pain[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t gladiator_move_pain = new mmove_t(FRAME_pain1, FRAME_pain6,
            gladiator_frames_pain, gladiator_run);

    static mframe_t gladiator_frames_pain_air[] = new mframe_t[] {
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null),
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t gladiator_move_pain_air = new mmove_t(FRAME_painup1,
            FRAME_painup7, gladiator_frames_pain_air, gladiator_run);

    static EntPainAdapter gladiator_pain = new EntPainAdapter() {
        public String getID() { return "gladiator_pain"; }
        public void pain(edict_t self, edict_t other, float kick, int damage) {

            if (self.health < (self.max_health / 2))
                self.s.skinnum = 1;

            if (GameBase.level.time < self.pain_debounce_time) {
                if ((self.velocity[2] > 100)
                        && (self.monsterinfo.currentmove == gladiator_move_pain))
                    self.monsterinfo.currentmove = gladiator_move_pain_air;
                return;
            }

            self.pain_debounce_time = GameBase.level.time + 3;

            if (Lib.random() < 0.5)
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain1, 1,
                        Defines.ATTN_NORM, 0);
            else
                GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_pain2, 1,
                        Defines.ATTN_NORM, 0);

            if (GameBase.skill.value == 3)
                return; // no pain anims in nightmare

            if (self.velocity[2] > 100)
                self.monsterinfo.currentmove = gladiator_move_pain_air;
            else
                self.monsterinfo.currentmove = gladiator_move_pain;

        }
    };

    static EntThinkAdapter gladiator_dead = new EntThinkAdapter() {
        public String getID() { return "gladiator_dead"; }
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

    static mframe_t gladiator_frames_death[] = new mframe_t[] {
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
            new mframe_t(GameAI.ai_move, 0, null) };

    static mmove_t gladiator_move_death = new mmove_t(FRAME_death1,
            FRAME_death22, gladiator_frames_death, gladiator_dead);

    static EntDieAdapter gladiator_die = new EntDieAdapter() {
        public String getID() { return "gladiator_die"; }
        public void die(edict_t self, edict_t inflictor, edict_t attacker,
                int damage, float[] point) {
            int n;

            //    check for gib
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

            //    regular death
            GameBase.gi.sound(self, Defines.CHAN_VOICE, sound_die, 1,
                    Defines.ATTN_NORM, 0);
            self.deadflag = Defines.DEAD_DEAD;
            self.takedamage = Defines.DAMAGE_YES;

            self.monsterinfo.currentmove = gladiator_move_death;

        }
    };

    /*
     * QUAKED monster_gladiator (1 .5 0) (-32 -32 -24) (32 32 64) Ambush
     * Trigger_Spawn Sight
     */
    public static void SP_monster_gladiator(edict_t self) {
        if (GameBase.deathmatch.value != 0) {
            GameUtil.G_FreeEdict(self);
            return;
        }

        sound_pain1 = GameBase.gi.soundindex("gladiator/pain.wav");
        sound_pain2 = GameBase.gi.soundindex("gladiator/gldpain2.wav");
        sound_die = GameBase.gi.soundindex("gladiator/glddeth2.wav");
        sound_gun = GameBase.gi.soundindex("gladiator/railgun.wav");
        sound_cleaver_swing = GameBase.gi.soundindex("gladiator/melee1.wav");
        sound_cleaver_hit = GameBase.gi.soundindex("gladiator/melee2.wav");
        sound_cleaver_miss = GameBase.gi.soundindex("gladiator/melee3.wav");
        sound_idle = GameBase.gi.soundindex("gladiator/gldidle1.wav");
        sound_search = GameBase.gi.soundindex("gladiator/gldsrch1.wav");
        sound_sight = GameBase.gi.soundindex("gladiator/sight.wav");

        self.movetype = Defines.MOVETYPE_STEP;
        self.solid = Defines.SOLID_BBOX;
        self.s.modelindex = GameBase.gi
                .modelindex("models/monsters/gladiatr/tris.md2");
        Math3D.VectorSet(self.mins, -32, -32, -24);
        Math3D.VectorSet(self.maxs, 32, 32, 64);

        self.health = 400;
        self.gib_health = -175;
        self.mass = 400;

        self.pain = gladiator_pain;
        self.die = gladiator_die;

        self.monsterinfo.stand = gladiator_stand;
        self.monsterinfo.walk = gladiator_walk;
        self.monsterinfo.run = gladiator_run;
        self.monsterinfo.dodge = null;
        self.monsterinfo.attack = gladiator_attack;
        self.monsterinfo.melee = gladiator_melee;
        self.monsterinfo.sight = gladiator_sight;
        self.monsterinfo.idle = gladiator_idle;
        self.monsterinfo.search = gladiator_search;

        GameBase.gi.linkentity(self);
        self.monsterinfo.currentmove = gladiator_move_stand;
        self.monsterinfo.scale = MODEL_SCALE;

        GameAI.walkmonster_start.think(self);
    }
}