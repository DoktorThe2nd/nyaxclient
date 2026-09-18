-- METADATA
-- REQUIRE-TRUSTED
-- METADATA

local util = require('nyax.util')
local net = require('nyax.net.base')
local net_cnt = require('nyax.net.connection')
local events = require('nyax.events.basic')
local session = require('nyax.session')

function onConnected()
    if not session.getCurrentSession():hasToken() then
        session.Events.StartAuthFlow:call()
    else
        session.Events.LoginSuccess:call()
    end
end

function connect()
    net_cnt.start()
    net.sendD(session.Packets.sessionInit:new(), function(answer)
        session.setCallsSeed(answer:getCallsSeed())
        onConnected()
    end)
end

net.Events.SocketClosed:subscribe(connect)
events.Startup:subscribe(connect)