-- METADATA
-- NAME Util module
-- DESC Safe to use. Provides variety of helpful functions.
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local Utils = api:findClass('Utils')

function M.map(tbl, mapper)
    M.assert(tbl, "table", "util.map arg1")
    M.assertCallable(mapper, "util.map arg2")
    local size = tbl.n
    if math.type(size) ~= "integer" then size = #tbl end
    local ret = {}
    for i = 1, size do
        ret[i] = mapper(tbl[i])
    end
    ret.n = size
    return ret
end


function M.insertAll(tbl, i, tbl2)
    M.assert(tbl, "table", "util.insertAll arg1")
    M.assert(i, "integer", "util.insertAll arg2")
    M.assert(tbl2, "table", "util.insertAll arg3")
    local w_size = tbl.n
    local r_size = tbl2.n
    if math.type(w_size) ~= "integer" then w_size = #tbl end
    if math.type(r_size) ~= "integer" then r_size = #tbl2 end
    table.move(tbl, i, w_size, r_size+i, tbl)
    table.move(tbl2, 1, r_size, i, tbl)
    if math.type(tbl.n) == "integer" then tbl.n = w_size+r_size end
end

function M.addAll(tbl, tbl2)
    M.assert(tbl, "table", "util.addAll arg1")
    M.assert(tbl2, "table", "util.addAll arg2")
    local w_size = tbl.n
    local r_size = tbl2.n
    if math.type(w_size) ~= "integer" then w_size = #tbl end
    if math.type(r_size) ~= "integer" then r_size = #tbl2 end
    table.move(tbl2, 1, r_size, w_size+1, tbl)
    if math.type(tbl.n) == "integer" then tbl.n = w_size+r_size end
end
M.appendAll = M.addAll

function M.prependAll(tbl, tbl2)
    M.insertAll(tbl, 1, tbl2)
end


function M.insertMapAll(tbl, i, tbl2, mapper)
    M.insertAll(tbl, i, M.map(tbl2, mapper))
end

function M.addMapAll(tbl, tbl2, mapper)
    M.addAll(tbl, M.map(tbl2, mapper))
end
M.appendMapAll = M.addMapAll

function M.prependMapAll(tbl, tbl2, mapper)
    M.prependAll(tbl, M.map(tbl2, mapper))
end


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

function _completable_add_assert(self, class, suffix)
    self._asserters[#self._asserters+1] = {
        class = class,
        suffix = suffix
    }
end

function _completable_set(self, value)
    for i = 1, #self._asserters do
        M.assertJClass(value, self._asserters[i].class, self._asserters[i].suffix)
    end
    self._value = value
    self.complete = function() end
    self.get = function(self2) return self2._value end
    self.set = function(self2, v) self2._value = v end
    self.onComplete = function(self2, f) f(self2._value) end
    self.isComplete = function() return true end
    for i = 1, #self._waiters do
        self._waiters[i](self._value)
    end
    self._waiters = nil
    self._asserters = nil
end

function M.newCompletable()
    return {
        _value = nil,
        _waiters = {},
        _asserters = {},
        onComplete = function(self, fun) if type(fun) ~= "nil" then self._waiters[#self._waiters+1] = fun end end,
        set = _completable_set,
        complete = _completable_set,
        isComplete = function() return false end,
        getOrNil = function(self) return self._value end,
        get = function() return error("completable.get called on incomplete completable") end,
        assertJClass = _completable_add_assert
    }
end

function M.mapCompletable(compl, mapper)
    M.assertCallable(compl.onComplete, "util.mapComplete arg1.onComplete")
    M.assertCallable(mapper, "util.mapComplete arg2")
    local ret = M.newCompletable()
    compl:onComplete(function(v)
        ret:complete(mapper(v))
        end)
    return ret
end
M.mapC = M.mapCompletable


M.Coercers = api:findClass('Utils$Coercer')

function M.jListToTable(list)
    return Utils:coerceList(list) -- java asserts on its own
end

function M.tableToJList(tbl, coercer)
    return Utils:coerceToArrayList(tbl, coercer) -- java asserts on its own
end
function M.tableToJMap(tbl, key_coercer, value_coercer)
    return Utils:coerceToHashMap(tbl, key_coercer, value_coercer) -- java asserts on its own
end
function M.tableToJMapC(tbl)
    return Utils:coerceToMapContainer(tbl) -- java asserts on its own
end


function error_builder(method, prefix, expect, got)
    M.assert(expect, "string", "error_builder 'expect' arg")
    if type(prefix) == "string" then
        return prefix..": "..method.." failed: expected "..expect..", got "..got
    else
        return method.." failed: expected "..expect..", got "..got
    end
end

function M.checkJClass(userdata, classpath)
    return api:classIsInstance(api:findClass(M.assert(classpath, "string", "util.checkJClass arg1")), userdata)
end
function M.assertJClass(userdata, classpath, prefix)
    if not M.checkJClass(userdata, classpath) then
        return error(error_builder("assertJClass", prefix, classpath, api:getObjectClassName(userdata)))
    end
    return userdata
end
function M.assert(v, typ, prefix)
    local t = type(v)
    if t == "number" then
        if typ == "number" then return v end
        if math.type(v) ~= typ then
            return error(error_builder("assert", prefix, typ, math.type(v)))
        end
    else
        if t ~= typ then
            return error(error_builder("assert", prefix, typ, t))
        end
    end
    return v
end
function M.isCallable(v)
    local t = type(v)
    if t == "function" then
        return true
    end
    if t == "table" or t == "userdata" then
        local mt = debug.getmetatable(v)
        if type(mt) == "table" then
            return mt.__call ~= nil
        end
    end
    return false
end
function M.assertCallable(v, prefix)
    if not M.isCallable(v) then
        return error(error_builder("assertCallable", prefix, "function or callable table", type(v)))
    end
    return v
end


function M.protect(tbl)
    local proxy = {}
    local mt = {
        __index = tbl,
        __newindex = function(t, k, v) return error("Attempt to modify '"..k.."' in protected table") end,
        __metatable = false
    }
    setmetatable(proxy, mt)
    return proxy
end
M.secure = M.protect

return M.protect(M)