/*
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

package org.anvilpowered.catalyst.velocity.service;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;
import org.anvilpowered.anvil.api.plugin.Plugin;
import org.anvilpowered.catalyst.api.service.EventService;

import java.util.concurrent.TimeUnit;

public class VelocityEventService implements EventService<Object> {

    @Inject
    Plugin<?> catalyst;

    @Inject
    private ProxyServer proxyServer;

    @Override
    public void fire(Object event) {
        proxyServer.getEventManager().fire(event).join();
    }

    @Override
    public void schedule(Runnable runnable, TimeUnit timeUnit, int time) {
        proxyServer.getScheduler().buildTask(catalyst, runnable).repeat(time, timeUnit).schedule();
    }

    @Override
    public void run(Runnable runnable) {
        proxyServer.getScheduler().buildTask(catalyst, runnable).schedule();
    }
}
