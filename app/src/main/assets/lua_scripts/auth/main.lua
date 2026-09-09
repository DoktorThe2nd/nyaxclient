-- METADATA
-- REQUIRE-TRUSTED
-- METADATA

local util = require('nyax.util')
local events = require('nyax.events.base')
local events_gen = require('nyax.events.generator')
local net_base = require('nyax.net.base')
local net_session = require('nyax.net.packets.session')
local session = require('nyax.session')
local ui = require('nyax.ui.builder')
local ui_manager = require('nyax.ui.manager')

function baseRoot()
    return ui.root(false, 20,
        ui.makeText("NYAX"):align(ui.TextAlign.CENTER):scale(1.2),
        ui.makeText("Custom open-source MAX client"):align(ui.TextAlign.CENTER)
    ):gravity(ui.Gravity.CENTER)
end

local phone
function loginEnd(packet)
    local answer = net_base.deserialize(net_session.newLogin(), packet)
    if net_base.isError(answer) then error("Login error: "..answer:getMessage()) end
    session.getCurrentSession().token = answer:getToken()
    session.getCurrentSession().phone = phone
    session.saveCurrentSession()
    events.call(session.Events.LoginSuccess)
end

local track_id
function passwordEntered(password)
    local send_password = net_session.newAuthPasswordSend(track_id, password)
    net_base.sendPacket(send_password, loginEnd)
end

local getPasswordEnterRoot = util.oneTimeInit(function()
    local passwordEnterField = ui.makeEditText("password","",ui.InputType.TYPE_TEXT_VARIATION_PASSWORD)
    return baseRoot():add(ui.cardContainer(true, 5,
        passwordEnterField,
        ui.makeButton("->", function(self)
            passwordEntered(passwordEnterField:getText():toString())
        end)
    ))
end)

local auth_token
function codeEntered(code)
    local send_code = net_session.newAuthCodeSend(auth_token, code)
    net_base.sendPacket(send_code, function(packet)
        local answer = net_base.deserialize(send_code, packet)
        -- if error - no password needed (prolly)
        if net_base.isError(answer) then return loginEnd(packet) end
        -- passwords, please
        track_id = answer:getTrackId()
        ui_manager.setLayout(getPasswordEnterRoot())
    end)
end

local getCodeEnterRoot = util.oneTimeInit(function()
    local codeEnterField = ui.makeEditText("XXXXXX","",ui.InputType.TYPE_CLASS_NUMBER):setMaxLength(6)
    return baseRoot():add(ui.cardContainer(true, 5,
        codeEnterField,
        ui.makeButton("->", function(self)
            codeEntered(codeEnterField:getText():toString())
        end)
    ))
end)

function phoneEntered()
    local request_code = net_session.newAuthCodeRequest(phone)
    net_base.sendPacket(request_code, function(packet)
        local answer = net_base.deserialize(request_code, packet)
        if net_base.isError(answer) then error("Auth code request error: "..answer:getMessage()) end
        auth_token = answer:getAuthToken()
    end)
end

local getPhoneEnterRoot = util.oneTimeInit(function()
    local phoneEnterField = ui.makeEditText("+7 (000) 000-00-00","",ui.InputType.TYPE_CLASS_PHONE)
    return baseRoot():add(ui.cardContainer(true, 5,
        phoneEnterField,
        ui.makeButton("->", function(self)
            phone = session.normalizePhone(phoneEnterField:getText():toString())
            phoneEntered()
            ui_manager.setLayout(getCodeEnterRoot())
        end)
    ))
end)

events_gen.generate("nyax:startAuthFlow").subscribe(function(...)
    ui_manager.setLayout(getPhoneEnterRoot())
end)