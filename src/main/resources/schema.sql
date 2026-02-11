-- Aquí irán las tablas para la base de datos

-- Este es el ejemplo de inicio de la plantilla (ver como referencia)
drop table Carreras;
create table Carreras (id int primary key not null, inicio date not null, fin date not null, fecha date not null, descr varchar(32), check(inicio<=fin), check(fin<fecha));