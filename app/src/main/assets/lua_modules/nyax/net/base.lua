-- METADATA
-- NAME Networking base
-- DESC Network related events and sendPacket function
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')
local event_gen = require('nyax.events.generator')

M.Events = event_gen.fromTable({
    _SocketOpened = event_gen.namespace.SOCKET_OPENED,
    _SocketClosed = event_gen.namespace.SOCKET_CLOSED,
    _UnhandledPacketReceived = event_gen.namespace.UNHANDLED_PACKET
})

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
        packet:send(api:makeOnReply(onReply))
    else
        packet:sendIgnoreReply()
    end
end

function M.sendPacketDeserialize(packet, onReply)
    if type(onReply) == "nil" then return M.sendPacket(packet, nil) end
    M.sendPacket(packet, function(p)
        local answer = M.deserialize(packet, p)
        if M.isError(answer) then error("Response error: "..answer:getMessage()) end
        onReply(answer)
    end)
end

function M.sendPacketCompletable(packet)
    local comp = util.newCompletable()
    M.sendPacket(packet, function(p) comp:complete(p) end)
    return comp
end

function M.sendPacketCompletableDeserialize(packet)
    local comp = util.newCompletable()
    M.sendPacketDeserialize(packet, function(p) comp:complete(p) end)
    return comp
end

M.send = M.sendPacket
M.sendD = M.sendPacketDeserialize
M.sendC = M.sendPacketCompletable
M.sendDC = M.sendPacketCompletableDeserialize
M.sendCD = M.sendPacketCompletableDeserialize

return util.secure(M)