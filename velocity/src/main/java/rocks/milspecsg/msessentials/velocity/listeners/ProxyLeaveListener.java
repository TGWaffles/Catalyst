/*
 *     MSEssentials - MilSpecSG
 *     Copyright (C) 2020 STG_Allen
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msessentials.velocity.listeners;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import rocks.milspecsg.msessentials.api.data.key.MSEssentialsKeys;
import rocks.milspecsg.msessentials.velocity.utils.StaffListUtils;
import rocks.milspecsg.msrepository.api.data.registry.Registry;

public class ProxyLeaveListener {

    @Inject
    private StaffListUtils staffListUtils;

    @Inject
    private ProxyServer proxyServer;

    @Inject
    private Registry registry;

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        Player player = event.getPlayer();
        staffListUtils.removeStaffNames(player);
        proxyServer.broadcast(LegacyComponentSerializer.legacy().deserialize(registry.getOrDefault(MSEssentialsKeys.LEAVE_MESSAGE).replace("%player%", player.getUsername())));
    }
}