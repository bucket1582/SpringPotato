package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.SuggestionHandler
import com.github.bucket1572.springpotato.text_components.SuccessComponent
import io.github.monun.tap.effect.playFirework
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class SuggestionHandOutListener(private val plugin: SpringPotato): Listener {
    @EventHandler
    fun onHandoutResult(event: PlayerInteractEvent) {
        // 게임 시작 전에는 이벤트를 적용하지 않음.
        if (!plugin.isRunning) return

        // 초기화
        val action = event.action
        val player = event.player
        val itemInMain = player.inventory.itemInMainHand

        // 우클릭으로 제출
        if (action == Action.RIGHT_CLICK_AIR) {
            if (itemInMain.type in SuggestionHandler.suggester.keys &&
                event.player !in SuggestionHandler.handedOutPlayer[itemInMain.type]!! &&
                (event.player != SuggestionHandler.suggester[itemInMain.type])
            ) {
                SuggestionHandler.handedOutPlayer[itemInMain.type]!!.add(event.player)
                Bukkit.broadcast(
                    SuccessComponent("${event.player.name}님이 ${itemInMain.type.name}을 제출했습니다!").getComponent()
                )
                val firework = FireworkEffect.builder().apply {
                    trail(true)
                    flicker(false)
                    withColor(Color.GREEN, Color.YELLOW, Color.WHITE, Color.LIME)
                    with(FireworkEffect.Type.BALL)
                }.build()
                event.player.playSound(event.player.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.6f, 1.0f)
                event.player.world.playFirework(event.player.location, firework, 15.0)
            }
        }
    }
}