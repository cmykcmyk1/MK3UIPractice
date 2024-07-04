package com.cmykcmyk.mk3uipractice

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

data class Tile(
    @DrawableRes val img: Int,
    val size: DpSize
)

open class CharacterSkin(
    private val tiles: Array<Tile>,
    private val loopFrame: Int = tiles.lastIndex
) {
    @Composable
    open fun getImage() {
        val tileId = nextFrame()
        if (tileId != -1)
            draw(tiles[tileId])
    }

    @Composable
    protected fun nextFrame(): Int {
        //HACK: after change character, I need to reset animate values. I don't know another way how to do it.
        var needRecompose by remember(this) { mutableStateOf(true) }
        LaunchedEffect(this) {
            needRecompose = false
        }
        if (needRecompose)
            return -1

        val lastFrame = tiles.lastIndex

        if (loopFrame > 0) {
            // first playback
            var target: Int by remember(this) { mutableIntStateOf(0) }
            val curFrame: Int by animateIntAsState(
                targetValue = target,
                animationSpec = tween(100 * lastFrame, 0, LinearEasing),
                label = ""
            )

            LaunchedEffect(this) {
                target = lastFrame
            }

            if (curFrame < loopFrame || lastFrame == loopFrame)
                return curFrame
        }

        // looping
        val infTransition = rememberInfiniteTransition("")
        val curFrame_2 by infTransition.animateValue(
            initialValue = loopFrame,
            targetValue = lastFrame,
            typeConverter = Int.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(100 * (lastFrame - loopFrame), 0, LinearEasing),
                RepeatMode.Restart
            ),
            label = ""
        )

        return curFrame_2
    }

    @Composable
    protected fun draw(tile: Tile) {
        Box(
            modifier = Modifier
                .widthIn(160.dp)
                .heightIn(260.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = tile.img),
                contentDescription = null,
                modifier = Modifier.size(tile.size * 1.2f)
            )
        }
    }
}

class SkinWithConstantAnim(tiles: Array<Tile>) : CharacterSkin(tiles, 0)
class SkinWithOneTimeAnim(tiles: Array<Tile>) : CharacterSkin(tiles, tiles.lastIndex)

class SindelVictoryPose : CharacterSkin(CharacterData.getTiles(CharacterData.CharacterId.Sindel, CharacterData.AnimId.VictoryPose)) {
    @Composable
    override fun getImage() {
        val frame = nextFrame()
        var levitation: State<Dp>? = null

        if (frame == 6) {
            val infTransition = rememberInfiniteTransition("")
            levitation = infTransition.animateValue(
                initialValue = 0.dp,
                targetValue = (-50).dp,
                typeConverter = Dp.VectorConverter,
                animationSpec = infiniteRepeatable(
                    animation = tween(700, 0, LinearEasing),
                    RepeatMode.Reverse
                ),
                label = ""
            )
        }

        Box(modifier = Modifier.offset(y = levitation?.value ?: 0.dp)) {
            super.getImage()
        }
    }
}

object CharacterData {
    enum class CharacterId {
        ShangTsung, Sindel, Jax, Kano, LiuKang,
        Sonya, Stryker, Smoke, SubZero, Cyrax,
        Sektor, NightWolf, Sheeva, KungLao, Kabal
    }

    enum class AnimId { FightingStance, VictoryPose }

    public @StringRes fun getName(character: CharacterId): Int {
        return names.get(character.ordinal)
    }

    public @DrawableRes fun getAvatar(character: CharacterId): Int {
        return avatars.get(character.ordinal)
    }

    public @DrawableRes fun getArt(character: CharacterId): Int {
        return arts.get(character.ordinal)
    }

    public @StringRes fun getStory(character: CharacterId): Int {
        return stories.get(character.ordinal)
    }

    public @Composable fun getSkin(character: CharacterId, anim: AnimId) {
        return if (anim == AnimId.FightingStance) fightingStanceSkins.get(character.ordinal).getImage() else victoryPoseSkins.get(character.ordinal).getImage()
    }

    public @RawRes fun getNameSound(character: CharacterId): Int {
        return nameSounds.get(character.ordinal)
    }

    public fun getTiles(
        character: CharacterId,
        anim: AnimId
    ): Array<Tile> {
        return if (anim == AnimId.FightingStance) fightingStanceTiles.get(character.ordinal) else victoryPoseTiles.get(character.ordinal)
    }

    private val names: Array<Int>
    private val avatars: Array<Int>
    private val arts: Array<Int>
    private val stories: Array<Int>
    private val nameSounds: Array<Int>

    private val fightingStanceTiles: Array<Array<Tile>>
    private val victoryPoseTiles: Array<Array<Tile>>

    private val fightingStanceSkins: Array<CharacterSkin>
    private val victoryPoseSkins: Array<CharacterSkin>

    init {
        names = arrayOf(
            R.string.shangtsung_name,
            R.string.sindel_name,
            R.string.jax_name,
            R.string.kano_name,
            R.string.liukang_name,
            R.string.sonya_name,
            R.string.stryker_name,
            R.string.smoke_name,
            R.string.subzero_name,
            R.string.cyrax_name,
            R.string.sektor_name,
            R.string.nightwolf_name,
            R.string.sheeva_name,
            R.string.kunglao_name,
            R.string.kabal_name,
        )

        avatars = arrayOf(
            R.drawable.avatar_shangtsung,
            R.drawable.avatar_sindel,
            R.drawable.avatar_jax,
            R.drawable.avatar_kano,
            R.drawable.avatar_liukang,
            R.drawable.avatar_sonya,
            R.drawable.avatar_stryker,
            R.drawable.avatar_smoke,
            R.drawable.avatar_subzero,
            R.drawable.avatar_cyrax,
            R.drawable.avatar_sektor,
            R.drawable.avatar_nightwolf,
            R.drawable.avatar_sheeva,
            R.drawable.avatar_kunglao,
            R.drawable.avatar_kabal,
        )

        arts = arrayOf(
            R.drawable.bio_shangtsung,
            R.drawable.bio_sindel,
            R.drawable.bio_jax,
            R.drawable.bio_kano,
            R.drawable.bio_liukang,
            R.drawable.bio_sonya,
            R.drawable.bio_stryker,
            R.drawable.bio_smoke,
            R.drawable.bio_subzero,
            R.drawable.bio_cyrax,
            R.drawable.bio_sektor,
            R.drawable.bio_nightwolf,
            R.drawable.bio_sheeva,
            R.drawable.bio_kunglao,
            R.drawable.bio_kabal,
        )

        stories = arrayOf(
            R.string.shangtsung_story,
            R.string.sindel_story,
            R.string.jax_story,
            R.string.kano_story,
            R.string.liukang_story,
            R.string.sonya_story,
            R.string.stryker_story,
            R.string.smoke_story,
            R.string.subzero_story,
            R.string.cyrax_story,
            R.string.sektor_story,
            R.string.nightwolf_story,
            R.string.sheeva_story,
            R.string.kunglao_story,
            R.string.kabal_story,
        )

        nameSounds = arrayOf(
            R.raw.shangtsung,
            R.raw.sindel,
            R.raw.jax,
            R.raw.kano,
            R.raw.liukang,
            R.raw.sonya,
            R.raw.stryker,
            R.raw.smoke,
            R.raw.subzero,
            R.raw.cyrax,
            R.raw.sektor,
            R.raw.nightwolf,
            R.raw.sheeva,
            R.raw.kunglao,
            R.raw.kabal,
        )

        fightingStanceTiles = arrayOf(
            arrayOf(
                Tile(R.drawable.tile_shangtsung_fighting_stance_1, DpSize(72.dp, 124.dp)),
                Tile(R.drawable.tile_shangtsung_fighting_stance_2, DpSize(70.dp, 123.dp)),
                Tile(R.drawable.tile_shangtsung_fighting_stance_3, DpSize(66.dp, 124.dp)),
                Tile(R.drawable.tile_shangtsung_fighting_stance_4, DpSize(64.dp, 125.dp)),
                Tile(R.drawable.tile_shangtsung_fighting_stance_5, DpSize(64.dp, 125.dp)),
                Tile(R.drawable.tile_shangtsung_fighting_stance_6, DpSize(65.dp, 124.dp)),
                Tile(R.drawable.tile_shangtsung_fighting_stance_7, DpSize(66.dp, 124.dp)),
                Tile(R.drawable.tile_shangtsung_fighting_stance_8, DpSize(68.dp, 124.dp)),
                Tile(R.drawable.tile_shangtsung_fighting_stance_9, DpSize(69.dp, 124.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_sindel_fighting_stance_1, DpSize(69.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_fighting_stance_2, DpSize(69.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_fighting_stance_3, DpSize(69.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_fighting_stance_4, DpSize(69.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_fighting_stance_5, DpSize(69.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_fighting_stance_6, DpSize(69.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_fighting_stance_7, DpSize(69.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_fighting_stance_8, DpSize(69.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_fighting_stance_9, DpSize(69.dp, 129.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_jax_fighting_stance_1, DpSize(81.dp, 136.dp)),
                Tile(R.drawable.tile_jax_fighting_stance_2, DpSize(81.dp, 136.dp)),
                Tile(R.drawable.tile_jax_fighting_stance_3, DpSize(81.dp, 136.dp)),
                Tile(R.drawable.tile_jax_fighting_stance_4, DpSize(81.dp, 136.dp)),
                Tile(R.drawable.tile_jax_fighting_stance_5, DpSize(81.dp, 136.dp)),
                Tile(R.drawable.tile_jax_fighting_stance_6, DpSize(81.dp, 136.dp)),
                Tile(R.drawable.tile_jax_fighting_stance_7, DpSize(81.dp, 136.dp)),
                Tile(R.drawable.tile_jax_fighting_stance_8, DpSize(81.dp, 136.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_kano_fighting_stance_1, DpSize(74.dp, 132.dp)),
                Tile(R.drawable.tile_kano_fighting_stance_2, DpSize(74.dp, 132.dp)),
                Tile(R.drawable.tile_kano_fighting_stance_3, DpSize(74.dp, 132.dp)),
                Tile(R.drawable.tile_kano_fighting_stance_4, DpSize(74.dp, 132.dp)),
                Tile(R.drawable.tile_kano_fighting_stance_5, DpSize(74.dp, 132.dp)),
                Tile(R.drawable.tile_kano_fighting_stance_6, DpSize(74.dp, 132.dp)),
                Tile(R.drawable.tile_kano_fighting_stance_7, DpSize(74.dp, 132.dp)),
                Tile(R.drawable.tile_kano_fighting_stance_8, DpSize(74.dp, 132.dp)),
                Tile(R.drawable.tile_kano_fighting_stance_9, DpSize(74.dp, 132.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_liukang_fighting_stance_1, DpSize(69.dp, 123.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_2, DpSize(70.dp, 125.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_3, DpSize(68.dp, 127.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_4, DpSize(65.dp, 128.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_5, DpSize(62.dp, 128.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_6, DpSize(61.dp, 128.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_7, DpSize(60.dp, 127.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_8, DpSize(59.dp, 126.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_9, DpSize(59.dp, 125.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_8, DpSize(59.dp, 126.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_7, DpSize(60.dp, 127.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_6, DpSize(61.dp, 128.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_5, DpSize(62.dp, 128.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_4, DpSize(65.dp, 128.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_3, DpSize(68.dp, 127.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_2, DpSize(70.dp, 125.dp)),
                Tile(R.drawable.tile_liukang_fighting_stance_1, DpSize(69.dp, 123.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_sonya_fighting_stance_1, DpSize(71.dp, 130.dp)),
                Tile(R.drawable.tile_sonya_fighting_stance_2, DpSize(71.dp, 130.dp)),
                Tile(R.drawable.tile_sonya_fighting_stance_3, DpSize(71.dp, 130.dp)),
                Tile(R.drawable.tile_sonya_fighting_stance_4, DpSize(71.dp, 130.dp)),
                Tile(R.drawable.tile_sonya_fighting_stance_5, DpSize(71.dp, 130.dp)),
                Tile(R.drawable.tile_sonya_fighting_stance_6, DpSize(71.dp, 130.dp)),
                Tile(R.drawable.tile_sonya_fighting_stance_7, DpSize(71.dp, 130.dp)),
                Tile(R.drawable.tile_sonya_fighting_stance_8, DpSize(71.dp, 130.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_stryker_fighting_stance_1, DpSize(65.dp, 134.dp)),
                Tile(R.drawable.tile_stryker_fighting_stance_2, DpSize(65.dp, 134.dp)),
                Tile(R.drawable.tile_stryker_fighting_stance_3, DpSize(65.dp, 134.dp)),
                Tile(R.drawable.tile_stryker_fighting_stance_4, DpSize(65.dp, 134.dp)),
                Tile(R.drawable.tile_stryker_fighting_stance_5, DpSize(65.dp, 134.dp)),
                Tile(R.drawable.tile_stryker_fighting_stance_6, DpSize(65.dp, 134.dp)),
                Tile(R.drawable.tile_stryker_fighting_stance_7, DpSize(65.dp, 134.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_smoke_fighting_stance_1, DpSize(69.dp, 133.dp)),
                Tile(R.drawable.tile_smoke_fighting_stance_2, DpSize(66.dp, 133.dp)),
                Tile(R.drawable.tile_smoke_fighting_stance_3, DpSize(63.dp, 133.dp)),
                Tile(R.drawable.tile_smoke_fighting_stance_4, DpSize(62.dp, 133.dp)),
                Tile(R.drawable.tile_smoke_fighting_stance_5, DpSize(66.dp, 133.dp)),
                Tile(R.drawable.tile_smoke_fighting_stance_6, DpSize(68.dp, 133.dp)),
                Tile(R.drawable.tile_smoke_fighting_stance_7, DpSize(72.dp, 133.dp)),
                Tile(R.drawable.tile_smoke_fighting_stance_8, DpSize(72.dp, 133.dp)),
                Tile(R.drawable.tile_smoke_fighting_stance_9, DpSize(71.dp, 133.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_subzero_fighting_stance_1, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_2, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_3, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_4, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_5, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_6, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_7, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_8, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_9, DpSize(67.dp, 131.dp)),
                Tile(R.drawable.tile_subzero_fighting_stance_10, DpSize(67.dp, 131.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_cyrax_fighting_stance_1, DpSize(71.dp, 136.dp)),
                Tile(R.drawable.tile_cyrax_fighting_stance_2, DpSize(69.dp, 136.dp)),
                Tile(R.drawable.tile_cyrax_fighting_stance_3, DpSize(68.dp, 136.dp)),
                Tile(R.drawable.tile_cyrax_fighting_stance_4, DpSize(67.dp, 136.dp)),
                Tile(R.drawable.tile_cyrax_fighting_stance_5, DpSize(68.dp, 131.dp)),
                Tile(R.drawable.tile_cyrax_fighting_stance_6, DpSize(71.dp, 130.dp)),
                Tile(R.drawable.tile_cyrax_fighting_stance_7, DpSize(73.dp, 131.dp)),
                Tile(R.drawable.tile_cyrax_fighting_stance_8, DpSize(72.dp, 132.dp)),
                Tile(R.drawable.tile_cyrax_fighting_stance_9, DpSize(72.dp, 133.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_sektor_fighting_stance_1, DpSize(77.dp, 135.dp)),
                Tile(R.drawable.tile_sektor_fighting_stance_2, DpSize(77.dp, 135.dp)),
                Tile(R.drawable.tile_sektor_fighting_stance_3, DpSize(77.dp, 135.dp)),
                Tile(R.drawable.tile_sektor_fighting_stance_4, DpSize(77.dp, 135.dp)),
                Tile(R.drawable.tile_sektor_fighting_stance_5, DpSize(77.dp, 135.dp)),
                Tile(R.drawable.tile_sektor_fighting_stance_6, DpSize(77.dp, 135.dp)),
                Tile(R.drawable.tile_sektor_fighting_stance_7, DpSize(77.dp, 135.dp)),
                Tile(R.drawable.tile_sektor_fighting_stance_8, DpSize(77.dp, 135.dp)),
                Tile(R.drawable.tile_sektor_fighting_stance_9, DpSize(77.dp, 135.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_nightwolf_fighting_stance_1, DpSize(65.dp, 125.dp)),
                Tile(R.drawable.tile_nightwolf_fighting_stance_2, DpSize(65.dp, 125.dp)),
                Tile(R.drawable.tile_nightwolf_fighting_stance_3, DpSize(66.dp, 125.dp)),
                Tile(R.drawable.tile_nightwolf_fighting_stance_4, DpSize(66.dp, 125.dp)),
                Tile(R.drawable.tile_nightwolf_fighting_stance_5, DpSize(65.dp, 125.dp)),
                Tile(R.drawable.tile_nightwolf_fighting_stance_6, DpSize(66.dp, 126.dp)),
                Tile(R.drawable.tile_nightwolf_fighting_stance_7, DpSize(66.dp, 126.dp)),
                Tile(R.drawable.tile_nightwolf_fighting_stance_8, DpSize(64.dp, 126.dp)),
                Tile(R.drawable.tile_nightwolf_fighting_stance_9, DpSize(64.dp, 126.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_sheeva_fighting_stance_1, DpSize(77.dp, 145.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_2, DpSize(78.dp, 145.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_3, DpSize(78.dp, 144.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_4, DpSize(78.dp, 143.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_5, DpSize(77.dp, 143.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_6, DpSize(77.dp, 143.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_5, DpSize(77.dp, 143.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_4, DpSize(78.dp, 143.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_3, DpSize(78.dp, 144.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_2, DpSize(78.dp, 145.dp)),
                Tile(R.drawable.tile_sheeva_fighting_stance_1, DpSize(77.dp, 145.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_kunglao_fighting_stance_1, DpSize(65.dp, 131.dp)),
                Tile(R.drawable.tile_kunglao_fighting_stance_2, DpSize(65.dp, 131.dp)),
                Tile(R.drawable.tile_kunglao_fighting_stance_3, DpSize(65.dp, 131.dp)),
                Tile(R.drawable.tile_kunglao_fighting_stance_4, DpSize(65.dp, 131.dp)),
                Tile(R.drawable.tile_kunglao_fighting_stance_5, DpSize(65.dp, 131.dp)),
                Tile(R.drawable.tile_kunglao_fighting_stance_6, DpSize(65.dp, 131.dp)),
                Tile(R.drawable.tile_kunglao_fighting_stance_7, DpSize(65.dp, 131.dp)),
                Tile(R.drawable.tile_kunglao_fighting_stance_8, DpSize(65.dp, 131.dp)),
                Tile(R.drawable.tile_kunglao_fighting_stance_9, DpSize(65.dp, 131.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_kabal_fighting_stance_1, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_2, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_3, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_4, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_5, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_6, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_7, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_8, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_9, DpSize(72.dp, 136.dp)),
                Tile(R.drawable.tile_kabal_fighting_stance_10, DpSize(72.dp, 136.dp)),
            )
        )

        victoryPoseTiles = arrayOf(
            arrayOf(
                Tile(R.drawable.tile_shangtsung_victory_pose_1, DpSize(74.dp, 124.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_2, DpSize(80.dp, 127.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_3, DpSize(64.dp, 147.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_4, DpSize(65.dp, 149.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_5, DpSize(65.dp, 151.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_6, DpSize(65.dp, 156.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_7, DpSize(68.dp, 159.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_8, DpSize(70.dp, 161.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_9, DpSize(70.dp, 161.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_10, DpSize(70.dp, 161.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_11, DpSize(70.dp, 161.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_12, DpSize(70.dp, 161.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_13, DpSize(70.dp, 161.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_14, DpSize(70.dp, 161.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_15, DpSize(70.dp, 161.dp)),
                Tile(R.drawable.tile_shangtsung_victory_pose_16, DpSize(70.dp, 161.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_sindel_victory_pose_1, DpSize(68.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_victory_pose_2, DpSize(63.dp, 128.dp)),
                Tile(R.drawable.tile_sindel_victory_pose_3, DpSize(78.dp, 129.dp)),
                Tile(R.drawable.tile_sindel_victory_pose_4, DpSize(84.dp, 134.dp)),
                Tile(R.drawable.tile_sindel_victory_pose_5, DpSize(89.dp, 135.dp)),
                Tile(R.drawable.tile_sindel_victory_pose_6, DpSize(94.dp, 136.dp)),
                Tile(R.drawable.tile_sindel_victory_pose_7, DpSize(97.dp, 137.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_jax_victory_pose_1, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_2, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_3, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_4, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_5, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_6, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_7, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_8, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_9, DpSize(125.dp, 167.dp)),
                Tile(R.drawable.tile_jax_victory_pose_10, DpSize(125.dp, 167.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_kano_victory_pose_1, DpSize(75.dp, 129.dp)),
                Tile(R.drawable.tile_kano_victory_pose_2, DpSize(60.dp, 132.dp)),
                Tile(R.drawable.tile_kano_victory_pose_3, DpSize(60.dp, 142.dp)),
                Tile(R.drawable.tile_kano_victory_pose_4, DpSize(60.dp, 162.dp)),
                Tile(R.drawable.tile_kano_victory_pose_5, DpSize(102.dp, 143.dp)),
                Tile(R.drawable.tile_kano_victory_pose_6, DpSize(122.dp, 136.dp)),
                Tile(R.drawable.tile_kano_victory_pose_7, DpSize(119.dp, 135.dp)),
                Tile(R.drawable.tile_kano_victory_pose_8, DpSize(85.dp, 143.dp)),
                Tile(R.drawable.tile_kano_victory_pose_9, DpSize(70.dp, 152.dp)),
                Tile(R.drawable.tile_kano_victory_pose_10, DpSize(75.dp, 141.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_liukang_victory_pose_1, DpSize(65.dp, 135.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_2, DpSize(40.dp, 138.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_3, DpSize(94.dp, 138.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_4, DpSize(133.dp, 138.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_5, DpSize(102.dp, 156.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_6, DpSize(46.dp, 171.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_7, DpSize(34.dp, 167.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_8, DpSize(41.dp, 153.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_9, DpSize(47.dp, 137.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_10, DpSize(46.dp, 138.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_11, DpSize(46.dp, 138.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_12, DpSize(46.dp, 135.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_13, DpSize(46.dp, 127.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_14, DpSize(47.dp, 122.dp)),
                Tile(R.drawable.tile_liukang_victory_pose_15, DpSize(46.dp, 115.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_sonya_victory_pose_1, DpSize(72.dp, 129.dp)),
                Tile(R.drawable.tile_sonya_victory_pose_2, DpSize(60.dp, 133.dp)),
                Tile(R.drawable.tile_sonya_victory_pose_3, DpSize(51.dp, 131.dp)),
                Tile(R.drawable.tile_sonya_victory_pose_4, DpSize(81.dp, 132.dp)),
                Tile(R.drawable.tile_sonya_victory_pose_5, DpSize(64.dp, 132.dp)),
                Tile(R.drawable.tile_sonya_victory_pose_6, DpSize(52.dp, 132.dp)),
                Tile(R.drawable.tile_sonya_victory_pose_7, DpSize(56.dp, 130.dp)),
                Tile(R.drawable.tile_sonya_victory_pose_8, DpSize(54.dp, 130.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_stryker_victory_pose_1, DpSize(48.dp, 135.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_2, DpSize(50.dp, 137.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_3, DpSize(51.dp, 137.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_4, DpSize(56.dp, 155.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_5, DpSize(48.dp, 182.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_6, DpSize(62.dp, 212.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_7, DpSize(53.dp, 210.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_8, DpSize(61.dp, 209.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_9, DpSize(54.dp, 205.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_10, DpSize(55.dp, 166.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_11, DpSize(66.dp, 139.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_12, DpSize(72.dp, 139.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_13, DpSize(63.dp, 139.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_14, DpSize(65.dp, 139.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_15, DpSize(61.dp, 139.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_16, DpSize(63.dp, 139.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_17, DpSize(57.dp, 139.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_18, DpSize(54.dp, 134.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_19, DpSize(45.dp, 138.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_20, DpSize(53.dp, 137.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_21, DpSize(48.dp, 137.dp)),
                Tile(R.drawable.tile_stryker_victory_pose_22, DpSize(47.dp, 136.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_smoke_victory_pose_1, DpSize(78.dp, 136.dp)),
                Tile(R.drawable.tile_smoke_victory_pose_2, DpSize(73.dp, 144.dp)),
                Tile(R.drawable.tile_smoke_victory_pose_3, DpSize(74.dp, 139.dp)),
                Tile(R.drawable.tile_smoke_victory_pose_4, DpSize(63.dp, 137.dp)),
                Tile(R.drawable.tile_smoke_victory_pose_5, DpSize(60.dp, 137.dp)),
                Tile(R.drawable.tile_smoke_victory_pose_6, DpSize(53.dp, 137.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_subzero_victory_pose_1, DpSize(65.dp, 133.dp)),
                Tile(R.drawable.tile_subzero_victory_pose_2, DpSize(65.dp, 137.dp)),
                Tile(R.drawable.tile_subzero_victory_pose_3, DpSize(59.dp, 161.dp)),
                Tile(R.drawable.tile_subzero_victory_pose_4, DpSize(53.dp, 170.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_cyrax_victory_pose_1, DpSize(61.dp, 130.dp)),
                Tile(R.drawable.tile_cyrax_victory_pose_2, DpSize(55.dp, 129.dp)),
                Tile(R.drawable.tile_cyrax_victory_pose_3, DpSize(56.dp, 131.dp)),
                Tile(R.drawable.tile_cyrax_victory_pose_4, DpSize(66.dp, 134.dp)),
                Tile(R.drawable.tile_cyrax_victory_pose_5, DpSize(62.dp, 148.dp)),
                Tile(R.drawable.tile_cyrax_victory_pose_6, DpSize(59.dp, 158.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_sektor_victory_pose_1, DpSize(86.dp, 142.dp)),
                Tile(R.drawable.tile_sektor_victory_pose_2, DpSize(81.dp, 150.dp)),
                Tile(R.drawable.tile_sektor_victory_pose_3, DpSize(82.dp, 146.dp)),
                Tile(R.drawable.tile_sektor_victory_pose_4, DpSize(71.dp, 142.dp)),
                Tile(R.drawable.tile_sektor_victory_pose_5, DpSize(67.dp, 143.dp)),
                Tile(R.drawable.tile_sektor_victory_pose_6, DpSize(62.dp, 142.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_nightwolf_victory_pose_1, DpSize(89.dp, 128.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_2, DpSize(78.dp, 141.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_3, DpSize(66.dp, 142.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_4, DpSize(58.dp, 121.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_5, DpSize(59.dp, 145.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_6, DpSize(64.dp, 155.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_7, DpSize(73.dp, 159.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_8, DpSize(79.dp, 167.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_9, DpSize(79.dp, 169.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_10, DpSize(81.dp, 173.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_11, DpSize(83.dp, 176.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_12, DpSize(84.dp, 179.dp)),
                Tile(R.drawable.tile_nightwolf_victory_pose_13, DpSize(85.dp, 181.dp)),
            ),
            arrayOf(
                Tile(R.drawable.tile_sheeva_victory_pose_1, DpSize(75.dp, 148.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_2, DpSize(92.dp, 148.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_3, DpSize(111.dp, 153.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_4, DpSize(119.dp, 152.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_5, DpSize(122.dp, 151.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_6, DpSize(113.dp, 156.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_7, DpSize(98.dp, 170.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_8, DpSize(68.dp, 183.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_9, DpSize(103.dp, 173.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_10, DpSize(124.dp, 158.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_11, DpSize(140.dp, 146.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_12, DpSize(141.dp, 139.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_13, DpSize(145.dp, 134.dp)),
                Tile(R.drawable.tile_sheeva_victory_pose_14, DpSize(146.dp, 133.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_kunglao_victory_pose_1, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_2, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_3, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_4, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_5, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_6, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_7, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_8, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_9, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_10, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_11, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_12, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_13, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_14, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_15, DpSize(76.dp, 145.dp)),
                Tile(R.drawable.tile_kunglao_victory_pose_16, DpSize(76.dp, 145.dp))
            ),
            arrayOf(
                Tile(R.drawable.tile_kabal_victory_pose_1, DpSize(79.dp, 134.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_2, DpSize(114.dp, 135.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_3, DpSize(198.dp, 135.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_4, DpSize(178.dp, 158.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_5, DpSize(117.dp, 186.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_6, DpSize(83.dp, 182.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_7, DpSize(78.dp, 160.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_8, DpSize(71.dp, 132.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_9, DpSize(69.dp, 133.dp)),
                Tile(R.drawable.tile_kabal_victory_pose_10, DpSize(82.dp, 134.dp))
            )
        )

        fightingStanceSkins = arrayOf(
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.ShangTsung.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Sindel.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Jax.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Kano.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.LiuKang.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Sonya.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Stryker.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Smoke.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.SubZero.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Cyrax.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Sektor.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.NightWolf.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Sheeva.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.KungLao.ordinal)),
            SkinWithConstantAnim(fightingStanceTiles.get(CharacterId.Kabal.ordinal))
        )

        victoryPoseSkins = arrayOf(
            CharacterSkin(victoryPoseTiles.get(CharacterId.ShangTsung.ordinal), loopFrame = 7),
            SindelVictoryPose(),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Jax.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Kano.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.LiuKang.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Sonya.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Stryker.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Smoke.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.SubZero.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Cyrax.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Sektor.ordinal)),
            CharacterSkin(victoryPoseTiles.get(CharacterId.NightWolf.ordinal), loopFrame = 9),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Sheeva.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.KungLao.ordinal)),
            SkinWithOneTimeAnim(victoryPoseTiles.get(CharacterId.Kabal.ordinal))
        )
    }
}
