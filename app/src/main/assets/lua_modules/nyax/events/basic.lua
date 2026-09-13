-- METADATA
-- NAME Basic events
-- DESC Access for basic events, such as Startup
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE nyax.events.generator nyax.util
-- METADATA

local M = {}
local util = require('nyax.util')
local event_gen = require('nyax.events.generator')

M.Events = event_gen.fromTable({
    _Startup = event_gen.namespace.STARTUP
})

return util.secure(M)