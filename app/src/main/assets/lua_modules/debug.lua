-- METADATA
-- NAME debug
-- DESC should not be used in release version of your plugin
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local System = luajava.bindClass('java.lang.System')
function M.print(line)
    System.out:println("Lua debug: " .. line)
end

return M