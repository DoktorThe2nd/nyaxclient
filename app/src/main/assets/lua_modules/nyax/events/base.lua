-- METADATA
-- NAME Events base
-- DESC Module for accessing events safely and a couple of basic events
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE nyax.events.event_generator
-- METADATA

local M = {}

local event_gen = require('nyax.events.event_generator')

-- events list

M.Events = {}

function M.Events.NullEvent()
    return {
        call = function(...) end,
        subscribe = function(fun) end
    }
end -- Does nothing. Subscriptions and calls are ignored.
M.Events.Startup = event_gen.generate_wrapped_noncallable(event_gen.namespace.STARTUP) -- Called on app startup. Non-callable. (Call will generate error)

-- events list

function M.subscribe(event, fun)
    if type(event) ~= "function" then error("Event subscribe got wrong event parameter") end
    if type(fun) ~= "function" then error("Event subscribe got wrong function parameter") end
    event().subscribe(fun)
end

function M.call(event, ...)
    if type(event) ~= "function" then error("Event call got wrong event parameter") end
    event().call(...)
end

return M