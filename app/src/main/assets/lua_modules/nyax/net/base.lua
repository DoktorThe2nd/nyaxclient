-- METADATA
-- NAME Networking base
-- DESC Network related events and sendPacket function
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local event_gen = require('nyax.events.event_generator')

M.Events = {}
M.Events.Connected = event_gen.generate_wrapped_noncallable(event_gen.namespace.SOCKET_OPENED)
M.Events.Disconnected = event_gen.generate_wrapped_noncallable(event_gen.namespace.SOCKET_CLOSED)
M.Events.UnhandledPacketReceived = event_gen.generate_wrapped_noncallable(event_gen.namespace.UNHANDLED_PACKET)

function M.sendPacket(packet, onReply)
    if type(packet) ~= "userdata" then error("sendPacket got wrong packet argument") end
    if type(packet.send) ~= "function" then error("sendPacket got packet with no send function") end
    if type(packet.sendIgnoreReply) ~= "function" then error("sendPacket got packet with no sendIgnoreReply function") end
    if type(onReply) == "function" then
        local proxy = api:onReplyProxy(onReply)
        packet:send(proxy)
    else
        packet:sendIgnoreReply()
    end
end

return M