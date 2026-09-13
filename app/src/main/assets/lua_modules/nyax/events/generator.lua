-- METADATA
-- NAME Events generator
-- DESC Full access to events system
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')

M.namespace = events_ids

function M.generate(name)
    return util.secure({
        call = function(...) events_api:call(name, table.pack(...)) end,
        subscribe = function(fun) events_api:subscribe(name, fun) end
    })
end

function M.generateNoncallable(name)
    return util.secure({
        call = function(...) return error(name.." event is not callable") end,
        subscribe = function(fun) events_api:subscribe(name, fun) end
    })
end

function M.fromTable(tbl)
    local ret = {}
    for i, v in pairs(tbl) do
        if i:sub(0, 1) == "_" then
            ret[i:sub(1)] = M.generateNoncallable(v)
        else
            ret[i] = M.generate(v)
        end
    end
    return util.secure(ret)
end

return util.secure(M)