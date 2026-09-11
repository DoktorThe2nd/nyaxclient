-- METADATA
-- NAME debug
-- DESC should not be used in release version of your plugin
-- AUTHOR DoktorThe2nd
-- VERSION built-in
-- REQUIRE-TRUSTED
-- METADATA

local M = {}

local System = api:findGlobalClass('java.lang.System')
local Toast = api:findGlobalClass('android.widget.Toast')
function M.print(line)
    System.out:println("Lua debug: " .. line)
end
function M.toast(line)
    Toast:makeText(api:getAppContext(), line, Toast.LENGTH_SHORT):show()
end

return M