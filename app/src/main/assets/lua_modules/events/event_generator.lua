-- METADATA
-- NAME Full access to event system
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

M.namespace = events_ids

function M.generate(name)
    local _name = name
    return {
        name = function() return _name end,
        call = function(...) events_api:call(_name, ...) end,
        subscribe = function(fun) events_api:subscribe(_name, fun) end
    }
end

function M.generate_wrapped(name)
    return function()
        return M.generate(name)
    end
end

function M.generate_noncallable(name)
    local _name = name
    return {
        name = function() return _name end,
        call = function(...) error(_name.." event is not callable") end,
        subscribe = function(fun) events_api:subscribe(_name, fun) end
    }
end

function M.generate_wrapped_noncallable(name)
    return function()
        return M.generate_noncallable(name)
    end
end

return M