-- METADATA
-- NAME Util module
-- DESC Safe to use. Provides variety of helpful functions.
-- AUTHOR DoktorThe2nd
-- VERSION built-in
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

function _completable_set(self, value)
    self._value:set(value)
    self.complete = function(_self2, _v) end
    self.get = function(self2) return self2._value:get() end
    self.set = function(self2, v) self2._value:set(v) end
    self.onComplete = function(self2, f) f(self2._value:get()) end
    for i = 1, #self._waiters do
        self._waiters[i](self:get())
    end
    self._waiters = {}
end

function M.newCompletable()
    return {
        _value = M.newEmptyTrackable(),
        _waiters = {},
        onComplete = function(self, fun) if type(fun) ~= "nil" then self._waiters[#self._waiters+1] = fun end end,
        set = _completable_set,
        complete = _completable_set,
        isComplete = function(self) return not self._value:isEmpty() end,
        getOrNil = function(self) return self._value:get() end,
        get = function(self) error("completable.get called on incomplete completable") end
    }
end

function M.mapCompletable(compl, mapper)
    local ret = M.newCompletable()
    compl:onComplete(function(v) ret:complete(mapper(v)) end)
    return ret
end
M.mapC = M.mapCompletable

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

function M.secure(tbl)
    local proxy = {}
    local mt = {
        __index = tbl,
        __newindex = function(t, k, v) return error("Attempt to modify value in secured table: '"..k.."'") end,
        __metatable = false
    }
    setmetatable(proxy, mt)
    return proxy
end

return M.secure(M)