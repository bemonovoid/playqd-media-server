<#ftl output_format="XML">
<Fault>
    <faultcode>s:Client</faultcode>
    <faultstring>UPnPError</faultstring>
    <detail>
        <UPnPError xmlns="urn:schemas-upnp-org:control-1-0">
            <errorCode>${actionError.errorCode}</errorCode>
            <errorDescription>${actionError.errorDescription}</errorDescription>
        </UPnPError>
    </detail>
</Fault>