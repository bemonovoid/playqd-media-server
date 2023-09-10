package io.playqd.mediaserver.service.upnp.service.contentdirectory;

public enum UpnpClass {

    audioItem("object.item.audioItem"),

    image("object.item.imageItem"),

    item("object.item"),

    musicArtist("object.container.person.musicArtist"),

    musicAlbum("object.container.album.musicAlbum"),

    musicGenre ("object.container.genre.musicGenre"),

    musicTrack("object.item.audioItem.musicTrack"),

    photo("object.item.imageItem.photo"),

    playlistItem("object.item.playlistItem"),

    playlistContainer("object.container.playlistContainer"),

    storageFolder("object.container.storageFolder"),

    text("object.item.textItem");

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
