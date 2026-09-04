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
    net_base.sendPacket(net_session.newSessionInit(), function(packet)
        debug.print("gibberish")
        debug.print(packet:toString())
        end)
end)