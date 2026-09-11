-- METADATA
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local Utils = api:findClass('Utils')

function writeNilTrackable(value)
    if type(value) == "nil" then return "nil" end
    if type(value) == "string" then return "S"..value end
    return value
end
function readNilTrackable(value)
    if type(value) == "string" then
        if value == "nil" then return nil end
        return value:sub(1)
    end
    return value
end

function M.newEmptyTrackable()
    return {
        _value = nil,
        set = function(self, value)
            local old = self._value
            self._value = writeNilTrackable(value)
            return readNilTrackable(old)
            end,
        get = function(self)
            return readNilTrackable(self._value)
            end,
        isEmpty = function(self)
            return type(self._value) == "nil"
            end,
        clear = function(self)
            local old = self._value
            self._value = nil
            return readNilTrackable(old)
            end
    }
end

function M.oneTimeInit(fun)
    local table = {
        _result = M.newEmptyTrackable(),
        _invoke = function(self)
            self._result:set(fun())
        end
    }
    setmetatable(table, {
        __call = function(self)
            if self._result:isEmpty() then self:_invoke() end
            return self._result:get()
        end
    })
    return table
end

M.Coercers = api:findClass('Utils$Coercer')

function M.jListToTable(list)
    return Utils:coerceList(list)
end

function M.tableToJList(tbl, coercer)
    if type(tbl) ~= "table" then error("util.tableToJList expected table, got "..type(tbl)) end
    return Utils:coerceToArrayList(tbl, coercer)
end
function M.tableToJMap(tbl, key_coercer, value_coercer)
    if type(tbl) ~= "table" then error("util.tableToJMap expected table, got "..type(tbl)) end
    return Utils:coerceToHashMap(tbl, key_coercer, value_coercer)
end
function M.tableToJMapC(tbl)
    if type(tbl) ~= "table" then error("util.tableToJMapC expected table, got "..type(tbl)) end
    return Utils:coerceToMapContainer(tbl)
end

return M