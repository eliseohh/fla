create table product
(
    id              bigint auto_increment,
    sku             varchar(50),
    name            varchar(50),
    brand           varchar(50),
    size            varchar(50),
    price           number,
    principal_image varchar,
    other_images     varchar
);