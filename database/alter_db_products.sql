create table products (
    id              integer primary key asc,
    product         text not null
);

alter table dictionary add column product integer references products(id);


