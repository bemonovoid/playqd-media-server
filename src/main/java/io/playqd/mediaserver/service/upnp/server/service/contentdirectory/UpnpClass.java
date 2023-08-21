package io.playqd.mediaserver.service.upnp.server.service.contentdirectory;

public enum UpnpClass {

    audioItem("object.item.audioItem"),

    image("object.item.imageItem"),

    musicArtist("object.container.person.musicArtist"),

    musicAlbum("object.container.album.musicAlbum"),

    musicGenre ("object.container.genre.musicGenre"),

    musicTrack("object.item.audioItem.musicTrack"),

    playlistItem("object.item.playlistItem"),

    playlistContainer("object.container.playlistContainer"),

    storageFolder("object.container.storageFolder");

    private final String classValue;

    UpnpClass(String classValue) {
        this.classValue = classValue;
    }

    public String getClassValue() {
        return classValue;
    }

    public boolean isContainer() {
        return getClassValue().startsWith("object.container");
    }

    public boolean isItem() {
        return getClassValue().startsWith("object.item");
    }
}
