-- METADATA
-- REQUIRE-TRUSTED
-- METADATA

local util = require('nyax.util')
local events_gen = require('nyax.events.generator')
local net_base = require('nyax.net.base')
local net_session = require('nyax.net.packets.session')
local session = require('nyax.session')
local ui = require('nyax.ui.builder')
local ui_manager = require('nyax.ui.manager')

local authToken
local getCodeEnterRoot = util.oneTimeInit(function()

end)

function phoneEntered(phone)
    local authcode = net_session.newAuthCodeRequest(phone)
    net_base.sendPacket(authcode, function(packet)
        local answer = net_base.deserialize(authcode, packet)
        if net_base.isError(answer) then error("Auth code request error: "..answer:getMessage()) end
        authToken = answer:getAuthToken()
        ui_manager.setLayout(getCodeEnterRoot())
    end)
end

local getPhoneEnterRoot = util.oneTimeInit(function()
    local phoneEnterField = ui.makeEditText("+7 (000) 000-00-00","",ui.InputType.TYPE_CLASS_PHONE)
    return ui.root(false, 20,
        ui.makeText("NYAX"):align(ui.TextAlign.CENTER):scale(1.2),
        ui.makeText("Custom open-source MAX client"):align(ui.TextAlign.CENTER),
        ui.cardContainer(true, 5,
            phoneEnterField,
            ui.makeButton("->", function(self)
                phoneEntered(session.normalizePhone(phoneEnterField:getText()))
            end)
        )
    ):gravity(ui.Gravity.CENTER)
end)

events_gen.generate("nyax:startAuthFlow").subscribe(function(...)
    ui_manager.setLayout(getPhoneEnterRoot())
end)