create table if not exists audio_file
(
    id                      bigint auto_increment primary key,

    name                    VARCHAR(255) not null,
    size                    bigint       not null,
    location                varchar(255) not null,
    extension               varchar(255) not null,
    file_last_scanned_date  datetime(6)  not null,
    file_last_modified_date datetime(6)  null,

    format                  varchar(100) null,
    bit_rate                varchar(100) null,
    lossless                boolean default false,
    channels                varchar(100) null,
    sample_rate             varchar(100) null,
    encoding_type           varchar(100) null,
    bits_per_sample         int          null,

    artist_name             varchar(512) null,
    artist_country          varchar(255) null,

    album_name              varchar(512) null,
    album_release_date      varchar(255) null,

    track_name              varchar(255) null,
    track_number            varchar(255) null,
    track_length            int          null,

    comment                 longtext     null,
    lyrics                  longtext     null,
    genre                   varchar(255) null,

    artwork_embedded        boolean default false,

    mb_track_id             varchar(255) null,
    mb_artist_id            varchar(255) null,
    mb_release_type         varchar(255) null,
    mb_release_group_id     varchar(255) null,

    created_by              varchar(255) not null,
    created_date            datetime(6)  not null,
    last_modified_by        varchar(255) null,
    last_modified_date      datetime(6)  null
);

create index audio_file_idx
    on audio_file (location);
