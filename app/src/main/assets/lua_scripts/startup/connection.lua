-- METADATA
-- REQUIRE-TRUSTED
-- METADATA

local events_gen = require('nyax.events.generator')
local events_base = require('nyax.events.base')
local net_base = require('nyax.net.base')
local net_cnt = require('nyax.net.connection')
local net_session = require('nyax.net.packets.session')
local session = require('nyax.session')

-- CONNECTION --

function connect(...)
    net_cnt.start()
    local sessionPacket = net_session.newSessionInit()
    net_base.sendPacket(sessionPacket, function(packet)
        local answer = net_base.deserialize(sessionPacket, packet)
        if net_base.isError(answer) then error("Session init error: " .. answer:getMessage()) end
        net_session.setCallsSeed(answer:getCallsSeed())
        onConnected()
    end)
end

events_base.subscribe(events_base.Events.Startup, connect)
events_base.subscribe(net_base.Events.SocketClosed, connect)

-- SESSION SYNC --
-- remember: session is auto loaded in Consts
-- BUT! NO TOKEN IF NOT AUTH

function onConnected()
    if not session.getCurrentSession():hasToken() then
        events_gen.generate("nyax:startAuthFlow").call()
    else
        -- authed
    end
end