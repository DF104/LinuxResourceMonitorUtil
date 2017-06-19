create table if not exists ServerInfo 
(
id integer primary key autoincrement, 
host text,
port text,
username text,
password text,
mark text,
time text,
type text
);

create table if not exists CommonInfo
(
info_id integer primary key autoincrement,
info_type text,
info_name text,
info_detail text,
info_tag text,
join_time text,
join_user_name text
);