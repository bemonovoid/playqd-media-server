<#ftl output_format="XML">
<u:BrowseResponse xmlns:u="urn:schemas-upnp-org:service:ContentDirectory:1">
    <Result>
        <![CDATA[
            <DIDL-Lite xmlns="urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/" xmlns:dc="http://purl.org/dc/elements/1.1/"
                       xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/" xmlns:sec="http://www.sec.co.kr/"
                       xmlns:pv="http://www.pv.com/pvns/">
                <#include "didl-lite-objects.ftl">
            </DIDL-Lite>
        ]]>
    </Result>
    <NumberReturned>${browseResponse.numberReturned}</NumberReturned>
    <TotalMatches>${browseResponse.totalMatches}</TotalMatches>
    <UpdateID>${browseResponse.updateId}</UpdateID>
</u:BrowseResponse>