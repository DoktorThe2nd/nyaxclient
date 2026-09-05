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

function M.isError(packet)
    if type(packet.isError) == "function" then return packet:isError() end
    return false
end

function M.deserialize(instance, packet)
    if M.isError(packet) then
        return luajava.new(api:findPacketClass('ErrorPacket'), packet.payload)
    end
    if not instance:deserialize(packet.payload) then
        return luajava.new(api:findPacketClass('ErrorPacket'), "Deserialize fault. Incorrect instance type?")
    end
    return instance
end

function M.sendPacket(packet, onReply)
    if type(packet) ~= "userdata" then error("sendPacket got wrong packet argument") end
    if type(packet.send) ~= "function" then error("sendPacket got packet with no send function") end
    if type(packet.sendIgnoreReply) ~= "function" then error("sendPacket got packet with no sendIgnoreReply function") end
    if type(onReply) == "function" then
        local proxy = api:makeOnReply(onReply)
        packet:send(proxy)
    else
        packet:sendIgnoreReply()
    end
end

return M