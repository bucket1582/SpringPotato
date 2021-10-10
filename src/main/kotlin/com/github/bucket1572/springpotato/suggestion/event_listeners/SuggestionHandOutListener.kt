package com.github.bucket1572.springpotato.suggestion.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.basic_logic.handlers.GameHandler
import com.github.bucket1572.springpotato.basic_logic.handlers.ScoreHandler
import com.github.bucket1572.springpotato.wand.handlers.WandHandler
import com.github.bucket1572.springpotato.common.text_components.SuccessComponent
import com.github.bucket1572.springpotato.suggestion.handlers.SuggestionHandler
import io.github.monun.tap.effect.playFirework
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class SuggestionHandOutListener(private val plugin: SpringPotato) : Listener {
    @EventHandler
    fun onHandoutResult(event: PlayerInteractEvent) {
        // 게임이 제출 페이즈가 아니면 무시한다.
        if (!GameHandler.isHandOutPhase()) return

        // 초기화
        val action = event.action
        val player = event.player
        val interactionItem = event.item ?: return

        // 우클릭으로 제출
        // 조건
        if (action != Action.RIGHT_CLICK_AIR) return
        if (interactionItem.type !in SuggestionHandler.suggester.keys) return
        if (player in SuggestionHandler.handedOutPlayer[interactionItem.type]!!) return
        if (player == SuggestionHandler.suggester[interactionItem.type]) return
        // 만약 그것이 기본 지급 완드여도 안 됨.
        if (WandHandler.isWand(interactionItem)) return

        SuggestionHandler.handedOutPlayer[interactionItem.type]!!.add(event.player)

        // 제출 공표
        Bukkit.broadcast(
            SuccessComponent("${event.player.name}님이 ${interactionItem.type.name}을 제출했습니다!").getComponent()
        )

        // 쿨타임 초기화
        WandHandler.removeCooldown(player, WandHandler.suggestionWand)

        // 견제 시 레벨 지급
        player.giveExpLevels(ScoreHandler.EXP_LEVEL_FOR_HANDOUT)

        // 성공 짝짝짝
        releaseFireWork(player)

    }

    private fun releaseFireWork(player: Player) {
        val firework = FireworkEffect.builder().apply {
            trail(true)
            flicker(false)
            withColor(Color.GREEN, Color.YELLOW, Color.WHITE, Color.LIME)
            with(FireworkEffect.Type.BALL)
        }.build()
        player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.6f, 1.0f)
        player.world.playFirework(player.location, firework, 15.0)
    }
}