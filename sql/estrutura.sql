-- --------------------------------------------------------
-- Estrutura da base de dados para gestao_redes
-- --------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `gestao_redes` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci;
USE `gestao_redes`;

-- Estrutura para tabela clientes
CREATE TABLE IF NOT EXISTS `clientes` (
  `id_cliente` int(11) NOT NULL AUTO_INCREMENT,
  `nome_cliente` varchar(100) NOT NULL,
  `contacto` varchar(50) DEFAULT NULL,
  `localidade` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `nome_cliente` (`nome_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Estrutura para tabela niveis_acesso
CREATE TABLE IF NOT EXISTS `niveis_acesso` (
  `id_nivel` int(11) NOT NULL AUTO_INCREMENT,
  `nome_nivel` varchar(30) NOT NULL,
  PRIMARY KEY (`id_nivel`),
  UNIQUE KEY `nome_nivel` (`nome_nivel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Estrutura para tabela nodos
CREATE TABLE IF NOT EXISTS `nodos` (
  `id_nodo` int(11) NOT NULL AUTO_INCREMENT,
  `nome_nodo` varchar(50) NOT NULL,
  `tipo` enum('Router','Switch','AP') NOT NULL,
  `total_portas` int(11) NOT NULL,
  `id_pai` int(11) DEFAULT NULL,
  `ip_gestao` varchar(50) DEFAULT NULL,
  `id_cliente` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_nodo`),
  KEY `id_pai` (`id_pai`),
  KEY `fk_nodos_clientes` (`id_cliente`),
  CONSTRAINT `fk_nodos_clientes` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `nodos_ibfk_1` FOREIGN KEY (`id_pai`) REFERENCES `nodos` (`id_nodo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Estrutura para tabela utilizadores
CREATE TABLE IF NOT EXISTS `utilizadores` (
  `id_user` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `id_nivel` int(11) DEFAULT NULL,
  `id_cliente_associado` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `username` (`username`),
  KEY `id_nivel` (`id_nivel`),
  KEY `id_cliente_associado` (`id_cliente_associado`),
  CONSTRAINT `utilizadores_ibfk_1` FOREIGN KEY (`id_nivel`) REFERENCES `niveis_acesso` (`id_nivel`),
  CONSTRAINT `utilizadores_ibfk_2` FOREIGN KEY (`id_cliente_associado`) REFERENCES `clientes` (`id_cliente`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Estrutura para tabela equipamentos_ligados
CREATE TABLE IF NOT EXISTS `equipamentos_ligados` (
  `id_equip` int(11) NOT NULL AUTO_INCREMENT,
  `nome_dispositivo` varchar(50) NOT NULL,
  `mac_address` varchar(17) NOT NULL,
  `sala` varchar(50) DEFAULT NULL,
  `id_nodo` int(11) DEFAULT NULL,
  `id_cliente` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_equip`),
  UNIQUE KEY `mac_address` (`mac_address`),
  KEY `id_cliente` (`id_cliente`),
  KEY `fk_equipamentos_nodos` (`id_nodo`),
  CONSTRAINT `equipamentos_ligados_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`) ON DELETE CASCADE,
  CONSTRAINT `fk_equipamentos_nodos` FOREIGN KEY (`id_nodo`) REFERENCES `nodos` (`id_nodo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Inserção de dados iniciais
INSERT IGNORE INTO `niveis_acesso` (`id_nivel`, `nome_nivel`) VALUES 
(1, 'Administrador'), 
(2, 'Colaborador'), 
(3, 'Cliente'), 
(4, 'Convidado');

INSERT IGNORE INTO `utilizadores` (`username`, `password`, `id_nivel`) VALUES 
('admin', 'admin123', 1);
