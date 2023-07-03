-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 15-06-2023 a las 03:06:31
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `selling4us`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `anuncio`
--

CREATE TABLE `anuncio` (
  `idAnuncio` int(11) NOT NULL,
  `Titulo` varchar(30) NOT NULL,
  `Descripcion` varchar(80) NOT NULL,
  `Precio` decimal(10,2) NOT NULL,
  `Direccion` varchar(40) NOT NULL,
  `Imagen` mediumblob DEFAULT NULL,
  `Estado` varchar(20) NOT NULL,
  `Revision` varchar(20) NOT NULL,
  `idPropietario` int(11) NOT NULL,
  `idCategoria` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `anuncio`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria`
--

CREATE TABLE `categoria` (
  `idCategoria` int(11) NOT NULL,
  `Tipo` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `categoria`
--

INSERT INTO `categoria` (`idCategoria`, `Tipo`) VALUES
(1, 'Ropa'),
(2, 'Móviles'),
(3, 'Deporte'),
(4, 'Consolas'),
(5, 'VideoJuegos'),
(6, 'Hogar'),
(7, 'Tecnología'),
(8, 'Coleccionismo'),
(9, 'Otros');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `chat`
--

CREATE TABLE `chat` (
  `idChat` int(11) NOT NULL,
  `idAnuncio` int(11) NOT NULL,
  `idPropietario` int(11) NOT NULL,
  `idUuarioInteresado` int(11) NOT NULL,
  `fecha_chat` date DEFAULT NULL,
  `revisado_Propietario` tinyint(1) NOT NULL,
  `revisado_Interesado` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `chat`
--


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `compra`
--

CREATE TABLE `compra` (
  `idCompra` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL,
  `idAnuncio` int(11) NOT NULL,
  `fecha_commpra` date NOT NULL,
  `precio_final` decimal(10,2) NOT NULL,
  `pagado` tinyint(1) NOT NULL,
  `revisado` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `compra`
--


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `favoritos`
--

CREATE TABLE `favoritos` (
  `idFavoritos` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL,
  `idAnuncio` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `favoritos`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mensaje`
--

CREATE TABLE `mensaje` (
  `idMensaje` int(11) NOT NULL,
  `idChat` int(11) NOT NULL,
  `idRemitente` int(11) NOT NULL,
  `idDestinatario` int(11) NOT NULL,
  `mensaje` varchar(50) NOT NULL,
  `fecha_hora` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `mensaje`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mensajerestriccion`
--

CREATE TABLE `mensajerestriccion` (
  `idMensajeRestriccion` int(11) NOT NULL,
  `TipoMensaje` varchar(40) NOT NULL,
  `Mensaje` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `mensajerestriccion`
--

INSERT INTO `mensajerestriccion` (`idMensajeRestriccion`, `TipoMensaje`, `Mensaje`) VALUES
(1, 'Productos Prohibidos', 'es un producto prohibido'),
(2, 'Productos Insalubres', 'Es un Productos Insalubres'),
(3, 'Falsificación', 'El producto es una falsicicación no autorizada');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `moderador`
--

CREATE TABLE `moderador` (
  `idModerador` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL,
  `idCategoria` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `moderador`
--


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `notificacion`
--

CREATE TABLE `notificacion` (
  `idNotificacion` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL,
  `idAnuncio` int(11) NOT NULL,
  `idMensajeRestriccion` int(11) NOT NULL,
  `revisado` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `oferta`
--

CREATE TABLE `oferta` (
  `idOferta` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL,
  `idAnuncio` int(11) NOT NULL,
  `precio_oferta` decimal(10,2) NOT NULL,
  `revisado` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `oferta`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `idRol` int(11) NOT NULL,
  `TipoRol` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`idRol`, `TipoRol`) VALUES
(1, 'Moderador'),
(2, 'Administrador'),
(3, 'Estándar');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tiene_rol`
--

CREATE TABLE `tiene_rol` (
  `idTiene_rol` int(11) NOT NULL,
  `idRol` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tiene_rol`
--

INSERT INTO `tiene_rol` (`idTiene_rol`, `idRol`, `idUsuario`) VALUES
(2, 2, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `idUsuario` int(11) NOT NULL,
  `Nombre` varchar(50) NOT NULL,
  `Contraseña` varchar(180) NOT NULL,
  `Fecha_nacimiento` date NOT NULL,
  `Direccion` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`idUsuario`, `Nombre`, `Contraseña`, `Fecha_nacimiento`, `Direccion`) VALUES
(2, '1', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a', '2023-05-11', 'Cádiz');
INSERT INTO `usuario` (`idUsuario`, `Nombre`, `Contraseña`, `Fecha_nacimiento`, `Direccion`) VALUES
(4, 'pablo', 'e4b0da8a66b7dd2154d92df97da1c6cea8d421519b0a300df2be329e423f9675e39f6240733a7534df30ade501af974fb267e6e6a2768af68e55a3b47a31bf1b', '2023-05-05', 'Albacete');



INSERT INTO `tiene_rol` (`idTiene_rol`, `idRol`, `idUsuario`) VALUES
(3, 1, 4);
INSERT INTO `moderador` (`idModerador`, `idUsuario`, `idCategoria`) VALUES
(1, 4, 3);
-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `valoracion`
--

CREATE TABLE `valoracion` (
  `idValoracion` int(11) NOT NULL,
  `Rating` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `valoracion`
--


--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `anuncio`
--
ALTER TABLE `anuncio`
  ADD PRIMARY KEY (`idAnuncio`),
  ADD KEY `idPropietario` (`idPropietario`),
  ADD KEY `idCategoria` (`idCategoria`);

--
-- Indices de la tabla `categoria`
--
ALTER TABLE `categoria`
  ADD PRIMARY KEY (`idCategoria`);

--
-- Indices de la tabla `chat`
--
ALTER TABLE `chat`
  ADD PRIMARY KEY (`idChat`),
  ADD KEY `idAnuncio` (`idAnuncio`),
  ADD KEY `idPropietario` (`idPropietario`),
  ADD KEY `idUuarioInteresado` (`idUuarioInteresado`);

--
-- Indices de la tabla `compra`
--
ALTER TABLE `compra`
  ADD PRIMARY KEY (`idCompra`),
  ADD UNIQUE KEY `idAnuncio` (`idAnuncio`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `favoritos`
--
ALTER TABLE `favoritos`
  ADD PRIMARY KEY (`idFavoritos`),
  ADD KEY `idUsuario` (`idUsuario`),
  ADD KEY `idAnuncio` (`idAnuncio`);

--
-- Indices de la tabla `mensaje`
--
ALTER TABLE `mensaje`
  ADD PRIMARY KEY (`idMensaje`),
  ADD KEY `idChat` (`idChat`),
  ADD KEY `idRemitente` (`idRemitente`),
  ADD KEY `idDestinatario` (`idDestinatario`);

--
-- Indices de la tabla `mensajerestriccion`
--
ALTER TABLE `mensajerestriccion`
  ADD PRIMARY KEY (`idMensajeRestriccion`);

--
-- Indices de la tabla `moderador`
--
ALTER TABLE `moderador`
  ADD PRIMARY KEY (`idModerador`),
  ADD KEY `idCategoria` (`idCategoria`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD PRIMARY KEY (`idNotificacion`),
  ADD KEY `idUsuario` (`idUsuario`),
  ADD KEY `idAnuncio` (`idAnuncio`),
  ADD KEY `idMensajeRestriccion` (`idMensajeRestriccion`);

--
-- Indices de la tabla `oferta`
--
ALTER TABLE `oferta`
  ADD PRIMARY KEY (`idOferta`),
  ADD KEY `idUsuario` (`idUsuario`),
  ADD KEY `idAnuncio` (`idAnuncio`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`idRol`);

--
-- Indices de la tabla `tiene_rol`
--
ALTER TABLE `tiene_rol`
  ADD PRIMARY KEY (`idTiene_rol`),
  ADD KEY `idRol` (`idRol`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`idUsuario`);

--
-- Indices de la tabla `valoracion`
--
ALTER TABLE `valoracion`
  ADD PRIMARY KEY (`idValoracion`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `anuncio`
--
ALTER TABLE `anuncio`
  MODIFY `idAnuncio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT de la tabla `categoria`
--
ALTER TABLE `categoria`
  MODIFY `idCategoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `chat`
--
ALTER TABLE `chat`
  MODIFY `idChat` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `compra`
--
ALTER TABLE `compra`
  MODIFY `idCompra` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- AUTO_INCREMENT de la tabla `favoritos`
--
ALTER TABLE `favoritos`
  MODIFY `idFavoritos` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de la tabla `mensaje`
--
ALTER TABLE `mensaje`
  MODIFY `idMensaje` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=103;

--
-- AUTO_INCREMENT de la tabla `mensajerestriccion`
--
ALTER TABLE `mensajerestriccion`
  MODIFY `idMensajeRestriccion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `moderador`
--
ALTER TABLE `moderador`
  MODIFY `idModerador` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  MODIFY `idNotificacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT de la tabla `oferta`
--
ALTER TABLE `oferta`
  MODIFY `idOferta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;

--
-- AUTO_INCREMENT de la tabla `rol`
--
ALTER TABLE `rol`
  MODIFY `idRol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tiene_rol`
--
ALTER TABLE `tiene_rol`
  MODIFY `idTiene_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `idUsuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `valoracion`
--
ALTER TABLE `valoracion`
  MODIFY `idValoracion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `anuncio`
--
ALTER TABLE `anuncio`
  ADD CONSTRAINT `anuncio_ibfk_1` FOREIGN KEY (`idPropietario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `anuncio_ibfk_2` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`idCategoria`);

--
-- Filtros para la tabla `chat`
--
ALTER TABLE `chat`
  ADD CONSTRAINT `chat_ibfk_1` FOREIGN KEY (`idAnuncio`) REFERENCES `anuncio` (`idAnuncio`),
  ADD CONSTRAINT `chat_ibfk_2` FOREIGN KEY (`idPropietario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `chat_ibfk_3` FOREIGN KEY (`idUuarioInteresado`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `compra`
--
ALTER TABLE `compra`
  ADD CONSTRAINT `compra_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `compra_ibfk_2` FOREIGN KEY (`idAnuncio`) REFERENCES `anuncio` (`idAnuncio`) ON DELETE CASCADE;

--
-- Filtros para la tabla `favoritos`
--
ALTER TABLE `favoritos`
  ADD CONSTRAINT `favoritos_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `favoritos_ibfk_2` FOREIGN KEY (`idAnuncio`) REFERENCES `anuncio` (`idAnuncio`) ON DELETE CASCADE;

--
-- Filtros para la tabla `mensaje`
--
ALTER TABLE `mensaje`
  ADD CONSTRAINT `mensaje_ibfk_1` FOREIGN KEY (`idChat`) REFERENCES `chat` (`idChat`),
  ADD CONSTRAINT `mensaje_ibfk_2` FOREIGN KEY (`idRemitente`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `mensaje_ibfk_3` FOREIGN KEY (`idDestinatario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `moderador`
--
ALTER TABLE `moderador`
  ADD CONSTRAINT `moderador_ibfk_1` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`idCategoria`),
  ADD CONSTRAINT `moderador_ibfk_2` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD CONSTRAINT `notificacion_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `notificacion_ibfk_2` FOREIGN KEY (`idAnuncio`) REFERENCES `anuncio` (`idAnuncio`) ON DELETE CASCADE,
  ADD CONSTRAINT `notificacion_ibfk_3` FOREIGN KEY (`idMensajeRestriccion`) REFERENCES `mensajerestriccion` (`idMensajeRestriccion`);

--
-- Filtros para la tabla `oferta`
--
ALTER TABLE `oferta`
  ADD CONSTRAINT `oferta_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `oferta_ibfk_2` FOREIGN KEY (`idAnuncio`) REFERENCES `anuncio` (`idAnuncio`) ON DELETE CASCADE;

--
-- Filtros para la tabla `tiene_rol`
--
ALTER TABLE `tiene_rol`
  ADD CONSTRAINT `tiene_rol_ibfk_1` FOREIGN KEY (`idRol`) REFERENCES `rol` (`idRol`),
  ADD CONSTRAINT `tiene_rol_ibfk_2` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `valoracion`
--
ALTER TABLE `valoracion`
  ADD CONSTRAINT `valoracion_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
