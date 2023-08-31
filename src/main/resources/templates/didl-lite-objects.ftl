<#ftl output_format="XML">
<#list browseResponse.objects as obj>
    <#if obj.upnp.upnpClass.isContainer()>
        <container id="${obj.objectId}" childCount="${obj.childCount}" childContainerCount="${obj.childContainerCount}" parentID="${obj.parentObjectId}" searchable="${obj.searchable?then(1, 0)}" restricted="${obj.restricted?then(1, 0)}">
            <dc:title>${obj.dc.title}</dc:title>
            <upnp:class>${obj.upnp.upnpClass.getClassValue()}</upnp:class>
    <#if obj.resources??>
        <#list obj.resources as res>
            <res xmlns:dlna="urn:schemas-dlna-org:metadata-1-0/" protocolInfo="${res.protocolInfo}" size="${res.size}">${res.uri}</res>
        </#list>
    </#if>
        </container>
    <#else>
        <#if obj.upnp.upnpClass.name() == 'image'>
            <item id="${obj.objectId}" parentID="${obj.parentObjectId}" restricted="${obj.restricted?then(1, 0)}">
                <dc:title>${obj.dc.title}</dc:title>
                <#list obj.resources as res>
                    <#if res.isImage()>
                        <res xmlns:dlna="urn:schemas-dlna-org:metadata-1-0/" protocolInfo="${res.protocolInfo}" size="${res.size}">${res.uri}</res>
                    </#if>
                <upnp:class>${obj.upnp.upnpClass.getClassValue()}</upnp:class>
                </#list>
            </item>
        <#else>
            <item id="${obj.objectId}" parentID="${obj.parentObjectId}" restricted="${obj.restricted?then(1, 0)}">
                <dc:title>${obj.dc.title}</dc:title>
                <dc:creator>${obj.dc.creator}</dc:creator>
                <#list obj.resources as res>
                    <#if res.isImage()>
                        <res xmlns:dlna="urn:schemas-dlna-org:metadata-1-0/" protocolInfo="${res.protocolInfo}" size="${res.size}">${res.uri}</res>
                    <#else>
                        <res xmlns:dlna="urn:schemas-dlna-org:metadata-1-0/" protocolInfo="${res.protocolInfo}" bitrate="${res.bitRate}" bitsPerSample="${res.bitsPerSample}" sampleFrequency="${res.sampleFrequency}" size="${res.size}" duration="${res.duration}">${res.uri}</res>
                    </#if>
                </#list>
                <upnp:artist>${obj.upnp.artist}</upnp:artist>
                <upnp:album>${obj.upnp.album}</upnp:album>
                <#if obj.upnp.genre??>
                    <upnp:genre>${obj.upnp.genre}</upnp:genre>
                </#if>
                <#if obj.upnp.originalTrackNumber??>
                    <upnp:originalTrackNumber>${obj.upnp.originalTrackNumber}</upnp:originalTrackNumber>
                </#if>
                <#if obj.upnp.lastPlaybackTime??>
                    <upnp:lastPlaybackTime>${obj.upnp.lastPlaybackTime}</upnp:lastPlaybackTime>
                </#if>
                <upnp:playbackCount>${obj.upnp.playbackCount}</upnp:playbackCount>
                <upnp:class>${obj.upnp.upnpClass.getClassValue()}</upnp:class>
                <#if obj.upnp.albumArtURI??>
                    <upnp:albumArtURI xmlns:dlna="urn:schemas-dlna-org:metadata-1-0/">${obj.upnp.albumArtURI}</upnp:albumArtURI>
                </#if>
            </item>
        </#if>

    </#if>
</#list>