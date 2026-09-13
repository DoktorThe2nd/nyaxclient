-- METADATA
-- NAME Luajava packet access
-- DESC (UNSAFE) Full access to packet classes in com.doktorthe2nd.nyax.types.packets
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}
local util = require('nyax.util')

function M.dir(dir_name)
    return {
        dir = dir_name,
        getClass = function(self, name)
            return api:findPacketClass(self.dir_name..'.'..name)
            end,
        getInst = function(self, name, ...)
            return luajava.new(self:getClass(name), ...)
            end,
        getInstAble = function(self, name)
            return util.secure({
                getClass = function() return self:getClass(name) end,
                newInstance = function(...) return self:getInst(name, ...) end,
                new = function(...) return self:getInst(name, ...) end
            })
            end
    }
end

function M.fromTable(dir, tbl)
    if type(dir) == "string" then dir = M.dir(dir) end
    local ret = {}
    for i, v in pairs(tbl) do
        ret[i] = dir:getInstAble(v)
    end
    return util.secure(ret)
end

return util.secure(M)