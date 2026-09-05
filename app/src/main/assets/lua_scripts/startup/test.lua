-- METADATA
-- NAME Test
-- DESC some description
-- VERSION 1
-- REQUIRE-TRUSTED
-- METADATA

local events_base = require('nyax.events.base')
local net_base = require('nyax.net.base')
local net_cnt = require('nyax.net.connection')
local net_session = require('nyax.net.packets.session')
local debug = require('nyax.debug')

events_base.subscribe(events_base.Events.Startup, function(...)
    net_cnt.start()
    local sessionPacket = net_session.newSessionInit()
    net_base.sendPacket(sessionPacket, function(packet)
        local answer = net_base.deserialize(sessionPacket, packet)
        if net_base.isError(answer) then error(answer:getMessage()) end
        net_session.setCallsSeed(answer:getCallsSeed())
        end)
end)