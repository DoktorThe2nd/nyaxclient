-- METADATA
-- NAME Sync module
-- DESC Access to sync functions, including chats, profiles, etc.
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')

M.Packets = require('nyax.net.packets').fromTable("sync", {
    login = "LoginPacket"
})

return util.secure(M)