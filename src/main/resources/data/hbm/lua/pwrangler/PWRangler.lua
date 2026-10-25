-- PWRangler, adapted from the actual X5846 program for CC:Tweaked.
-- Uses the existing HBM PWR peripheral; no replacement reactor or OC emulation.
-- Usage: disk/PWRangler [reactor peripheral name] [monitor peripheral name]
local args = {...}
local reactorName = args[1]
if not reactorName then
    for _, name in ipairs(peripheral.getNames()) do
        if peripheral.hasType(name, "ntm_pwr_control") then reactorName = name end
    end
end
if not reactorName or not peripheral.hasType(reactorName, "ntm_pwr_control") then
    error("Connect an HBM PWR controller directly or through a wired modem", 0)
end
local function call(method, ...) return peripheral.call(reactorName, method, ...) end
local function setLevel(value) return call("setLevel", value) end
local monitorName = args[2]
if not monitorName then
    for _, name in ipairs(peripheral.getNames()) do
        if peripheral.hasType(name, "monitor") then monitorName = name; break end
    end
end
local output = monitorName and peripheral.wrap(monitorName) or term.current()
if not output or not output.getSize then error("Invalid monitor peripheral", 0) end
if monitorName then output.setTextScale(0.5) end
-- The source visible drawing occupies x60..113/y4..48. A native window keeps
-- the entire original layout, even on a smaller computer/monitor viewport.
local screen = window.create(output, 1, 1, 54, 45, false)
local panX, panY = 0, 0
local palette = {}
for index=0,15 do
    local color=2^index
    local r,g,b=output.getPaletteColor(color)
    palette[#palette+1]={color=color,r=r*255,g=g*255,b=b*255}
end
local colorCache = {}
local function nearest(rgb)
    if colorCache[rgb] then return colorCache[rgb] end
    local r=math.floor(rgb/65536)%256
    local g=math.floor(rgb/256)%256
    local b=rgb%256
    local best,distance
    for _, entry in ipairs(palette) do
        local delta=(r-entry.r)^2+(g-entry.g)^2+(b-entry.b)^2
        if not distance or delta<distance then best,distance=entry.color,delta end
    end
    colorCache[rgb]=best
    return best
end
local function foreground(rgb) screen.setTextColor(nearest(rgb)) end
local function background(rgb) screen.setBackgroundColor(nearest(rgb)) end
-- CC terminals use their native single-byte glyph set, rather than UTF-8.
local glyphs={ ["█"]=219,["▄"]=220,["▀"]=223,["┃"]=179,["→"]=26,["←"]=27,["Δ"]=235 }
local function glyphText(value)
    value=tostring(value)
    for glyph,code in pairs(glyphs) do value=value:gsub(glyph,string.char(code)) end
    return value
end
local function text(x,y,value)
    x,y=x-59,y-3
    if y<1 or y>45 then return end
    value=glyphText(value)
    if x<1 then value=value:sub(2-x);x=1 end
    if x>54 or #value==0 then return end
    screen.setCursorPos(x,y);screen.write(value:sub(1,55-x))
end
local function fill(x,y,width,height,value)
    if width<=0 or height<=0 then return end
    local row=string.rep(glyphText(value),math.floor(width))
    for line=math.max(4,y),math.min(48,y+math.floor(height)-1) do text(x,line,row) end
end
local function paint()
    local width,height=output.getSize()
    local viewHeight=math.max(1,height-1)
    panX=math.max(0,math.min(math.max(0,54-width),panX))
    panY=math.max(0,math.min(math.max(0,45-viewHeight),panY))
    output.setBackgroundColor(colors.black);output.setTextColor(colors.white)
    for row=1,viewHeight do
        output.setCursorPos(1,row)
        if row+panY<=45 then
            local line,fg,bg=screen.getLine(row+panY)
            local count=math.min(width,54-panX)
            output.blit(line:sub(panX+1,panX+count),fg:sub(panX+1,panX+count),bg:sub(panX+1,panX+count))
            if count<width then output.write(string.rep(" ",width-count)) end
        else output.write(string.rep(" ",width)) end
    end
    output.setCursorPos(1,height)
    output.write(("PWRangler | arrows/wheel: pan | Q: quit"..string.rep(" ",width)):sub(1,width))
end

colorGradient = {0x00FF00, 0x6BEE00, 0x95DB00, 0xB0C800, 0xC5B400, 0xD79F00, 0xE68700, 0xF46900, 0xFC4700, 0xFF0000}
coreHeatESTOP = true
coolantLossESTOP = true
hotCoolantESTOP = true

local const = {}
local initialized = {}
local mt = {
    __newindex = function(t, k, v)
        if not initialized[k] then
            rawset(t, k, v)
            initialized[k] = true
        else
            error(k .. " is a constant")
        end
    end
}
setmetatable(const, mt)

_, _, const.coreHeatCapacity = call("getHeat")
if type(const.coreHeatCapacity) ~= "number" or const.coreHeatCapacity <= 0 then
    error("PWRangler needs an assembled PWR controller with a positive heat capacity", 0)
end
const.fullCoreHeatMAX = const.coreHeatCapacity * 0.9
const.coldCoolantLevelMIN = 10000
const.hotCoolantLevelMAX = 0.5

runSig = true

coldCoolantLevel = 0
coldCoolantOutflow = 0
prevCoolantFlow = 0

hotCoolantLevel = 0
hotCoolantOutflow = 0
prevHotCoolantFlow = 0

fill(1,1,160,160," ")

-- Button Bullshit
function newButton(x, y, width, height, colorUp, colorDown, func)
    local button = {xpos = 0, ypos = 0, width = 0, height = 0, colorUp = 0, colorDown = 0, func = nil}
    button.xpos = x
    button.ypos = y
    button.width = width
    button.height = height
    button.colorUp = colorUp
    button.colorDown = colorDown
    button.func = func
    return button
end

function drawButton(button, color)
    background(color)
    fill(button.xpos, button.ypos, button.width, button.height, " ")
    background(0x000000)
end

pressedButton = nil
function buttonPress(_, _, x, y, _, _)
    for _, b in pairs(buttons) do
        if((x>=b.xpos) and (x<(b.xpos+b.width)) and (y>=b.ypos) and (y<(b.ypos+b.height)) ) then
            drawButton(b, b.colorDown)
            pressedButton = b
        end
    end
end

function buttonRelease(_, _, x, y, _, _)
    if pressedButton then
        local button = pressedButton
        pressedButton = nil
        button.func()
        drawButton(button, button.colorUp)
    end
end
--Button bullshit ends

buttons = {}

local deltas = {1,5,10} -- This is very bad. Need new buttons
for i, d in ipairs(deltas) do
    buttons[i] = newButton(61+(i-1)*7, 6, 6, 2, 0xFFFFFF, 0xAAAAAA, function() setLevel(call("getLevel")+d) end)
    buttons[i+3] = newButton(61+(i-1)*7, 9, 6, 2, 0xFFFFFF, 0xAAAAAA, function() setLevel(call("getLevel")-d) end)
end

buttons[7] = newButton(82, 6, 11, 5, 0xFF0000, 0xAA0000, function() setLevel(100) end)
buttons[8] = newButton(94, 6, 12, 2, 0x00FF00, 0x00AA00, function() coreHeatESTOP = not coreHeatESTOP if coreHeatESTOP == true then buttons[8].colorUp = 0x00FF00 buttons[8].colorDown = 0x00AA00 else buttons[8].colorUp = 0xFF0000 buttons[8].colorDown = 0xAA0000 end end)
buttons[9] = newButton(94, 9, 12, 2, 0x00FF00, 0x00AA00, function() coolantLossESTOP = not coolantLossESTOP if coolantLossESTOP == true then buttons[9].colorUp = 0x00FF00 buttons[9].colorDown = 0x00AA00 else buttons[9].colorUp = 0xFF0000 buttons[9].colorDown = 0xAA0000 end  end)

buttons[10] = newButton(107, 8, 5, 3, 0xFF0000, 0xAA0000, function() runSig = false end)

foreground(0xAAAAAA)

--Control rods
fill(60,4,54,8,"█")

--Outlet
fill(91,13,16,8,"█")

--Inlet
fill(91,30,16,8,"█")

text(61,13,"    █████████████████████")
text(61,14,"     █ █ █ █ █ █ █ █ █ █")
text(61,15,"     █ █ █▄█▄█▄█▄█▄█ █ █")
text(61,16,"    ▄█████▀█▀█▀█▀█▀█████▄")
text(61,17,"  ▄███▀█ █ █ █ █ █ █ █▀███▄")
text(61,18," ▄██ █ █ █ █ █ █ █ █ █ █ ██▄")
text(61,19," ██                       ██")
text(61,20,"██▀ █████████████████████ ▀██")
text(61,21,"██  █████████████████████  ██▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄")
text(61,22,"██  █                   █  ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀")
text(61,23,"██  █████████████████████  → → → → → → → → → →")
text(61,24,"██  █                   █  ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄")
text(61,25,"██  █████████████████████  ██▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀")
text(61,26,"██  █                   █  ██")
text(61,27,"██  █████████████████████  ██")
text(61,28,"██  █                   █  ██")
text(61,29,"██  █████████████████████  ██")
text(61,30,"██  █                   █  ██")
text(61,31,"██  █████████████████████  ██")
text(61,32,"██                         ██")
text(61,33,"██                         ██")
text(61,34,"██                         ██")
text(61,35,"██                         ██")
text(61,36,"██                         ██")
text(61,37,"██                         ██")
text(61,38,"██                         ██▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄")
text(61,39,"██                         ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀")
text(61,40,"██                         ← ← ← ← ← ← ← ← ← ←")
text(61,41,"██                         ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄")
text(61,42,"██                         ██▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀")
text(61,43,"██▄                       ▄██")
text(61,44," ██                       ██")
text(61,45," ▀██                     ██▀")
text(61,46,"  ▀██▄▄               ▄▄██▀")
text(61,47,"    ▀▀███▄▄▄▄▄▄▄▄▄▄▄███▀▀")
text(61,48,"        ▀▀▀▀▀▀▀▀▀▀▀▀")

background(0xAAAAAA)
foreground(0x000000)

text(70,4,"CONTROL RODS")
text(61,5,"INS+1  INS+5  INS+10")
text(61,8,"RET+1  RET+5  RET+10")

text(85,5,"ESTOP")
text(107,5,"LEVEL")
text(107,7,"QUIT")

text(94,5,"OVHEAT ESTOP")
text(94,8,"NOCOOL ESTOP")

text(95,13,"OUTFLOW")
text(92,14,"BUFFER")
text(99,14,"HOTΔ")

text(95,30,"INFLOW")
text(92,31,"BUFFER")
text(99,31,"COOLΔ")

text(69,20,"REACTOR  CORE")
text(71,21,"CORE HEAT:")
text(71,23,"HULL HEAT:")
text(71,25,"CORE FLUX:")
text(68,27,"COLD HEATEX LVL:")
text(69,29,"HOT HEATEX LVL:")
background(0x000000)

foreground(0xFFFFFF)
fill(107,6,5,1,"█")

--Outflow Buffer
fill(92,15,6,5,"█")

--CoolDelta
fill(99,15,7,1,"█")

--HotDelta

text(66,19,"┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃")
for y=22,30,2 do
    fill(66,y,19,1,"█")
end
text(66,32,"┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃")
foreground(0xAAAAAA)

foreground(0x000000)
background(0xFFFFFF)
text(83,22,"TU")
text(83,24,"TU")
foreground(0xFFFFFF)
background(0x000000)


local function update()
    rodLevel = call("getLevel")

    coreHeat, _ = call("getHeat")
    coreHeat = math.floor(coreHeat / (const.coreHeatCapacity / 10))

    for _, b in pairs(buttons) do
        drawButton(b, b.colorUp)
    end

    for j=math.floor(rodLevel/10),10 do
        fill(64+(j*2), 33, 1, 10, " ")
    end

    for j=1,math.floor(rodLevel/10) do
        fill(64+(j*2), 33, 1, 10, "┃")
    end

    fill(64+(math.ceil(rodLevel/10)*2), 33, 1, math.fmod(rodLevel,10), "┃")

    for j=0,20,2 do
        foreground(colorGradient[math.max(1, math.min(#colorGradient, coreHeat+1))])
        fill(65+j, 33, 1, 9, "█")
        foreground(0xAAAAAA)
    end

    background(0xFFFFFF)

    foreground(0xFFFFFF)
    for y = 22, 30, 2 do
        fill(66,y,19,1,"█")
    end

    fill(92,15,6,5,"█")
    fill(92,32,6,5,"█")

    fill(99,15,7,1,"█")
    fill(99,32,7,1,"█")

    prevCoolantFlow = coldCoolantLevel
    prevHotCoolantFlow = hotCoolantLevel

    fullCoreHeat, fullHullHeat = call("getHeat")
    coldCoolantLevel, _, hotCoolantLevel, maxHotCoolantLevel = call("getCoolantInfo")

    coldCoolantOutflow = coldCoolantLevel - prevCoolantFlow
    hotCoolantOutflow = hotCoolantLevel - prevHotCoolantFlow

    foreground(0xFF0099)
    fill(92,15+(5-math.floor(hotCoolantLevel/25600)),6,math.floor(hotCoolantLevel/25600), "█")
    foreground(0x000000)

    foreground(0x00FFFF)
    fill(92,32+(5-math.floor(coldCoolantLevel/25600)),6,math.floor(coldCoolantLevel/25600), "█")
    foreground(0x000000)

    text(66,22,tostring(fullCoreHeat)) -- What the heck? This is too declarative!
    text(66,24,tostring(fullHullHeat)) -- Will fix that garbage later :P
    text(66,26,tostring(call("getFlux")))
    text(66,28,tostring(coldCoolantLevel))
    text(66,30,tostring(hotCoolantLevel))

    text(99,15,tostring(hotCoolantOutflow))
    text(99,32,tostring(coldCoolantOutflow))

    text(107,6,"   ")
    text(107,6,tostring(call("getLevel")))

    background(0x000000)
    foreground(0xFFFFFF)

    if (coreHeatESTOP == true) and (fullCoreHeat) > const.fullCoreHeatMAX then
        setLevel(100)
    end

    if (coolantLossESTOP == true) and (coldCoolantLevel) < const.coldCoolantLevelMIN then
        setLevel(100)
    end

    if (hotCoolantESTOP == true) and (hotCoolantLevel) > const.hotCoolantLevelMAX * maxHotCoolantLevel then
        setLevel(100)
    end

end



local function refresh()
    while runSig do update();paint();sleep(0.25) end
end
local function input()
    while runSig do
        local kind,a,b,c=os.pullEventRaw()
        if kind=="terminate" then runSig=false;return
        elseif kind=="key" then
            if a==keys.q then runSig=false;return
            elseif a==keys.left then panX=panX-1
            elseif a==keys.right then panX=panX+1
            elseif a==keys.up then panY=panY-1
            elseif a==keys.down then panY=panY+1 end
            paint()
        elseif kind=="mouse_scroll" and not monitorName then panY=panY+a*3;paint()
        elseif kind=="mouse_click" and not monitorName then
            buttonPress(nil,nil,b+panX+59,c+panY+3);paint()
        elseif kind=="mouse_up" and not monitorName then
            buttonRelease();paint()
        elseif kind=="monitor_touch" and a==monitorName then
            buttonPress(nil,nil,b+panX+59,c+panY+3);buttonRelease();paint()
        elseif kind=="term_resize" or kind=="monitor_resize" then paint() end
    end
end
local ok,reason=pcall(function() parallel.waitForAny(refresh,input) end)
output.setBackgroundColor(colors.black);output.setTextColor(colors.white)
output.clear();output.setCursorPos(1,1)
if not ok and reason~="Terminated" then error(reason,0) end
