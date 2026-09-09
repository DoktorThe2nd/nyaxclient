-- METADATA
-- REQUIRE-TRUSTED
-- METADATA

local events = require('nyax.events.base')
local session = require('nyax.session')
local net_base = require('nyax.net.base')
local net_sync = require('nyax.net.packets.sync')

local Profile = api:findClass('Profile')

events.subscribe(session.Events.LoginSuccess, function(...)
    local login_packet = net_sync.newLogin()
    net_base.sendPacket(login_packet, function(packet)
        local answer = net_base.deserialize(login_packet, packet)
        if net_base.isError(answer) then error("Login sync error: "..answer:getMessage()) end
        Profile.me = luajava.new(Profile, answer:getMyProfileData())
        -- TODO CHATS!
    end)
end)