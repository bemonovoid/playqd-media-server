package io.playqd.mediaserver.service.upnp.server;

import lombok.extern.slf4j.Slf4j;
import org.jupnp.binding.annotations.*;
import org.jupnp.model.profile.RemoteClientInfo;
import org.jupnp.model.types.UnsignedIntegerFourBytes;
import org.jupnp.model.types.csv.CSV;
import org.jupnp.model.types.csv.CSVString;
import org.jupnp.support.contentdirectory.ContentDirectoryException;
import org.jupnp.support.model.BrowseFlag;
import org.jupnp.support.model.BrowseResult;

import java.util.List;

@Slf4j
@UpnpService(
        serviceId = @UpnpServiceId("ContentDirectory"),
        serviceType = @UpnpServiceType(value = "ContentDirectory", version = 1)
)
@UpnpStateVariables({
        @UpnpStateVariable(
                name = "A_ARG_TYPE_ObjectID",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_Result",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_BrowseFlag",
                sendEvents = false,
                datatype = "string",
                allowedValuesEnum = BrowseFlag.class),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_Filter",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_SortCriteria",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_Index",
                sendEvents = false,
                datatype = "ui4"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_Count",
                sendEvents = false,
                datatype = "ui4"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_UpdateID",
                sendEvents = false,
                datatype = "ui4"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_URI",
                sendEvents = false,
                datatype = "uri"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_SearchCriteria",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_PosSecond",
                sendEvents = false,
                datatype = "ui4"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_CategoryType",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_RID",
                sendEvents = false,
                datatype = "string")
})
public class UpnpContentDirectoryServiceImpl {

    private static final List<String> CAPS_SEARCH = List.of();
    private static final List<String> CAPS_SORT = List.of("upnp:class", "dc:title", "dc:creator", "upnp:artist", "upnp:album", "upnp:genre");
    private static final String CRLF = "\r\n";

    @UpnpStateVariable(sendEvents = false)
    private final CSV<String> searchCapabilities = new CSVString();

    @UpnpStateVariable(sendEvents = false)
    private final CSV<String> sortCapabilities = new CSVString();

    @UpnpStateVariable(
            sendEvents = true,
            defaultValue = "0",
            eventMaximumRateMilliseconds = 200
    )
    private UnsignedIntegerFourBytes systemUpdateID = new UnsignedIntegerFourBytes(0);

    @UpnpAction(out = @UpnpOutputArgument(name = "SearchCaps"))
    public CSV<String> getSearchCapabilities() {
        return searchCapabilities;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "SortCaps"))
    public CSV<String> getSortCapabilities() {
        return sortCapabilities;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "Id"))
    public synchronized UnsignedIntegerFourBytes getSystemUpdateID() {
        throw new RuntimeException("Not supported");
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Result",
                    stateVariable = "A_ARG_TYPE_Result",
                    getterName = "getResult"),
            @UpnpOutputArgument(name = "NumberReturned",
                    stateVariable = "A_ARG_TYPE_Count",
                    getterName = "getCount"),
            @UpnpOutputArgument(name = "TotalMatches",
                    stateVariable = "A_ARG_TYPE_Count",
                    getterName = "getTotalMatches"),
            @UpnpOutputArgument(name = "UpdateID",
                    stateVariable = "A_ARG_TYPE_UpdateID",
                    getterName = "getContainerUpdateID")
    })
    public BrowseResult browse(
            @UpnpInputArgument(name = "ObjectID", aliases = "ContainerID") String objectId,
            @UpnpInputArgument(name = "BrowseFlag") String browseFlag,
            @UpnpInputArgument(name = "Filter") String filter,
            @UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") UnsignedIntegerFourBytes firstResult,
            @UpnpInputArgument(name = "RequestedCount", stateVariable = "A_ARG_TYPE_Count") UnsignedIntegerFourBytes maxResults,
            @UpnpInputArgument(name = "SortCriteria") String orderBy,
            RemoteClientInfo remoteClientInfo
    ) throws ContentDirectoryException {
        throw new RuntimeException("Not supported");
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Result",
                    stateVariable = "A_ARG_TYPE_Result",
                    getterName = "getResult"),
            @UpnpOutputArgument(name = "NumberReturned",
                    stateVariable = "A_ARG_TYPE_Count",
                    getterName = "getCount"),
            @UpnpOutputArgument(name = "TotalMatches",
                    stateVariable = "A_ARG_TYPE_Count",
                    getterName = "getTotalMatches"),
            @UpnpOutputArgument(name = "UpdateID",
                    stateVariable = "A_ARG_TYPE_UpdateID",
                    getterName = "getContainerUpdateID")
    })
    public BrowseResult search(
            @UpnpInputArgument(name = "ContainerID", stateVariable = "A_ARG_TYPE_ObjectID") String containerId,
            @UpnpInputArgument(name = "SearchCriteria") String searchCriteria,
            @UpnpInputArgument(name = "Filter") String filter,
            @UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") UnsignedIntegerFourBytes startingIndex,
            @UpnpInputArgument(name = "RequestedCount", stateVariable = "A_ARG_TYPE_Count") UnsignedIntegerFourBytes requestedCount,
            @UpnpInputArgument(name = "SortCriteria") String orderBy,
            RemoteClientInfo remoteClientInfo
    ) throws ContentDirectoryException {
        throw new RuntimeException("Not supported");
    }
}
