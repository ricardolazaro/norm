# --- !Ups

CREATE TABLE PUBLIC.Products (
    id          integer not null identity,
    name        varchar(25),
    description varchar(100),
    price       decimal,
    taxRange    integer,
    inStock     boolean,
    PRIMARY KEY (id),
);

# --- !Downs

DROP TABLE Products;