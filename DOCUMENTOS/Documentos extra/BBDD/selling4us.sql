
CREATE TABLE USUARIO(
idUsuario int AUTO_INCREMENT,
Nombre varchar(50) NOT NULL,
Contraseña varchar(180) NOT NULL,
Fecha_nacimiento date NOT NULL,
Direccion varchar(50) NOT NULL,
PRIMARY KEY(idUsuario)
);

CREATE TABLE VALORACION(
idValoracion int AUTO_INCREMENT,
Rating int NOT NULL,
idUsuario int NOT NULL,
PRIMARY KEY(idValoracion),
FOREIGN KEY (idUsuario) REFERENCES USUARIO (idUsuario) ON DELETE CASCADE
);



CREATE TABLE CATEGORIA(
idCategoria int AUTO_INCREMENT,
Tipo varchar(20) NOT NULL,
PRIMARY KEY (idCategoria)
);

CREATE TABLE ANUNCIO(
idAnuncio int AUTO_INCREMENT,
Titulo varchar(30) NOT NULL,
Descripcion varchar(80) NOT NULL,
Precio DECIMAL(10,2) NOT NULL,
Direccion varchar(40) NOT NULL,
Imagen BLOB,
Estado varchar(20) NOT NULL,
Revision varchar(20) NOT NULL,
idPropietario int NOT NULL,
idCategoria int NOT NULL,
PRIMARY KEY (idAnuncio),
FOREIGN KEY (idPropietario) REFERENCES USUARIO (idUsuario) ON DELETE CASCADE,
FOREIGN KEY (idCategoria) REFERENCES CATEGORIA (idCategoria)
);

alter table ANUNCIO MODIFY Imagen BLOB(10485760); 

CREATE TABLE FAVORITOS(
idFavoritos int AUTO_INCREMENT,
idUsuario int NOT NULL,
idAnuncio int NOT NULL,
FOREIGN KEY (idUsuario) REFERENCES USUARIO (idUsuario) ON DELETE CASCADE,
FOREIGN KEY (idAnuncio) REFERENCES ANUNCIO (idAnuncio) ON DELETE CASCADE,
PRIMARY KEY(idFavoritos)
);

CREATE TABLE OFERTA(
idOferta int AUTO_INCREMENT,
idUsuario int NOT NULL,
idAnuncio int NOT NULL,
precio_oferta DECIMAL(10,2) NOT NULL,
revisado BOOLEAN NOT NULL,
FOREIGN KEY (idUsuario) REFERENCES USUARIO (idUsuario) ON DELETE CASCADE,
FOREIGN KEY (idAnuncio) REFERENCES ANUNCIO (idAnuncio) ON DELETE CASCADE,
PRIMARY KEY(idOferta)
);

CREATE TABLE COMPRA(
idCompra int AUTO_INCREMENT,
idUsuario int NOT NULL,
idAnuncio int NOT NULL UNIQUE,
fecha_commpra date NOT NULL,
precio_final DECIMAL(10,2) NOT NULL,
pagado BOOLEAN NOT NULL,
revisado BOOLEAN NOT NULL,
FOREIGN KEY (idUsuario) REFERENCES USUARIO (idUsuario) ON DELETE CASCADE,
FOREIGN KEY (idAnuncio) REFERENCES ANUNCIO (idAnuncio) ON DELETE CASCADE,
PRIMARY KEY(idCompra)
);


CREATE TABLE ROL(
idRol int AUTO_INCREMENT,
TipoRol varchar(20) NOT NULL,
PRIMARY KEY(idRol)
);

CREATE TABLE TIENE_ROL(
idTiene_rol int AUTO_INCREMENT,
idRol int NOT NULL,
idUsuario int NOT NULL,
PRIMARY KEY (idTiene_rol),
FOREIGN KEY (idRol) REFERENCES ROL (idRol),
FOREIGN KEY (idUsuario) REFERENCES USUARIO (idUsuario)
);

CREATE TABLE MODERADOR(
idModerador int AUTO_INCREMENT,
idUsuario int NOT NULL,
idCategoria INT NOT NULL,
PRIMARY KEY (idModerador),
FOREIGN KEY (idCategoria) REFERENCES CATEGORIA (idCategoria),
FOREIGN KEY (idUsuario) REFERENCES USUARIO (idUsuario)
);


CREATE TABLE CHAT(
idChat int AUTO_INCREMENT,
idAnuncio int NOT NULL,
idPropietario int NOT NULL,
idUuarioInteresado int NOT NULL,
fecha_chat date,
revisado_Propietario BOOLEAN NOT NULL,
revisado_Interesado BOOLEAN NOT NULL,
PRIMARY KEY(idChat),
FOREIGN KEY (idAnuncio) REFERENCES ANUNCIO (idAnuncio),
FOREIGN KEY (idPropietario) REFERENCES USUARIO (idUsuario) ON DELETE CASCADE,
FOREIGN KEY (idUuarioInteresado) REFERENCES USUARIO (idUsuario) ON DELETE CASCADE
);

CREATE TABLE MENSAJE(
idMensaje int AUTO_INCREMENT,
idChat int NOT NULL,
idRemitente int NOT NULL,
idDestinatario int NOT NULL,
mensaje varchar(50) NOT NULL,
fecha_hora DATETIME NOT NULL,
PRIMARY KEY (idMensaje),
FOREIGN KEY (idChat) REFERENCES CHAT(idChat),
FOREIGN KEY (idRemitente) REFERENCES USUARIO(idUsuario) ON DELETE CASCADE,
FOREIGN KEY (idDestinatario) REFERENCES USUARIO(idUsuario) ON DELETE CASCADE
); 

CREATE TABLE MENSAJERESTRICCION(
idMensajeRestriccion int AUTO_INCREMENT,
TipoMensaje varchar(40) NOT NULL,
Mensaje varchar(200) NOT NULL,
PRIMARY KEY(idMensajeRestriccion)
);


CREATE TABLE NOTIFICACION(
idNotificacion int AUTO_INCREMENT,
idUsuario int NOT NULL,
idAnuncio int NOT NULL,
idMensajeRestriccion int NOT NULL,
revisado BOOLEAN NOT NULL,
PRIMARY KEY(idNotificacion),
FOREIGN KEY (idUsuario) REFERENCES USUARIO(idUsuario) ON DELETE CASCADE,
FOREIGN KEY (idAnuncio) REFERENCES ANUNCIO(idAnuncio) ON DELETE CASCADE,
FOREIGN KEY (idMensajeRestriccion) REFERENCES MENSAJERESTRICCION(idMensajeRestriccion)
);

INSERT INTO `usuario` (`idUsuario`, `Nombre`, `Contraseña`, `Fecha_nacimiento`, `Direccion`) VALUES
(2, '1', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a', '2023-05-11', 'Cádiz');
INSERT INTO `usuario` (`idUsuario`, `Nombre`, `Contraseña`, `Fecha_nacimiento`, `Direccion`) VALUES
(4, 'pablo', 'e4b0da8a66b7dd2154d92df97da1c6cea8d421519b0a300df2be329e423f9675e39f6240733a7534df30ade501af974fb267e6e6a2768af68e55a3b47a31bf1b', '2023-05-05', 'Albacete');


INSERT INTO `rol` (`idRol`, `TipoRol`) VALUES
(1, 'Moderador'),
(2, 'Administrador'),
(3, 'Estándar');

INSERT INTO `tiene_rol` (`idTiene_rol`, `idRol`, `idUsuario`) VALUES
(2, 2, 2);
