<?xml version="1.0" encoding='UTF-8'?>
<PageMeta caption="批量新增签卡登记" componentId="annoyuicomponnet" controllerClazz="nc.bs.hrsms.ta.SignReg.ctrl.BatchSignRegWinController" foldPath="/sync/hrss/ta/html/nodes/BatchSignReg/" id="BatchSignReg" sourcePackage="ta/src/public/">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="true" id="main" refId="main">
        </Widget>
    </Widgets>
    <Attributes>
        <Attribute>
            <Key>$QueryTemplate</Key>
            <Value>
            </Value>
            <Desc>
            </Desc>
        </Attribute>
    </Attributes>
    <Events>
        <Event async="true" jsEventClaszz="nc.uap.lfw.core.event.conf.PageListener" methodName="sysWindowClosed" name="onClosed" onserver="true">
            <SubmitRule cardSubmit="false" panelSubmit="false" tabSubmit="false">
            </SubmitRule>
            <Params>
                <Param>
                    <Name>event</Name>
                    <Value>
                    </Value>
                    <Desc>                        <![CDATA[nc.uap.lfw.core.event.PageEvent]]>
                    </Desc>
                </Param>
            </Params>
            <Action>
            </Action>
        </Event>
    </Events>
    <PlugoutDescs>
    </PlugoutDescs>
    <PluginDescs>
    </PluginDescs>
</PageMeta>