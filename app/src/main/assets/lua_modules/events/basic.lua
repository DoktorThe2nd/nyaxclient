-- METADATA
-- NAME Basic events
-- DESC Module for accessing basic events, such as Startup
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE events.event_generator
-- METADATA

local M = {}

local EventGen = require('events.event_generator')

-- events list

function M.NullEvent()
    return {
        name = function() return "NullEvent" end,
        call = function(...) end,
        subscribe = function(fun) end
    }
end -- Does nothing. Subscriptions and calls are ignored.
M.Startup = EventGen.generate_wrapped_noncallable(EventGen.namespace.STARTUP) -- Called on app startup. Non-callable. (Call will generate error)

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