package io.playqd.mediaserver.persistence.jpa.dao;

public interface BrowsableObjectSetter {

    void setLocation(String location);

    void setChildrenCountTransient(long count);

    void setParentId(Long parentId);

    void setDcTitle(String dcTitle);
}
